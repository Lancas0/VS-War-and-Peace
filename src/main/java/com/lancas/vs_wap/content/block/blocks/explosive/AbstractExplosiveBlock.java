package com.lancas.vs_wap.content.block.blocks.explosive;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractExplosiveBlock extends Block {
    public static void simpleExplosion(ServerLevel level, BlockPos pos, int power) {
        level.removeBlock(pos, false);
        level.explode(null,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            power,
            Level.ExplosionInteraction.BLOCK
        );
    }


    public AbstractExplosiveBlock(Properties p_49795_) {
        super(p_49795_);
    }


    public void explodeByRedstone(ServerLevel level, BlockPos pos, BlockState state) {}
    public void explodeByExplosion(ServerLevel level, BlockPos pos, BlockState state) {}

    //public boolean explsive by fire (together with flint and steel)
    //public abstract boolean explosiveByRedstone();// { return true; }
    //public abstract boolean explosiveByExplosion();//{ return true; }
    //public abstract int getExplosionPower();
    //public abstract int getExplsionRange();

    //public abstract void explode(ServerLevel level, BlockPos pos, BlockState state);


    //todo check redstone signal at place
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal) {
            explodeByRedstone((ServerLevel)level, pos, state);
        }
    }
}
