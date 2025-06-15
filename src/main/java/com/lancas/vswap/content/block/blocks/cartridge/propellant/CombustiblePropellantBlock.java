package com.lancas.vswap.content.block.blocks.cartridge.propellant;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CombustiblePropellantBlock extends BlockPlus implements IPropellant {
    //be air after burn so there is no need for an empty value
    //public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            CombustiblePropellantBlock.class,

            () -> List.of(new DefaultCartridgeAdder(true))
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
    public BlockState getEmptyState(BlockState state) { return Blocks.AIR.defaultBlockState(); }
    @Override
    public double getSPE(BlockState state) { return WapBlockInfos.StdPropellantEnergy.valueOrDefaultOf(state); }
}
