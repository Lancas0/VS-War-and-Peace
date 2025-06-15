package com.lancas.vswap.subproject.pondervs.sandbox;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.ClientOnlyBehaviour;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxPonderShip;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PonderMunitionBehaviour extends ClientOnlyBehaviour<PonderMunitionData> {

    @Override
    protected PonderMunitionData makeInitialData() { return new PonderMunitionData(); }

    @Override
    public void clientTick(ClientLevel level) {
        PonderWorld ponderWorld = data.ponderScene.getWorld();
        if (ponderWorld == null) {
            EzDebug.warn("get null ponder world!");
            return;
        }

        var rigidWriter = ship.getRigidbody().getDataWriter();
        var rigidReader = ship.getRigidbody().getDataReader();
        Vector3dc vel = rigidReader.getVelocity();
        if (vel.lengthSquared() > 1E-8)
            rigidWriter.setRotation(JomlUtil.swingYXRotateTo(JomlUtil.d(data.localForward), vel, new Quaterniond()));

        EzDebug.highlight("vel:" + vel);

        if (data.remainDestroyCnt <= 0)
            return;

        int preRemainDestroy = data.remainDestroyCnt;
        data.breakableSections.forEach(s -> {
            Vector3d from = rigidReader.localIToWorldPos(data.localTip);
            Vector3d worldDir = rigidReader.getRotation().transform(JomlUtil.d(data.localForward));
            Vector3d to = new Vector3d(worldDir).mul(0.0167).add(from);

            for (int i = 0; i < 100; ++i) {
                if (data.remainDestroyCnt <= 0)
                    return;

                var traceResult = s.rayTrace(ponderWorld, JomlUtil.v3(from), JomlUtil.v3(to));
                if (traceResult == null)
                    break;

                BlockHitResult hit = traceResult.getSecond();
                if (hit.getType() == HitResult.Type.MISS)
                    break;

                double radius = data.destroyRadiusGetter.apply((SandBoxPonderShip)ship);
                breakAround(ponderWorld, hit.getLocation(), radius);  //don't use actualHit in traceResult: the hit should be on the BlockPos

                /*BlockState prevState = ponderWorld.getBlockState(hit.getBlockPos());
                ponderWorld.setBlock(hit.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
                ponderWorld.addBlockDestroyEffects(hit.getBlockPos(), prevState);

                data.remainDestroyCnt--;*/
            }
        });

        if (data.remainDestroyCnt != preRemainDestroy) {
            data.ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
        }

    }

    protected void breakAround(PonderWorld ponderWorld, Vec3 hit, double radius) {
        BlockPos.betweenClosed(
            (int)Math.floor(hit.x - radius),
            (int)Math.floor(hit.y - radius),
            (int)Math.floor(hit.z - radius),
            (int)Math.floor(hit.x + radius),
            (int)Math.floor(hit.y + radius),
            (int)Math.floor(hit.z + radius)
        ).forEach(bp -> {
            if (data.remainDestroyCnt <= 0)
                return;

            BlockState prevState = ponderWorld.getBlockState(bp);
            ponderWorld.setBlockAndUpdate(bp, Blocks.AIR.defaultBlockState());
            ponderWorld.addBlockDestroyEffects(bp, prevState);

            data.breakCallback.accept(prevState);

            data.remainDestroyCnt--;
        });
    }

    @Override
    public Class<?> getDataType() { return PonderMunitionData.class; }
}
