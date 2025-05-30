package com.lancas.vswap.subproject.sandbox.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class ShipServerWorld extends ServerLevel {
    private final Map<Vector3ic, BlockEntity> blockEntities = new ConcurrentHashMap<>();


    public ShipServerWorld(
        MinecraftServer p_214999_,
        Executor p_215000_,
        LevelStorageSource.LevelStorageAccess p_215001_,
        ServerLevelData p_215002_,
        ResourceKey<Level> p_215003_,
        LevelStem p_215004_,
        ChunkProgressListener p_215005_,
        boolean p_215006_,
        long p_215007_,
        List<CustomSpawner> p_215008_,
        boolean p_215009_,
        @Nullable RandomSequences p_288977_
    ) {
        super(p_214999_, p_215000_, p_215001_, p_215002_, p_215003_, p_215004_, p_215005_, p_215006_, p_215007_, p_215008_, p_215009_, p_288977_);
    }


}
