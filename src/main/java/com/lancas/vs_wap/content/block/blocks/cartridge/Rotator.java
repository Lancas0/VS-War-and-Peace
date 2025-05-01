package com.lancas.vs_wap.content.block.blocks.cartridge;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vs_wap.ship.ballistics.api.IPhysicalBehaviourBlock;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.util.List;

public class Rotator extends BlockPlus implements IPhysicalBehaviourBlock {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RotatorPhysBehaviour implements IPhysBehaviour {
        public Vector3d getAdditionalTorque(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) {
            Vector3d worldForward = shipData.getForwardInWorld(physShip.getTransform().getShipToWorld());
            Vector3d torque =  worldForward.mul(physShip.getInertia().getMomentOfInertiaTensor(), new Vector3d()).mul(10);
            return torque;
        }
    }

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
    public IPhysBehaviour getPhysicalBehaviour(BlockPos bp, BlockState state) { return new RotatorPhysBehaviour(); }
}
