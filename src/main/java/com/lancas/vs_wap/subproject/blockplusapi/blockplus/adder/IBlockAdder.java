package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public interface IBlockAdder {
    public default void onInit(BlockPlus thisBlock) {}
    public default void onCreateBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {}
    public default BlockState setValueForDefaultState(BlockState defaultState) { return defaultState; }
    public default BlockState getStateForPlacement(BlockPlaceContext ctx, BlockState dest) { return dest; }
    public default VoxelShape appendShape(BlockState state) { return Shapes.empty(); }

    public default int getRedstoneModifyValue(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) { return 0; }
    public default boolean provideRedstoneSrcVerification(BlockState state) { return false; }

    public default void onNeighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {}

    public default void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {}
    public default void onPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {}

    public default void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) { }

    //public default void onDestroyBy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {}

    public default boolean cancelVanillaItemDrop() { return false; }
    public default boolean dropXpOnMinedAppend() { return false; }
    public default float foodExhaustionOnMinedAppend() { return 0; }
    //public default void onPlayerMined(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack minedWithStack) {}
    /*@Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        //EzDebug.Log("direction:" + ctx.getNearestLookingDirection().getOpposite());

        //return this.defaultBlockState()
        //    .setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
        return this.defaultBlockState().setValue(FACING, getDirectionForPlacement(ctx));
    }*/
}
