package com.lancas.vs_wap.content.blocks.cartridge.propellant;

import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CombustiblePropellantBlock extends BlockPlus implements IPropellant {
    //be air after burn so there is no need for an empty value
    //public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            CombustiblePropellantBlock.class,

            () -> List.of(new DefaultCartridgeAdder())
        );
    }

    public CombustiblePropellantBlock(Properties p_49795_) {
        super(p_49795_);
    }

    /*public static final ShapeBuilder UP_SHAPE = ShapeBuilder.createBox(3, 0, 3, 13, 16, 13);
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        Direction dir = state.getValue(FACING);
        return UP_SHAPE.getRotated(dir);
    }

    /*
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        ServerShip onShip = ShipUtil.getShipAt((ServerLevel)level, pos);
        if (onShip == null) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        Direction dir = state.getValue(FACING);
        if (hasSignal) {
            ShipBuilder splitShipBuilder = DirectionalSplitHandler.trySplit((ServerLevel)level, pos, dir);
            if (splitShipBuilder != null) {
                Vector3d velocity = onShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(dir)).mul(20);
                splitShipBuilder.setWorldVelocity(velocity);
                //EzDebug.Log("vel:" + splitShipBuilder.get().getVelocity());
            }
        }
    }*/

    @Override
    public boolean isEmpty(BlockState state) { return false; }

    @Override
    public void setAsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
    @Override
    public double getPower(BlockState state) { return WapBlockInfos.propellant_power.valueOrDefaultOf(state); }
}
