package com.lancas.vswap.sandbox.schedule;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.ballistics.helper.BallisticsMath;
import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.api.ISavedObject;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxExpireTicker;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxTween;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.ExpireTickerData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.TweenData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.subproject.sandbox.thread.impl.client.SandBoxClientThread;
import com.lancas.vswap.subproject.sandbox.thread.schedule.IScheduleData;
import com.lancas.vswap.subproject.sandbox.thread.schedule.ScheduleState;
import com.lancas.vswap.subproject.sandbox.thread.schedule.impl.MultiTimesScheduleData;
import com.lancas.vswap.subproject.sandbox.thread.schedule.impl.MultiTimesScheduler;
import com.lancas.vswap.util.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.Objects;
import java.util.UUID;

public class ClientShardShipScheduler extends MultiTimesScheduler<SandBoxClientThread, ClientShardShipScheduler.ShardShipScheduleData> {

    public static class ShardShipScheduleData extends MultiTimesScheduleData implements ISavedObject<ShardShipScheduleData> {
        private int delayTicks;
        private final Vector3d spawnPos = new Vector3d();
        private BlockState shardBlock;
        private final Vector3d expCenterToBlockDir = new Vector3d();
        private final Vector3d projectileVelWhenExp = new Vector3d();

        private ShardShipScheduleData() {}
        public ShardShipScheduleData(int inDelayTicks, Vector3dc inSpawnPos, BlockState inShardState, Vector3dc inExpCenterToBlockDir, Vector3dc inProjectileVelWhenExp) {
            delayTicks = inDelayTicks;
            spawnPos.set(inSpawnPos);
            shardBlock = inShardState;
            expCenterToBlockDir.set(inExpCenterToBlockDir);
            projectileVelWhenExp.set(inProjectileVelWhenExp);
        }

        @Override
        public Class<?> getSchedulerType() { return ClientShardShipScheduler.class; }


        @Override
        public CompoundTag saved() {
            return new NbtBuilder()
                .putEnum("state", state)
                .putInt("scheduled_times", scheduledTimes.get())
                .putInt("delay_ticks", delayTicks)
                .putVector3d("spawn_pos", spawnPos)
                .putBlockState("shard_block", shardBlock)
                .putVector3d("exp_center_to_block_dir", expCenterToBlockDir)
                .putVector3d("projectile_vel_when_exp", projectileVelWhenExp)
                .get();
        }
        @Override
        public ShardShipScheduleData load(CompoundTag tag) {
            NbtBuilder.modify(tag)
                .readEnumDo("state", ScheduleState.class, s -> state = s)
                .readIntDo("scheduled_times", scheduledTimes::set)
                .readVector3d("spawn_pos", spawnPos)
                .readIntDo("delay_ticks", v -> delayTicks = v)
                .readBlockStateDo("shard_block", s -> shardBlock = s)
                .readVector3d("exp_center_to_block_dir", expCenterToBlockDir)
                .readVector3d("projectile_vel_when_exp", projectileVelWhenExp);
            return this;
        }
    }


    @Override
    protected void handleNoWorryTimes(SandBoxClientThread thread, ShardShipScheduleData data) {
        EzDebug.highlight("schedule data times:" + data.getScheduledTimes());
        if (data.getScheduledTimes() < data.delayTicks) return;

        ClientLevel level = thread.getLevel();

        //simply treate inv dir of proj vel is ground normal
        Vector3d groundNormal = data.projectileVelWhenExp.normalize(-1, new Vector3d());
        if (!groundNormal.isFinite())
            groundNormal.set(0, 1, 0);  //default ground normal for safe

        Vector3d velDirAlongVel = new Vector3d();
        Vector3d velDirVertToVel = new Vector3d();
        //沿着速度方向对距离差进行正交分解
        MathUtil.orthogonality(data.expCenterToBlockDir, data.projectileVelWhenExp, velDirAlongVel, velDirVertToVel);
        velDirAlongVel.normalize(); velDirVertToVel.normalize();

        if (!velDirAlongVel.isFinite() || !velDirVertToVel.isFinite()) {  //cancel ship if the vel is not finite
            EzDebug.warn("can't spawn shard ship because invalid vel:" + velDirAlongVel + "," + velDirVertToVel);
            data.setState(ScheduleState.CANCELED);
            return;
        }
                /*EzDebug.log(
                    "block in level:" + StrUtil.getBlockName(level.getBlockState())
                );*/
        //EzDebug.log("ship blocks:");
        //EzDebug.logs(ship.getCluster().allBlockStates(), StrUtil::getBlockName);
        Vector3d shardVel =
            velDirAlongVel.mul(RandUtil.nextG(15, 4), new Vector3d())
                .add(velDirVertToVel.mul(RandUtil.nextG(3, 1.5), new Vector3d()));

        //检测可能的碰撞，如果有碰撞则以炮弹速度反方向为法向量计算弹射速度
        ClipContext ctx = new ClipContext(
            JomlUtil.v3(data.spawnPos),
            JomlUtil.v3(shardVel.mul(0.5, new Vector3d()).add(data.spawnPos)),  //计算0.5s后是否有碰撞
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            null
        );
        BlockHitResult result = level.clip(ctx);
        if (result.getType() != HitResult.Type.MISS) {
            //计算反弹的速度
            shardVel.set(BallisticsMath.getBouncedVelNoDecrease(shardVel, groundNormal));
            EzDebug.warn(
                "worldCenter:" + data.spawnPos +
                    "is inside?:" + result.isInside() +
                    " at " + StrUtil.poslike(result.getBlockPos()) +
                    ", the state in level at hitpos:" + StrUtil.getBlockName(level.getBlockState(result.getBlockPos()))
            );
        }

        RigidbodyData rigidbodyData = RigidbodyData.createEarthGravity(new TransformPrimitive().setPosition(data.spawnPos));
        rigidbodyData.setOmega(RandUtil.onRandSphere(1, 8));
        rigidbodyData.setVelocity(shardVel);

        SandBoxClientShip ship = new SandBoxClientShip(
            UUID.randomUUID(),
            rigidbodyData,
            BlockClusterData.BlockAtCenter(data.shardBlock)
        );
        ship.addBehaviour(new SandBoxExpireTicker(), new ExpireTickerData(150));
        ship.addBehaviour(new SandBoxTween(), new TweenData(
            TweenData.TweenFunction.Scale,
            2.5
        ));

        thread.getSandBoxWorld().addClientShip(ship);
        data.setState(ScheduleState.SUCCESS);
    }

    @Override
    public Class<? extends IScheduleData> getDataType() { return ShardShipScheduleData.class; }


    public static void register() {
        SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;
        Objects.requireNonNull(clientWorld.getSpecificThread(SandBoxClientThread.class))
            .register(new ClientShardShipScheduler());
    }
}
