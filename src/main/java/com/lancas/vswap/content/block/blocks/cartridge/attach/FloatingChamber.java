package com.lancas.vswap.content.block.blocks.cartridge.attach;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import java.util.List;

public class FloatingChamber extends BlockPlus implements ISandBoxBallisticBlock {
    public FloatingChamber(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(FloatingChamber.class, () -> List.of(
            new DefaultCartridgeAdder(true)
        ));
    }

    public void modifyFlyingContext(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, BlockState state, BallisticFlyingContext ctx) {
        var rigidReader = ship.getRigidbody().getDataReader();

        //todo when meet not water fluid....
        if (!level.getFluidState(JomlUtil.bpContaining(rigidReader.getPosition())).isEmpty()) {
            ctx.gravity.add(new Vector3d(0, -9.8, 0).mul(-1.15));
        }
    }

}
