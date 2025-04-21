package com.lancas.vs_wap.content.blocks.explosive;

/*
import com.lancas.einherjar.content.EinherjarBlockEntites;
import com.lancas.einherjar.content.blockentity.DelayedExplosiveBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DelayedExplosiveBlock extends AbstractExplosiveBlock implements IBE<DelayedExplosiveBlockEntity> {
    //public static final String ID = "delayed_explosive_block";
    //public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 1, 10);

    public static final IntegerProperty DELAYED_TICK = IntegerProperty.create("delayed_tick", 0, 200);
    public static final IntegerProperty EXPLOSION_POWER = IntegerProperty.create("explosion_power", 1, 20);
    //todo explosive intensity

    @Override
    public Class<DelayedExplosiveBlockEntity> getBlockEntityClass() { return DelayedExplosiveBlockEntity.class; }
    @Override
    public BlockEntityType<? extends DelayedExplosiveBlockEntity> getBlockEntityType() { return EinherjarBlockEntites.DELAYED_EXPLOSIVE_BLOCK_ENTITY.get(); }

    public DelayedExplosiveBlock(Properties p_49795_, int inDelayedTicks, int explosionPower) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any()
            //.setValue(DAMAGE, 10)
            .setValue(DELAYED_TICK, inDelayedTicks)
            .setValue(EXPLOSION_POWER, explosionPower)
        );
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(/.*DAMAGE, *./EXPLOSION_POWER, DELAYED_TICK);
    }

    /.*@Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DelayedExplosiveBlockEntity explosionBe) {
                int delayedTicks = state.getValue(DELAYED_TICK);
                int explosionPower = state.getValue(EXPLOSION_POWER);

                explosionBe.startCountingDown(delayedTicks, explosionPower);
            }
        }
    }*./

    @Override
    public void explodeByRedstone(ServerLevel level, BlockPos pos, BlockState state) {
        int delayedTicks = state.getValue(DELAYED_TICK);
        explode(level, pos, state, delayedTicks);
    }
    @Override
    public void explodeByExplosion(ServerLevel level, BlockPos pos, BlockState state) {
        explode(level, pos, state, 0);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, bs, be) -> ((DelayedExplosiveBlockEntity)be).tick();
    }



    public void explode(ServerLevel level, BlockPos pos, BlockState state, int delayedTicks) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DelayedExplosiveBlockEntity explosionBe) {
            //int delayedTicks = state.getValue(DELAYED_TICK);
            int explosionPower = state.getValue(EXPLOSION_POWER);

            explosionBe.startCountingDown(delayedTicks, explosionPower);
        }
    }
}
*/