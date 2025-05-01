package com.lancas.vs_wap.content.block.blocks.explosive;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class C4ExplosiveBlock extends AbstractExplosiveBlock {
    public static final String ID = "c4_explosive_block";
    public static int EXPLOSION_POWER = 10;

    public C4ExplosiveBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void explodeByRedstone(ServerLevel level, BlockPos pos, BlockState state) { /*explode(level, pos, state);*/ }
    @Override
    public void explodeByExplosion(ServerLevel level, BlockPos pos, BlockState state) { explode(level, pos, state); }


    public void explode(ServerLevel level, BlockPos pos, BlockState state) {
        AbstractExplosiveBlock.simpleExplosion(level, pos, EXPLOSION_POWER);
    }
}
