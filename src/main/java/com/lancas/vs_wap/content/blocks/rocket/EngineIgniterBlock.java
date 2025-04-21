package com.lancas.vs_wap.content.blocks.rocket;

/*
public class EngineIgniterBlock extends Block implements IBE<EngineIgniterBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public EngineIgniterBlock(Properties props) {
        super(props);
        registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }


    @Override
    public Class<EngineIgniterBlockEntity> getBlockEntityClass() { return EngineIgniterBlockEntity.class; }
    @Override
    public BlockEntityType<? extends EngineIgniterBlockEntity> getBlockEntityType() { return EinherjarBlockEntites.ENGINE_IGNITER_BE.get(); }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, bs, be) -> ((EngineIgniterBlockEntity)be).tick();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        //EzDebug.Log("igniter block neighbor changed");
        if (level.isClientSide) return;

        if (!(level.getBlockEntity(pos) instanceof EngineIgniterBlockEntity be)) {
            //EzDebug.Log("has no block entity");
            return;
        }

        boolean activated = be.activated;
        //EzDebug.Log("activated is " + be.activated);
        if (activated) return;

        if (level.hasNeighborSignal(pos)) {
            Direction facing = state.getValue(FACING);
            be.activate(facing);
        }
    }
}
*/