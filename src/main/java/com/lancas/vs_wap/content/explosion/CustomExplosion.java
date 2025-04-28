package com.lancas.vs_wap.content.explosion;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsMath;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxTween;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.TweenData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.subproject.sandbox.ship.ScheduleShipData;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.MathUtil;
import com.lancas.vs_wap.util.RandUtil;
import com.lancas.vs_wap.util.StrUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.*;

//todo change name
public class CustomExplosion extends Explosion {
    private static final int MAX_SHARD_CNT = 30;
    private static final int MAX_SHARD_SIZE = 5;

    private final Level level;
    private final double cx, cy, cz;
    private final float size;
    private final Vector3d projectileVelWhenExp = new Vector3d();
    private final Entity src;
    private final BlockInteraction blockInteract;
    private final boolean fire;

    private final ObjectArrayList<BlockPos> noAirBlockPoses = new ObjectArrayList<>();


    //private final List<SandBoxBlockClusterData> blockClusters = new ArrayList<>();
    //private final LinkedHashSet<BlockPos> blowPoses = new LinkedHashSet<>();  //todo use tree?


    public CustomExplosion(Level level, @Nullable Entity src, double x, double y, double z, Vector3dc inProjVel, float size, boolean fire, BlockInteraction bInteract) {
        this(level, src, null, null, x, y, z, inProjVel, size, fire, bInteract);
    }
    public CustomExplosion(Level inLevel, @Nullable Entity src, @Nullable DamageSource dmgSrc, @Nullable ExplosionDamageCalculator cal, double x, double y, double z, Vector3dc inProjVel, float size, boolean fire, BlockInteraction blockInteract) {
        super(inLevel, src, dmgSrc, cal, x, y, z, size, fire, blockInteract);
        level = inLevel;
        cx = x; cy = y; cz = z;
        projectileVelWhenExp.set(inProjVel);
        this.size = size;
        this.blockInteract = blockInteract;
        this.src = src;
        this.fire = fire;
    }

    @Override
    public void explode() {
        super.explode();

        noAirBlockPoses.clear();
        //temp
        noAirBlockPoses.addAll(getToBlow().stream().filter(p -> !level.getBlockState(p).isAir()).toList());
        //todo knonk back player
        //var toBlow = getToBlow();
        //toBlow.clear();
        /*blowPoses.clear();
        blockClusters.clear();
        float sqRadius = size * size;

        //todo compact valkyrien
        BlockPos.betweenClosedStream(
                BlockPos.containing(cx - size, cy - size, cz - size),
                BlockPos.containing(cx + size, cy + size, cz + size))
            .filter(pos -> pos.distToCenterSqr(cx, cy, cz) <= sqRadius)
            .forEach(pos -> {
                if (!level.getBlockState(pos).isAir()) {
                    blowPoses.add(pos.immutable());
                    //toBlow.add(pos.immutable());
                    // 预计算每个方块的爆炸类型
                    //typeCache.put(pos, detectEnvironment(getOpenDirections(level, pos)));
                }
            });

        if (blowPoses.isEmpty()) return;

        List<BlockPos> open = new ArrayList<>();*/
    }

    @Override
    public void finalizeExplosion(boolean spawnParticles) {
        preFinalizeExp(spawnParticles);

        if (!(level instanceof ServerLevel sLevel)) return;

        //do before super finalize
        //List<BlockPos> toBlow = getToBlow();
        //Util.shuffle((ObjectArrayList<BlockPos>)toBlow, level.random);

        //toBlow is shuffled in super
        // 生成物理碎片 gen phys shards
        int spawnCnt = Math.min((int)(noAirBlockPoses.size()), MAX_SHARD_CNT);
        Util.shuffle(noAirBlockPoses, level.random);
        EzDebug.log("to spawn count:" + spawnCnt);
        for (int i = 0; i < spawnCnt; ++i) {
            BlockPos curToBlow = noAirBlockPoses.get(i);
            BlockState state = level.getBlockState(curToBlow);

            if (state.isAir()) continue;

            //todo compact valkyrien

            //Vector3d normal = new Vector3d(0, (expCenterToBlockDir.y > 0 ? 1 : -1), 0);

            //Vector3d outVelDir = BallisticsMath.getBouncedVelNoDecrease(expCenterToBlockDir, normal);
            Vector3d spawnPos = JomlUtil.dCenter(curToBlow);
            //simply groundNormal as inv for proj vel dir
            Vector3d groundNormal = projectileVelWhenExp.normalize(-1, new Vector3d());
            if (!groundNormal.isFinite())
                groundNormal.set(0, 1, 0);  //default ground normal for safe

            SandBoxServerShip shardShip = new SandBoxServerShip(
                UUID.randomUUID(),
                new SandBoxTransformData().setPos(spawnPos),  //todo randomize
                SandBoxBlockClusterData.BlockAtCenter(state),
                SandBoxRigidbodyData.createEarthGravity().setOmega(RandUtil.onRandSphere(1, 8))
                    //.setVelocity(expCenterToBlockDir.mul(level.random.nextGaussian() * (-10)).setComponent(1, level.random.nextInt(10, 20))/*outVelDir.mul(level.random.nextFloat() * 20)*/)  //随机0-20速度
                    //.setOmega(RandUtil.onRandSphere(1, 8))
            ).timeOut(60);  //todo tween scale animation
            shardShip.addBehaviour(new SandBoxTween(), new TweenData(
                (transform, t01) -> SandBoxTransformData.copy(transform).setScaleXYZ(1 - t01),
                2.5
            ));

            //save the blockCenter before schedule callback because the blockPos is not serializable, Vector3d is
            Vector3d blockWorldCenter = JomlUtil.dCenter(curToBlow);  //todo change when compact valkyrien

            //exp center is overlap with blockWorldCenter, don't create the shard ship for safe
            Vector3d expCenterToBlockDir = blockWorldCenter.sub(cx, cy, cz, new Vector3d()).normalize();
            if (!expCenterToBlockDir.isFinite())
                continue;

            //delay for 2 tick to make sure blocks are removed from level(actually 3 tick)
            ScheduleShipData scheduleShipData = new ScheduleShipData(
                shardShip, 2, (l, ship, remainTick, canceled) -> {
                if (remainTick != 0) return;  //only set vel at remain tick 0
                //cx, cy, cz is serializable and don't change
                //projectileVelWhenExp is serializable and don't change

                Vector3d velDirAlongVel = new Vector3d();
                Vector3d velDirVertToVel = new Vector3d();
                //沿着速度方向对距离差进行正交分解
                MathUtil.orthogonality(expCenterToBlockDir, projectileVelWhenExp, velDirAlongVel, velDirVertToVel);
                velDirAlongVel.normalize(); velDirVertToVel.normalize();

                if (!velDirAlongVel.isFinite() || !velDirVertToVel.isFinite()) {  //cancel ship if the vel is not finite
                    EzDebug.warn("can't spawn shard ship because invalid vel:" + velDirAlongVel + "," + velDirVertToVel);
                    canceled.set(true);
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
                    JomlUtil.v3(blockWorldCenter),
                    JomlUtil.v3(shardVel.mul(0.5, new Vector3d()).add(blockWorldCenter)),  //计算0.5s后是否有碰撞
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    null
                );
                BlockHitResult result = l.clip(ctx);
                if (result.getType() != HitResult.Type.MISS) {
                    //计算反弹的速度
                    shardVel.set(BallisticsMath.getBouncedVelNoDecrease(shardVel, groundNormal));
                    EzDebug.warn(
                        "worldCenter:" + blockWorldCenter +
                            "is inside?:" + result.isInside() +
                            " at " + StrUtil.poslike(result.getBlockPos()) +
                            ", the state in level at hitpos:" + StrUtil.getBlockName(level.getBlockState(result.getBlockPos()))
                    );
                }
                ship.getRigidbody().getExposedData().setVelocity(shardVel);
            });

            SandBoxServerWorld.scheduleShipOverwriteIfExisted(sLevel, scheduleShipData);
            blowOnBlock(sLevel, curToBlow);
            //EzDebug.light("spawn at " + StrUtil.F2(spawnPos) + ", vel:" + StrUtil.F2(outVelDir) + ", normal:" + StrUtil.F2(normal) + ", expCenterToBlockDir:" + StrUtil.F2(expCenterToBlockDir));
        }

        fireStage();
    }

    private void preFinalizeExp(boolean spawnParticles) {
        if (this.level.isClientSide) {
            this.level.playLocalSound(cx, cy, cz, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        if (spawnParticles) {
            if (!(this.size < 2.0F) && this.interactsWithBlocks()) {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, cx, cy, cz, 1.0, 0.0, 0.0);
            } else {
                this.level.addParticle(ParticleTypes.EXPLOSION, cx, cy, cz, 1.0, 0.0, 0.0);
            }
        }
    }
    private void fireStage() {
        if (this.fire) {
            //ObjectListIterator var14 = this.toBlow.iterator();

            for (BlockPos bp : getToBlow()) {
                BlockPos above = bp.above();
                if (level.random.nextInt(3) == 0 &&
                    !this.level.getBlockState(bp).isAir() &&
                    this.level.getBlockState(above).isSolidRender(this.level, above)
                ) {
                    this.level.setBlockAndUpdate(above, BaseFireBlock.getState(this.level, above));
                }
            }
        }
    }
    private void blowOnBlock(ServerLevel level, BlockPos pos) {
        BlockState blockstate = this.level.getBlockState(pos);
        if (blockstate.isAir()) return;

        //BlockPos blockpos1 = blockpos.immutable();
        //this.level.getProfiler().push("explosion_blocks");
        if (blockstate.canDropFromExplosion(this.level, pos, this)) {
            BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(pos) : null;
            LootParams.Builder lootparams$builder = (new LootParams.Builder(level))
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, this.src);

            if (this.blockInteract == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.size);
            }

            //blockstate.spawnAfterBreak(sLevel, pos, ItemStack.EMPTY, /*flag1*/false);
                /*blockstate.getDrops(lootparams$builder).forEach((p_46074_) ->
                    addBlockDrops(objectarraylist, p_46074_, pos)
                );*/  //todo get drops
        }

        blockstate.onBlockExploded(this.level, pos, this);
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        //this.level.getProfiler().pop();
    }
    //todo pop item
    /*
    while(var5.hasNext()) {
                Pair<ItemStack, BlockPos> pair = (Pair)var5.next();
                net.minecraft.world.level.block.Block.popResource(this.level, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
            }

     */

    /*@Deprecated(forRemoval = true)
    @Override
    public List<BlockPos> getToBlow() {
        throw new NotImplementedException("Custome Explision don't use original toBlow!");
    }*/
}
