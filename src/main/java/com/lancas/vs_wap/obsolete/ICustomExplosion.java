package com.lancas.vs_wap.obsolete;

/*
public interface ICustomExplosion {
    //todo currently do not drop item
    //public float getItemDropProb();

    public void gatherToBlowBlocks(@NotNull List<BlockPos> blockPoses, Vec3 pos);
    public void gatherEffectedEntity(@NotNull List<LivingEntity> entities, Vec3 pos);
    //todo impact item entity

    public default void effectBlock(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.isAir()) return;

        //state.onBlockExploded();

        //level.getProfiler().push("explosion_blocks");
        /.*if (state.canDropFromExplosion(level, pos, this)) {
            ServerLevel serverlevel = (ServerLevel)$$9;
            BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
            if (this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
            }

            state.spawnAfterBreak(serverlevel, pos, ItemStack.EMPTY, flag1);
            state.getDrops(lootparams$builder).forEach((p_46074_) -> addBlockDrops(objectarraylist, p_46074_, blockpos1));
        }*./

        //blockstate.onBlockExploded(this.level, blockpos, this);
        //this.level.getProfiler().pop();
    }

    public default void createExplosionParticles(Level level, Vec3 pos) {
        level.addParticle(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 1.0, 0.0, 0.0);
    }
    public default void playSound(ClientLevel level, Vec3 pos) {
        level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F, false);
    }


    public default void explode(Level level, Vec3 pos) {  //pos is not the attribute of explosion itself so can be used everywhere with a single reference
        if (level.isClientSide) {
            playSound((ClientLevel)level, pos);
        } else {
            //create explosion particles in server level and game will handle it automatically
            //createExplosionParticles(level);
        }
    }

}
*/