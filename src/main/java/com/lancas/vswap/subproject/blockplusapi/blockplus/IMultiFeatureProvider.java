package com.lancas.vswap.subproject.blockplusapi.blockplus;

/*
public interface IMultiFeatureProvider extends IBlockFeatureProvider {
    public Iterable<IBlockFeatureProvider> getSubProviders();

    public default void onInit(FeatureAccepterBlock thisBlock) {
        getSubProviders().forEach(p -> p.onInit(thisBlock));
    }
    public default void onCreateBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        getSubProviders().forEach(p -> p.onCreateBlockStateDefinition(builder));
    }
    public default void onGetStateForPlacement(BlockPlaceContext ctx, BlockState dest) {
        getSubProviders().forEach(p -> p.onGetStateForPlacement(ctx, dest));
    }
    public default VoxelShape appendShape(BlockState state) {
        AtomicReference<VoxelShape> appendShape = new AtomicReference<>(Shapes.empty());
        getSubProviders().forEach(p -> appendShape.set(Shapes.join(appendShape.get(), p.appendShape(state), BooleanOp.OR)));
        return appendShape.get();
    }
    public default int getRedstoneModifyValue(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        AtomicInteger redstoneModify = new AtomicInteger();
        getSubProviders().forEach(p -> redstoneModify.addAndGet(getRedstoneModifyValue(state, blockAccess, pos, side)));
        return redstoneModify.get();
    }
    public default boolean provideRedstoneSrcVerification(BlockState state) {
        AtomicBoolean isSrc = new AtomicBoolean(false);
        getSubProviders().forEach(p -> isSrc.set(isSrc.get() || p.provideRedstoneSrcVerification(state)));
        return isSrc.get();
    }
}
*/