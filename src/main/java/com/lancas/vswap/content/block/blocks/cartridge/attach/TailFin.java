package com.lancas.vswap.content.block.blocks.cartridge.attach;

import com.lancas.vswap.content.block.blocks.cartridge.booster.IFlameThroughable;
import com.lancas.vswap.content.saved.blockrecord.IBlockRecord;
import com.lancas.vswap.foundation.api.PidDirectionController;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.foundation.api.PidDirectionPhysBehaviour;
import com.lancas.vswap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vswap.ship.ballistics.api.IPhysicalBehaviourBlock;
import com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vswap.subproject.sandbox.ship.IServerSandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TailFin extends BlockPlus implements IPhysicalBehaviourBlock, ISandBoxBallisticBlock, IFlameThroughable {
    private static final ConcurrentHashMap<UUID, PidDirectionController> sandboxShipTailFinController = new ConcurrentHashMap<>();
    static {
        SandBoxEventMgr.onRemoveShip.addListener((world, ship) -> {
            if (ship instanceof IServerSandBoxShip) {
                sandboxShipTailFinController.remove(ship.getUuid());
            }
        });
    }

    public static class TailFinRecord implements IBlockRecord {
        public PidDirectionController controller = new PidDirectionController(6, 1, 3, 100);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            TailFin.class,
            () -> List.of(
                new DefaultCartridgeAdder(false)//,
                //new RefreshBlockRecordAdder((blockPos, state) -> new TailFinRecord())
                //, EinherjarBlockInfos.mass.getOrCreateExplicit(TailFin.class, state -> 15.0)
            )
        );
    }

    public TailFin(Properties p_49795_) {
        super(p_49795_);
    }

    /*@Override
    public Vector3dc calculateForceInServerTick(ModifierData data) {
        if (!data.isOutArtillery) return new Vector3d();
        if (!data.isTail()) return new Vector3d();

        EzDebug.log("tail fin effects");
        return new Vector3d(0, 4, 0).mul(data.projectileShip.getInertiaData().getMass());
    }*/

    @Override
    public IPhysBehaviour getPhysicalBehaviour(BlockPos bp, BlockState state) {
        //todo only tail
        return new PidDirectionPhysBehaviour(6, 1, 3, 100);
        /*return PhysBehaviourImpl.createTorqueBehaviour(
            (physShip, bpInShip) -> {
                /.*Vector3dc shipVel = physShip.getPoseVel().getVel();
                double shipVelSqLen = shipVel.lengthSquared();
                if (shipVelSqLen < 10) return null;
                Vector3d force = shipVel.normalize(-shipVelSqLen * 0.0005, new Vector3d());

                Vector3d worldBlockCenter = physShip.getTransform().getShipToWorld().transformPosition(JomlUtil.dCenter(bpInShip));

                return new ForceOnPos(force, worldBlockCenter);*./
                return null;
            }
        );*/
    }

    /*@Override
    public void physTick(SandBoxServerShip ship, BallisticData ballisticData) {
        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();
        IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();
        if (rigidReader.getVelocity().lengthSquared() < 10)
            return;

        PidDirectionController controller = sandboxShipTailFinController.computeIfAbsent(
            ship.getUuid(),
            k -> new PidDirectionController(3, 0.5, 2, 1)
        );

        Vector3d worldTowards = rigidReader.localIToWorldNoScaleDir(ballisticData.initialStateData.localForward).normalize();
        Vector3dc velTowards = rigidReader.getVelocity().normalize(new Vector3d());

        Vector3d newOmega = controller.getNewOmega(worldTowards, velTowards, rigidReader.getOmega(), Constants.PHYS_FRAME_TIME);
        rigidWriter.setOmega(newOmega);
    }*/

    @Override
    public void modifyFlyingContext(ServerLevel level, SandBoxServerShip ship, BallisticData ballisticData, BallisticPos ballisticPos, BlockState state, BallisticFlyingContext ctx) {
        if (ballisticPos.fromTail() == 0)
            ctx.displacementIntensity /= 4;
    }
}
