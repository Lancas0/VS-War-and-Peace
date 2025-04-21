package com.lancas.vs_wap.content.blocks.cartridge;

import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vs_wap.ship.ballistics.api.IPhysicalBehaviourAdder;
import com.lancas.vs_wap.ship.ballistics.api.PhysBehaviourImpl;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import java.util.List;

public class Rotator extends BlockPlus implements IPhysicalBehaviourAdder {

    public Rotator(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            Rotator.class,
            () -> List.of(
                new DefaultCartridgeAdder()
                //, EinherjarBlockInfos.mass.getOrCreateExplicit(Rotator.class, state -> 400.0)
            )
        );
    }

    @Override
    public IPhysBehaviour getPhysicalBehaviour(BlockPos bp, BlockState state) {
        return PhysBehaviourImpl.createTorqueBehaviour(
            (physShip, blockPos, shipData) -> {
                Vector3d worldForward = shipData.getForwardInWorld(physShip.getTransform().getShipToWorld());
                Vector3d torque =  worldForward.mul(physShip.getInertia().getMomentOfInertiaTensor(), new Vector3d()).mul(10);
                return torque;
            }
        );
    }
}
