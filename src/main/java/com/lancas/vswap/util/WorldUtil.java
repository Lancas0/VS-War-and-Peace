package com.lancas.vswap.util;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.apigame.world.ShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.function.Function;

public class WorldUtil {
    public static class Lightweight {
        /*private BlockState setBlockInSection(ServerLevel level, LevelChunk chunk, LevelChunkSection section, BlockPos realBp, BlockState state) {
            //temp
            boolean p_62867_ = true;
            ChunkAccessor chunkAccessor = (ChunkAccessor)chunk;

            int i = realBp.getY();
            boolean flag = section.hasOnlyAir();
            if (flag && state.isAir()) {
                return null;
                int j = realBp.getX() & 15;
                int k = i & 15;
                int l = realBp.getZ() & 15;
                BlockState blockstate = section.setBlockState(j, k, l, state);
                if (blockstate == state) {
                    return null;
                } else {
                    Block block = state.getBlock();
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.MOTION_BLOCKING)).update(j, i, l, state);
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(j, i, l, state);
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.OCEAN_FLOOR)).update(j, i, l, state);
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.WORLD_SURFACE)).update(j, i, l, state);
                    boolean flag1 = section.hasOnlyAir();
                    if (flag != flag1) {
                        level.getChunkSource().getLightEngine().updateSectionStatus(realBp, flag1);
                    }

                    if (LightEngine.hasDifferentLightProperties(chunk, realBp, blockstate, state)) {
                        ProfilerFiller profilerfiller = level.getProfiler();
                        profilerfiller.push("updateSkyLightSources");
                        chunk.getSkyLightSources().update(chunk, j, i, l);
                        profilerfiller.popPush("queueCheckLight");
                        level.getChunkSource().getLightEngine().checkBlock(realBp);
                        profilerfiller.pop();
                    }

                    boolean flag2 = blockstate.hasBlockEntity();
                    if (!level.isClientSide) {
                        blockstate.onRemove(level, realBp, state, p_62867_);
                    } else if ((!blockstate.is(block) || !state.hasBlockEntity()) && flag2) {
                        chunk.removeBlockEntity(realBp);
                    }

                    if (!section.getBlockState(j, k, l).is(block)) {
                        return null;
                    } else {
                        if (!level.isClientSide && !level.captureBlockSnapshots) {
                            state.onPlace(level, realBp, blockstate, p_62867_);
                        }

                        if (state.hasBlockEntity()) {
                            BlockEntity blockentity = chunk.getBlockEntity(realBp, LevelChunk.EntityCreationType.CHECK);
                            if (blockentity == null) {
                                blockentity = ((EntityBlock)block).newBlockEntity(realBp, state);
                                if (blockentity != null) {
                                    chunk.addAndRegisterBlockEntity(blockentity);
                                }
                            } else {
                                blockentity.setBlockState(state);
                                chunk.updateBlockEntityTicker(blockentity);
                            }
                        }

                        chunk.setUnsaved(true);
                        return blockstate;
                    }
                }
        }*/
        /*
        public BlockState setBlocksInSection(ServerLevel level, LevelChunk chunk, LevelChunkSection section, BlockPos pos, BlockState state) {
            ChunkAccessor chunkAccessor = (ChunkAccessor)chunk;

            int i = pos.getY();
            boolean flag = section.hasOnlyAir();
            if (flag && state.isAir()) {
                return null;
            } else {
                int j = pos.getX() & 15;
                int k = i & 15;
                int l = pos.getZ() & 15;
                BlockState blockstate = section.setBlockState(j, k, l, state);
                if (blockstate == state) {
                    return null;
                } else {
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.MOTION_BLOCKING)).update(j, i, l, state);
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(j, i, l, state);
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.OCEAN_FLOOR)).update(j, i, l, state);
                    (chunkAccessor.getHeightmaps().get(Heightmap.Types.WORLD_SURFACE)).update(j, i, l, state);
                    boolean flag1 = section.hasOnlyAir();
                    if (flag != flag1) {
                        level.getChunkSource().getLightEngine().updateSectionStatus(pos, flag1);
                    }

                    if (LightEngine.hasDifferentLightProperties(chunk, pos, blockstate, state)) {
                        ProfilerFiller profilerfiller = level.getProfiler();
                        profilerfiller.push("updateSkyLightSources");
                        chunk.getSkyLightSources().update(this, j, i, l);
                        profilerfiller.popPush("queueCheckLight");
                        level.getChunkSource().getLightEngine().checkBlock(pos);
                        profilerfiller.pop();
                    }

                    boolean flag2 = blockstate.hasBlockEntity();
                    if (!level.isClientSide) {
                        blockstate.onRemove(level, pos, state, p_62867_);
                    } else if ((!blockstate.is(block) || !state.hasBlockEntity()) && flag2) {
                        chunk.removeBlockEntity(pos);
                    }

                    if (!section.getBlockState(j, k, l).is(block)) {
                        return null;
                    } else {
                        if (!level.isClientSide && !level.captureBlockSnapshots) {
                            state.onPlace(level, pos, blockstate, p_62867_);
                        }

                        if (state.hasBlockEntity()) {
                            BlockEntity blockentity = chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
                            if (blockentity == null) {
                                blockentity = ((EntityBlock)block).newBlockEntity(pos, state);
                                if (blockentity != null) {
                                    this.addAndRegisterBlockEntity(blockentity);
                                }
                            } else {
                                blockentity.setBlockState(state);
                                this.updateBlockEntityTicker(blockentity);
                            }
                        }

                        chunk.setUnsaved(true);
                        return blockstate;
                    }
                }
            }*/
        //}
    }
    /*public static ShipObjectServerWorldAccessor shipWorldAccessorOf(ServerLevel level) {
        return (ShipObjectServerWorldAccessor) VSGameUtilsKt.getShipObjectWorld(level);
    }*/
    public static int midY(Level level) { return level.getHeight() / 2 + level.getMinBuildHeight(); }

    public static ShipWorldCore shipWorldOf(Level level) {
        return VSGameUtilsKt.getShipObjectWorld(level);
    }
    public static ServerShipWorldCore shipWorldOf(ServerLevel level) {
        return VSGameUtilsKt.getShipObjectWorld(level);
    }

    public static <T> T getBlockInterface(Level level, BlockPos at, @Nullable Dest<BlockState> stateDest) {
        BlockState state = level.getBlockState(at);
        Dest.setIfExistDest(stateDest, state);

        try {
            return (T)state.getBlock();
        } catch (Exception e) {
            EzDebug.warn("fail to get blockInterface. block:" + StrUtil.getBlockName(state));
            return null;
        }
    }
    public static Vector3d getWorldCenter(Level level, BlockPos bp) {
        Ship ship = ShipUtil.getShipAt(level, bp);
        if (ship == null) return JomlUtil.dCenter(bp);

        return JomlUtil.dWorldCenter(ship.getShipToWorld(), bp);
    }
    public static Vector3d getWorldPos(Level level, Vector3dc pos, Vector3d dest) {
        Ship ship = ShipUtil.getShipAt(level, JomlUtil.bpContaining(pos));
        if (ship == null) return dest.set(pos);

        return ship.getShipToWorld().transformPosition(pos, dest);
    }
    public static Vector3d getWorldCenter(@Nullable Ship ship, BlockPos bp) {
        if (ship == null) return JomlUtil.dCenter(bp);
        return JomlUtil.dWorldCenter(ship.getShipToWorld(), bp);
    }
    public static Vector3d getWorldDirection(Level level, BlockPos bp, Direction dir) {
        Ship ship = ShipUtil.getShipAt(level, bp);
        return getWorldDirection(ship, dir);
    }
    public static Vector3d getWorldDirection(@Nullable Ship ship, Direction dir) {
        if (ship == null) return JomlUtil.dNormal(dir);
        return JomlUtil.dWorldNormal(ship.getShipToWorld(), dir);
    }
    public static Vector3d getWorldFaceCenter(Level level, BlockPos bp, Direction face) {
        Ship ship = ShipUtil.getShipAt(level, bp);
        if (ship == null) return JomlUtil.relativeFromCenter(bp, face, 0.5);

        return ship.getShipToWorld().transformPosition(JomlUtil.relativeFromCenter(bp, face, 0.5));
    }

    public static BlockPos getWorldBp(Level level, BlockPos bp) {
        Ship ship = ShipUtil.getShipAt(level, bp);
        if (ship == null) return bp;
        return JomlUtil.bpContaining(JomlUtil.dWorldCenter(ship.getShipToWorld(), bp));
    }
    public static BlockPos getWorldBp(Level level, Vector3dc pos) {
        BlockPos bp = JomlUtil.bpContaining(pos);
        Ship ship = ShipUtil.getShipAt(level, bp);
        if (ship == null) return bp;
        return JomlUtil.bpContaining(ship.getShipToWorld().transformPosition(pos, new Vector3d()));
    }
    public static double getScaleOfShipOrWorld(Level level, BlockPos bp) {
        Ship ship = ShipUtil.getShipAt(level, bp);
        if (ship == null) return 1;
        return ship.getTransform().getShipToWorldScaling().x();
    }

    public static void copyBlock(Level level, BlockPos from, BlockPos to) {
        BlockState state = level.getBlockState(from);
        BlockEntity blockentity = level.getBlockEntity(from);
        level.getChunk(to).setBlockState(to, state, false);

        // Transfer pending schedule-ticks
        if (level.getBlockTicks().hasScheduledTick(from, state.getBlock())) {
            level.getBlockTicks().schedule(new ScheduledTick<Block>(state.getBlock(), to, 0, 0));
        }

        // Transfer block-entity data
        if (state.hasBlockEntity() && blockentity != null) {
            CompoundTag data = blockentity.saveWithId();
            level.setBlockEntity(blockentity);
            BlockEntity newBlockentity = level.getBlockEntity(to);
            newBlockentity.load(data);
        }

        EzDebug.log("copy " + state.getBlock().getName().getString() + " from " + from.toShortString() + " to " + to.toShortString());
    }

    public static void updateBlock(Level level, BlockPos pos) {
        // 75 = flag 1 (block update) & flag 2 (send to clients) + flag 8 (force rerenders)
        int flags = 11;
        //updateNeighbourShapes recurses through nearby blocks, recursionLeft is the limit
        int recursionLeft = 511;


        BlockState state = level.getBlockState(pos);
        BlockState airState = Blocks.AIR.defaultBlockState();

        level.setBlocksDirty(pos, airState, state);
        level.sendBlockUpdated(pos, airState, state, flags);
        level.blockUpdated(pos, state.getBlock());
        if (!level.isClientSide && state.hasAnalogOutputSignal()) {
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
        //This updates lighting for blocks in shipspace
        level.getChunkSource().getLightEngine().checkBlock(pos);
    }

    public static void updateBlock(Level level, BlockPos fromPos, BlockPos toPos, BlockState toState) {
        // 75 = flag 1 (block update) & flag 2 (send to clients) + flag 8 (force rerenders)
        int flags = 11;
        //updateNeighbourShapes recurses through nearby blocks, recursionLeft is the limit
        int recursionLeft = 511;

        BlockState airSt = Blocks.AIR.defaultBlockState();

        level.setBlocksDirty(fromPos, toState, airSt);
        level.sendBlockUpdated(fromPos, toState, airSt, flags);
        level.blockUpdated(fromPos, airSt.getBlock());

        // This handles the update for neighboring blocks in worldspace
        airSt.updateIndirectNeighbourShapes(level, fromPos, flags, recursionLeft - 1);
        airSt.updateNeighbourShapes(level, fromPos, flags, recursionLeft);
        airSt.updateIndirectNeighbourShapes(level, fromPos, flags, recursionLeft);
        //This updates lighting for blocks in worldspace
        level.getChunkSource().getLightEngine().checkBlock(fromPos);

        level.setBlocksDirty(toPos, airSt, toState);
        level.sendBlockUpdated(toPos, airSt, toState, flags);
        level.blockUpdated(toPos, toState.getBlock());
        if (!level.isClientSide && toState.hasAnalogOutputSignal()) {
            level.updateNeighbourForOutputSignal(toPos, toState.getBlock());
        }
        //This updates lighting for blocks in shipspace
        level.getChunkSource().getLightEngine().checkBlock(toPos);
    }

    public static void removeBlock(Level level, BlockPos pos) {
        level.removeBlockEntity(pos);
        level.getChunk(pos).setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
    }

    public static void setBlock(Level level, BlockPos pos, BlockState state, @Nullable CompoundTag entityTag) {
        level.setBlock(pos, state, Block.UPDATE_ALL);

        //恢复方块实体数据
        if (level.getBlockEntity(pos) != null) {
            if (entityTag == null) {
                EzDebug.warn("block " + StrUtil.getBlockName(state) + " has be but with param null BeNbt");
            } else {
                BlockEntity blockEntity = BlockEntity.loadStatic(pos, state, entityTag);
                if (blockEntity != null) {
                    level.setBlockEntity(blockEntity);
                }
            }
        }

    }

    public static void updateBlockStateOfBe(BlockEntity be, BlockState newState) {
        Level level = be.getLevel();
        if (level == null) {
            EzDebug.warn("try updateBlockStateOfBe but level is null");
            return;
        }
        level.setBlockAndUpdate(be.getBlockPos(), newState);
    }
    public static void updateBlockStateOfBe(BlockEntity be, Function<BlockState, BlockState> stateTransformer) {
        Level level = be.getLevel();
        if (level == null) {
            EzDebug.warn("try updateBlockStateOfBe but level is null");
            return;
        }
        BlockState newState = stateTransformer.apply(level.getBlockState(be.getBlockPos()));
        level.setBlockAndUpdate(be.getBlockPos(), newState);
    }
}
