package com.lancas.vs_wap.content.block.blockentity;

import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.attachment.ForcesInducer;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsMath;
import com.lancas.vs_wap.ship.helper.builder.TeleportDataBuilder;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.Random;

public class ApWarheadBlockEntity extends BlockEntity {
    private final Random randomSrc = new Random();
    private Vector3d currentVelocity = null;
    private Quaterniond toUpdateRotation = null;
    private boolean bouncing = false;
    private boolean updateProjectile = false;

    public void setIsBouncing() { bouncing = true; }
    public boolean isBouncing() { return bouncing; }

    public ApWarheadBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public void tickUpdate() {
        if (!(level instanceof ServerLevel sLevel)) return;

        ServerShip projectile = ShipUtil.getServerShipAt(sLevel, worldPosition);
        /*if (!(projectile instanceof ShipObjectServer projectileShipObj)) {
            //may not on ship now, do nothing.
            if (projectile != null) {
                EzDebug.warn("fail to convert a non-null projectile to ShipObjectServer");
            }
            return;
        }*/

        //((ShipObjectServer)projectile).getShipData().getPhysicsData().setLinearVelocity(new Vector3d());

        if (updateProjectile) {
            updateProjectile = false;

            TeleportDataBuilder teleportData = TeleportDataBuilder.copy(level, projectile);
            if (currentVelocity != null) {
                teleportData.setVel(currentVelocity);
                teleportData.addPos(currentVelocity.mul(0.016, new Vector3d()));  //todo i don't know if i should use phy tick time or game tick time
                //Vector3d velRef = (Vector3d)(((ShipObjectServer)projectile).asShipDataCommon().getPhysicsData().setLinearVelocity(););
                //velRef.set(currentVelocity);
                //((ShipObjectServer)projectile).asShipDataCommon().getPhysicsData().setLinearVelocity(currentVelocity);
                //teleportData.setVel(currentVelocity);

            }
            if (toUpdateRotation != null)
                teleportData.setRotation(toUpdateRotation);


            //todo change rotation

            ShipUtil.teleport(sLevel, projectile, teleportData.get());
            EzDebug.light("update projectile, new vel:" + StrUtil.F2(currentVelocity) + ", pos:" + StrUtil.F2(teleportData.get().getNewPos()));
            //projectileShipObj.setShipTeleportId$impl(projectileShipObj.getShipTeleportId() + 1);
            //projectileShipObj.teleportShip(teleportData.get());
        }

        currentVelocity = null;
        toUpdateRotation = null;
        bouncing = false;
    }

    public void onAcceptTriggerInfo(TriggerInfo.CollisionTriggerInfo info) {
        if (!(level instanceof ServerLevel sLevel)) return;
        if (bouncing) return;  //do not accept info if it's bouncing

        var hitInfo = info.hitInfo;
        ServerShip projectile = ShipUtil.getServerShipAt(sLevel, info.triggerBlockPos);
        LoadedServerShip hitShip = null;
        if (ShipUtil.getServerShipAt(sLevel, hitInfo.hitBlockPos) instanceof LoadedServerShip loadedHitShip) {
            hitShip = loadedHitShip;
        }
        //@Nullable LoadedServerShip hitShip = ();

        //todo rotation
        if (currentVelocity == null) {
            currentVelocity = new Vector3d(projectile.getVelocity());
        }

        BlockState projHeadState = info.triggerBlockState;
        BlockState armourState = hitInfo.getHitBlockState(sLevel);
        if (armourState.isAir()) {  //the block is already broken
            return;
        }

        double projectileScale = projectile.getTransform().getShipToWorldScaling().x();  //todo 3d scale
        double armourScale = hitShip == null ? 1.0 : hitShip.getTransform().getShipToWorldScaling().x();  //todo 3d scale?

        //EzDebug.log("projState:" + StrUtil.getBlockName(projHeadState) + ", projHardness:" + EinherjarBlockInfos.hardness.valueOrDefaultOf(projHeadState));
        //EzDebug.log("armourState:" + StrUtil.getBlockName(armourState) + ", armourHardness:" + EinherjarBlockInfos.hardness.valueOrDefaultOf(armourState));

        BallisticsMath.TerminalContext terminalCtx = BallisticsMath.TerminalContext.safeContextOrNull(
            currentVelocity,
            hitInfo.worldNormal,
            WapBlockInfos.oblique_degree.valueOrDefaultOf(projHeadState),
            WapBlockInfos.hardness.valueOrDefaultOf(projHeadState),
            WapBlockInfos.hardness.valueOrDefaultOf(armourState),
            WapBlockInfos.toughness.valueOrDefaultOf(armourState),
            WapBlockInfos.getValkrienMass(projHeadState),  //todo
            WapBlockInfos.ap_area.valueOrDefaultOf(projHeadState),
            projectileScale,
            armourScale
        );
        if (terminalCtx == null)
            return;

        if (terminalCtx.isPass()) {
            EzDebug.highlight("pass by deg:" + Math.toDegrees(terminalCtx.incidenceRad));
            return;
        }

        boolean penetrate = terminalCtx.canPenetrate();
        boolean shouldBounce = terminalCtx.isBounce(randomSrc);
        if (penetrate) {
            //todo maybe add a destroy context
            level.setBlock(hitInfo.hitBlockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);  //todo armour post effect
        }

        double mass = projectile.getInertiaData().getMass();
        Vector3d prevVel = new Vector3d(currentVelocity);
        Vector3d postVel = terminalCtx.getPostVelocity(randomSrc);  // = shouldBounce ? terminalCtx.getBouncedVelocity(penetrate) : terminalCtx.getPenetratedVel();
        /*if (!penetrate) {
            postVel = terminalCtx.getReboundVel();
        } else if (shouldBounce) {
            postVel = terminalCtx.getBouncedVelocity(penetrate);
        }*/

        currentVelocity.set(postVel);
        bouncing = shouldBounce;
        if (bouncing) {
            toUpdateRotation = toUpdateRotation == null ?
                new Quaterniond(projectile.getTransform().getShipToWorldRotation()) :
                toUpdateRotation;

            toUpdateRotation.set(terminalCtx.getBouncedRotation(toUpdateRotation));
        }

        updateProjectile = true;


        EzDebug.highlight(
            "incDeg:" + Math.toDegrees(terminalCtx.incidenceRad) +
                ", obliquedIncDeg:" + Math.toDegrees(terminalCtx.obliquedIncidenceRad) +
                ", criticalDeg:" + Math.toDegrees(terminalCtx.criticalRad) +
                ", penetrate?: " + penetrate +
                ", bounce? :" + shouldBounce +
                ", prevVel:" + StrUtil.F2(prevVel) +
                ", postVel:" + StrUtil.F2(postVel) +
                ", prevE(kJ):" + 0.5 * mass * prevVel.lengthSquared() / 1000 +
                ", postE(kJ):" + 0.5 * mass * postVel.lengthSquared() / 1000
        );

        if (hitShip != null) {
            ForcesInducer.apply(hitShip, terminalCtx.getImpactForce());
        }
    }
}
