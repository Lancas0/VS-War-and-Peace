package com.lancas.vs_wap.content.block.blocks.abstrac;

/*
public abstract class AbstractFuseBlock extends AbstractCartridgeBlock implements ICollisionDetector {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public AbstractFuseBlock(Properties p_49795_) {
        super(p_49795_);
        this.stateDefinition.any().setValue(POWER, 0);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER);
    }


    @Override
    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return state.getValue(POWER);
    }
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
    @Override
    public int getDirectSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return getSignal(state, blockAccess, pos, side);
    }

    //@Override
    //public abstract ShapeBuilder getUpShape();

    /.*@Override
    public Property<Integer> getProperty() { return POWER; }
    @Override
    public Integer getDefaultValue() { return 0; }*./

    @Override
    public AABBd getLocalBound(BlockState state) {
        Direction dir = state.getValue(FACING);
        //AABB aabb = getUpShape().getRotated(dir).bounds();
        AABB aabb = UP_SHAPE.getRotated(dir).bounds();  //todo shape setting
        return JomlUtil.d(aabb);
    }

    @Override
    public boolean shouldDetect(BlockState state) {
        if (state.getValue(POWER) > 0)
            return false;
        return true;
    }
}
*/