package com.lancas.vswap.experiemental;

import com.simibubi.create.foundation.utility.worldWrappers.DummyLevelEntityGetter;
import net.minecraft.core.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.Nullable;

public class WrappedLevel extends Level {

    protected Level level;
    protected ChunkSource chunkSource;

    protected LevelEntityGetter<Entity> entityGetter = new DummyLevelEntityGetter<>();

    public WrappedLevel(Level level) {
        super((WritableLevelData) level.getLevelData(), level.dimension(), level.registryAccess(), level.dimensionTypeRegistration(),
            level::getProfiler, level.isClientSide, level.isDebug(), 0, 0);
        this.level = level;
    }

    public void setChunkSource(ChunkSource source) {
        this.chunkSource = source;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public BlockState getBlockState(@Nullable BlockPos pos) {
        return level.getBlockState(pos);
    }

    @Override
    public boolean isStateAtPosition(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
        return level.isStateAtPosition(p_217375_1_, p_217375_2_);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
        return level.setBlock(pos, newState, flags);
    }

    @Override
    public int getMaxLocalRawBrightness(BlockPos pos) {
        return 15;
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        level.sendBlockUpdated(pos, oldState, newState, flags);
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return level.getBlockTicks();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return level.getFluidTicks();
    }

    @Override
    public ChunkSource getChunkSource() {
        return chunkSource != null ? chunkSource : level.getChunkSource();
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {}

    @Override
    public List<? extends Player> players() {
        return Collections.emptyList();
    }

    @Override
    public void playSeededSound(Player pPlayer, double pX, double pY, double pZ, Holder<SoundEvent> pSound,
                                SoundSource pSource, float pVolume, float pPitch, long pSeed) {}

    @Override
    public void playSeededSound(Player pPlayer, Entity pEntity, Holder<SoundEvent> pSound, SoundSource pCategory,
                                float pVolume, float pPitch, long pSeed) {}

    @Override
    public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent soundIn,
                          SoundSource category, float volume, float pitch) {}

    @Override
    public void playSound(@Nullable Player p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_,
                          SoundSource p_217384_4_, float p_217384_5_, float p_217384_6_) {}

    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public MapItemSavedData getMapData(String mapName) {
        return null;
    }

    /*@Override
    public boolean addFreshEntity(Entity entityIn) {
        ((EntityAccessor) entityIn).catnip$callSetLevel(level);
        return level.addFreshEntity(entityIn);
    }*/

    @Override
    public void setMapData(String pMapId, MapItemSavedData pData) {}

    @Override
    public int getFreeMapId() {
        return level.getFreeMapId();
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {}

    @Override
    public Scoreboard getScoreboard() {
        return level.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return level.getRecipeManager();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
        return level.getUncachedNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
    }

    @Override
    public RegistryAccess registryAccess() {
        return level.registryAccess();
    }

    @Override
    public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
        return level.getShade(p_230487_1_, p_230487_2_);
    }

    @Override
    public void updateNeighbourForOutputSignal(BlockPos p_175666_1_, Block p_175666_2_) {}

    @Override
    public void gameEvent(Entity pEntity, GameEvent pEvent, BlockPos pPos) {}

    @Override
    public void gameEvent(GameEvent p_220404_, Vec3 p_220405_, GameEvent.Context p_220406_) {}

    @Override
    public String gatherChunkSourceStats() {
        return level.gatherChunkSourceStats();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return entityGetter;
    }

    // Intentionally copied from LevelHeightAccessor. Workaround for issues caused
    // when other mods (such as Lithium)
    // override the vanilla implementations in ways which cause WrappedWorlds to
    // return incorrect, default height info.
    // WrappedWorld subclasses should implement their own getMinBuildHeight and
    // getHeight overrides where they deviate
    // from the defaults for their dimension.

    @Override
    public int getMaxBuildHeight() {
        return this.getMinBuildHeight() + this.getHeight();
    }

    @Override
    public int getSectionsCount() {
        return this.getMaxSection() - this.getMinSection();
    }

    @Override
    public int getMinSection() {
        return SectionPos.blockToSectionCoord(this.getMinBuildHeight());
    }

    @Override
    public int getMaxSection() {
        return SectionPos.blockToSectionCoord(this.getMaxBuildHeight() - 1) + 1;
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return this.isOutsideBuildHeight(pos.getY());
    }

    @Override
    public boolean isOutsideBuildHeight(int y) {
        return y < this.getMinBuildHeight() || y >= this.getMaxBuildHeight();
    }

    @Override
    public int getSectionIndex(int y) {
        return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(y));
    }

    @Override
    public int getSectionIndexFromSectionY(int sectionY) {
        return sectionY - this.getMinSection();
    }

    @Override
    public int getSectionYFromSectionIndex(int sectionIndex) {
        return sectionIndex + this.getMinSection();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return level.enabledFeatures();
    }
}