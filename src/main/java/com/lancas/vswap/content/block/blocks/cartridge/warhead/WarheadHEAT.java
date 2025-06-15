package com.lancas.vswap.content.block.blocks.cartridge.warhead;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.explosion.CustomExplosion;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.math.WapBallisticMath;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

import java.util.List;
import java.util.Objects;

public class WarheadHEAT extends BlockPlus implements ISandBoxBallisticBlock {
    public WarheadHEAT(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return WarheadHEAT.addersIfAbsent(WarheadHEAT.class, () -> List.of(
            new DefaultCartridgeAdder(true)
        ));
    }

    public static final double JET_SPE = 80;
    public static final double JET_MAX_DIST = 10;  //jetSpe will linear decrease by dist
    public static final float EXP_POWER = 1f;

    @Override
    public void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
        infos.stream()
            .map(i -> {
                if (i instanceof SandBoxTriggerInfo.ActivateTriggerInfo activateInfo)
                    return activateInfo;
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst()
            .ifPresent(info -> {
                var rigidReader = ship.getRigidbody().getDataReader();

                //Vector3dc vel = rigidReader.getVelocity();
                Vector3d jetDir = rigidReader.getVelocity(new Vector3d()).normalize();
                if (!jetDir.isFinite()) {
                    EzDebug.warn("When trigger HEAT warhead, get invalid jetDir:" + jetDir);
                    return;
                }

                double jetSPE = JET_SPE;
                Vec3 clipFrom = JomlUtil.v3(info.activatePos);
                Vec3 clipTo = JomlUtil.v3(jetDir).scale(JET_MAX_DIST).add(clipFrom);
                while (jetSPE > 0.01) {
                    ClipContext curClip = new ClipContext(
                        clipFrom, clipTo,
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                        null
                    );
                    BlockHitResult curHit = RaycastUtilsKt.clipIncludeShips(level, curClip, true);
                    if (curHit.getType() == HitResult.Type.MISS)
                        break;

                    //hit block
                    BlockState curArmour = level.getBlockState(curHit.getBlockPos());
                    Vector3d worldNormal = WorldUtil.getWorldDirection(level, curHit.getBlockPos(), curHit.getDirection());

                    double incRad = WapBallisticMath.RAD.calIncidenceRad(jetDir, worldNormal);
                    double sqDist = Math.max(curHit.getLocation().distanceToSqr(clipFrom), 1.0);
                    double scaledJetSpe = jetSPE / sqDist;//(1.0 - Math.min(curHit.getLocation().distanceTo(clipFrom), JET_MAX_DIST) / JET_MAX_DIST);
                    double curRhae = WapBallisticMath.RAD.caEquivalentRhae(curArmour, incRad);

                    if (scaledJetSpe < curRhae)
                        break;

                    jetSPE -= curRhae;
                    BlockHelper.destroyBlock(level, curHit.getBlockPos(), 1f);
                }

                level.explode(null, clipFrom.x, clipFrom.y, clipFrom.z, EXP_POWER, Level.ExplosionInteraction.BLOCK);  //apply a small exp

                terminateByEffect.set(true);
                SandBoxServerWorld.markShipDeleted(level, ship.getUuid());
            });
    }
}
