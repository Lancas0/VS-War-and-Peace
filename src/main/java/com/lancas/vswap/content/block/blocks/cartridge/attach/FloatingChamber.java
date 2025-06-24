package com.lancas.vswap.content.block.blocks.cartridge.attach;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
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

    @Override
    public void modifyFlyingContext(ServerLevel level, SandBoxServerShip ship, BallisticData ballisticData, BallisticPos ballisticPos, BlockState state, BallisticFlyingContext ctx) {
        var rigidReader = ship.getRigidbody().getDataReader();

        BlockPos bp = JomlUtil.bpContaining(rigidReader.getPosition());
        FluidState hereFluid = level.getFluidState(bp);
        FluidState aboveFluid = level.getFluidState(bp.above());
        FluidState aboveAbvFluid = level.getFluidState(bp.above().above());
        FluidState aboveAbvAbvFluid = level.getFluidState(bp.above().above().above());

        boolean isFloating = rigidReader.getVelocity().y() > 0;

        if (!hereFluid.isEmpty()) {
            if (aboveFluid.isEmpty())  {
                if (isFloating)
                    ctx.gravity.add(0, -9.8, 0);
                return;
            }

            //above not empty
            if (aboveAbvFluid.isEmpty()) {
                ctx.gravity.add(isFloating ? new Vector3d(0) : new Vector3d(0, -9.8, 0).mul(-0.5));
            }

            //abv abv not empty
            if (aboveAbvAbvFluid.isEmpty())
                ctx.gravity.add(isFloating ? new Vector3d(0, -4.9, 0) : new Vector3d(0, 9.8, 0));

            //abv abv abv not empty
            //if (aboveAbvAbvFluid.isEmpty())
            ctx.gravity.add(isFloating ? new Vector3d(0, -3, 0) : new Vector3d(0, -9.8, 0).mul(-1.15));
        }
    }

}
