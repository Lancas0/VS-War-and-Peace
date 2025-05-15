package com.lancas.vs_wap.content.block.blocks.cartridge.modifier;

import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.foundation.Constants;
import com.lancas.vs_wap.foundation.api.PidDirectionController;
import com.lancas.vs_wap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.foundation.api.PidDirectionPhysBehaviour;
import com.lancas.vs_wap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vs_wap.ship.ballistics.api.IPhysicalBehaviourBlock;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vs_wap.subproject.sandbox.ship.IServerSandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TailFin extends BlockPlus implements IPhysicalBehaviourBlock, ISandBoxBallisticBlock {
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
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            TailFin.class,
            () -> List.of(
                new DefaultCartridgeAdder()//,
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

    @Override
    public void physTick(SandBoxServerShip ship, BallisticData ballisticData) {
        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();
        IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();
        if (rigidReader.getVelocity().lengthSquared() < 10)
            return;

        PidDirectionController controller = sandboxShipTailFinController.computeIfAbsent(
            ship.getUuid(),
            k -> new PidDirectionController(3, 0.5, 2, 1)
        );

        Vector3d worldTowards = rigidReader.localToWorldNoScaleDir(ballisticData.initialStateData.localForward).normalize();
        Vector3dc velTowards = rigidReader.getVelocity().normalize(new Vector3d());

        Vector3d newOmega = controller.getNewOmega(worldTowards, velTowards, rigidReader.getOmega(), Constants.PHYS_FRAME_TIME);
        rigidWriter.setOmega(newOmega);
    }
}
