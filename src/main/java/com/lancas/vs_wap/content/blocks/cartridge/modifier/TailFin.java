package com.lancas.vs_wap.content.blocks.cartridge.modifier;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.foundation.api.PidDirectionPhysBehaviour;
import com.lancas.vs_wap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vs_wap.ship.ballistics.api.IPhysicalBehaviourAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TailFin extends BlockPlus implements IPhysicalBehaviourAdder {
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            TailFin.class,
            () -> List.of(
                new DefaultCartridgeAdder()
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
}
