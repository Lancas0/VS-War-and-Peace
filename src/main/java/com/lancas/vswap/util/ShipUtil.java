package com.lancas.vswap.util;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.ship.attachment.ShipBytesDataAttachment;
import com.lancas.vswap.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.apache.logging.log4j.util.Supplier;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ShipUtil {
    @Nullable
    public static Ship getShipAt(Level level, BlockPos pos) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);
        return ship == null ? VSGameUtilsKt.getShipManagingPos(level, pos) : ship;
    }
    @Nullable
    public static ServerShip getServerShipAt(ServerLevel level, BlockPos pos) {
        ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);
        return ship == null ? VSGameUtilsKt.getShipManagingPos(level, pos) : ship;
    }
    @Nullable
    public static Ship getShipByID(Level level, @Nullable Long id) {
        if (id == null || id < 0) return null;
        return VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(id);
    }
    public static long getShipOrGroundId(ServerLevel level, @Nullable Ship ship) {
        if (ship == null)
            return getGroundId(level);
        return ship.getId();
    }
    public static long getShipOrGroundIdAt(ServerLevel level, BlockPos pos) {
        Ship ship = getShipAt(level, pos);
        if (ship == null)
            return getGroundId(level);
        return ship.getId();
    }
    public static boolean getBlockInShipId(Level level, BlockPos pos, Dest<Long> idDest) {
        Ship ship = getShipAt(level, pos);
        if (ship == null)
            return false;

        idDest.set(ship.getId());
        return true;
    }
    @Nullable
    public static ClientShip getClientShipByID(ClientLevel level, @Nullable Long id) {
        if (id == null || id < 0) return null;
        return VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(id);
    }
    @Nullable
    public static ServerShip getServerShipByID(ServerLevel level, Long id) {
        if (id == null || id < 0) return null;
        return VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(id);
    }
    @Nullable
    public static LoadedServerShip getLoadedServerByID(ServerLevel level, Long id) {
        if (id == null || id < 0) return null;
        return VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(id);
    }
    @Nullable
    public static Ship getLoadedShipByID(Level level, Long id) {
        if (id == null || id < 0) return null;
        return VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(id);
    }
    public static boolean tryDeleteShipById(ServerLevel level, Long id) {
        ServerShip ship = (ServerShip)getShipByID(level, id);
        if (ship == null) return false;

        VSGameUtilsKt.getShipObjectWorld(level).deleteShip(ship);
        return true;
    }

    public static void deleteShip(ServerLevel level, ServerShip ship) {
        if (ship == null) return;
        VSGameUtilsKt.getShipObjectWorld(level).deleteShip(ship);
    }
    public static void deleteShipById(ServerLevel level, long shipId) {
        ServerShip ship = getServerShipByID(level, shipId);
        if (ship != null)
            VSGameUtilsKt.getShipObjectWorld(level).deleteShip(ship);
    }

    public static Long getGroundId(ServerLevel level) {
        if (level == null) return null;
        return VSGameUtilsKt.getShipObjectWorld(level).getDimensionToGroundBodyIdImmutable().get(VSGameUtilsKt.getDimensionId(level));
    }

    public static long idOrNegate1(@Nullable Ship ship) {
        if (ship == null) return -1;
        return ship.getId();
    }
    public static long idOfShipOrGround(ServerLevel level, @Nullable Ship ship) {
        if (ship == null)
            return getGroundId(level);
        return ship.getId();
    }

    public static void teleport(ServerLevel level, ServerShip ship, ShipTeleportData teleportData) {
        if (ship == null || level == null) {
            EzDebug.warn("can't teleport ship because ship is null or level is null");
            return;
        }

        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(
            ship,
            teleportData
        );
    }

    public static Vector3d getShipFaceCenterInShip(Ship ship, Direction face) {
        AABBic aabbInShip = ship.getShipAABB();
        if (aabbInShip == null) {
            EzDebug.error("aabbInShip is null");
            return new Vector3d();
        }

        Vector3d faceCenterInShip = aabbInShip.center(new Vector3d());
        switch (face) {
            case UP -> faceCenterInShip.setComponent(1, aabbInShip.maxY());  //xz upper face, set y max
            case DOWN -> faceCenterInShip.setComponent(1, aabbInShip.minY());
            case SOUTH -> faceCenterInShip.setComponent(2, aabbInShip.maxZ());  //xy forward face, set z max
            case NORTH -> faceCenterInShip.setComponent(2, aabbInShip.minZ());
            case EAST -> faceCenterInShip.setComponent(0, aabbInShip.maxX());  //yz left face, set x max
            case WEST -> faceCenterInShip.setComponent(0, aabbInShip.minX());
        }
        return faceCenterInShip;
    }
    public static Vector3d getShipFaceCenterInWorld(Ship ship, Direction face) {
        Vector3d faceCenterInShip = getShipFaceCenterInShip(ship, face);
        return
            ship instanceof ClientShip cShip ?
                cShip.getRenderTransform().getShipToWorld().transformPosition(faceCenterInShip) :
                ship.getShipToWorld().transformPosition(faceCenterInShip);
    }
    public static Vector3d getShipGeometryCenterInWorld(Ship ship) {
        return ship.getWorldAABB().center(new Vector3d());  //todo I guess it is right
    }
    public static Vector3d getShipGeometryCenterInShip(Ship ship) {
        if (ship.getShipAABB() == null)
            return null;
        return ship.getShipAABB().center(new Vector3d());  //todo I guess it is right
    }

    /*public static void applyImpulse(ServerShip ship, Vector3dc impulse) {
        //todo
    }*/
    //func arg1 is blockPos
    //func arg2 is current blockstate
    //func arg3 is current blockentity
    public static void foreachBlock(ServerShip ship, ServerLevel level, TriConsumer<BlockPos, BlockState, BlockEntity> func) {
        if (ship == null || level == null) return;

        //BlockPos shipCenterBP = ShipUtil.getCenterShipBP(ship);

        ship.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
            LevelChunk chunk = level.getChunk(chunkX, chunkZ);

            for (int i = chunk.getSections().length - 1; i >= 0; --i) {
                LevelChunkSection section = chunk.getSection(i);
                if (section.hasOnlyAir()) continue;

                int bottomY = i << 4;
                for (int x = 0; x <= 15; ++x)
                    for (int y = 0; y <= 15; ++y)
                        for (int z = 0; z <= 15; ++z) {
                            BlockState state = section.getBlockState(x, y, z);
                            //ignore air block
                            if (state.isAir()) continue;

                            int realX = (chunkX << 4) + x;
                            int realY = bottomY + y + level.getMinBuildHeight();
                            int realZ = (chunkZ << 4) + z;

                            BlockPos blockPos = new BlockPos(realX, realY, realZ);
                            BlockEntity blockEntity = level.getBlockEntity(blockPos);

                            func.accept(blockPos, state, blockEntity);
                        }
            }
        });
    }

    public static void foreachSection(ServerShip ship, ServerLevel level, QuadConsumer<Integer, Integer, Integer, LevelChunkSection> consumer) {
        if (ship == null || level == null) return;
        ship.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
            LevelChunk chunk = level.getChunk(chunkX, chunkZ);

            for (int i = chunk.getSections().length - 1; i >= 0; --i) {
                LevelChunkSection section = chunk.getSection(i);
                if (section.hasOnlyAir()) continue;

                consumer.apply(chunkX, chunkZ, i, section);
            }
        });
    }
    public static void foreachChunk(ServerShip ship, ServerLevel level, TriConsumer<Integer, Integer, LevelChunk> consumer) {
        if (ship == null || level == null) return;
        ship.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
            LevelChunk chunk = level.getChunk(chunkX, chunkZ);  //todo check if chunk has only air?
            consumer.accept(chunkX, chunkZ, chunk);
        });
    }

    /*public static ServerShip createShipAt(ServerLevel level, BlockPos pos, double scale) {
        ServerShip ship =
            VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(
                JomlUtil.i(pos),
                false,
                scale,
                VSGameUtilsKt.getDimensionId(level)
            );
        BlockPos centerPos = JomlUtil.bp(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i()));

        // Move the block from the world to a ship
        RelocationUtilKt.relocateBlock(level, pos, centerPos, true, ship, Rotation.NONE);
        return ship;
    }*/
    public static BlockPos getCenterShipBP(Level level, Ship ship) {
        /*Vector3dc shipCenter = ship.getTransform().getPositionInShip();//ship.getWorldToShip().transformPosition(ship.getTransform().getPositionInWorld(), new Vector3d());
        return new BlockPos(
            (int)Math.floor(shipCenter.x()),
            (int)Math.floor(shipCenter.y()),
            (int)Math.floor(shipCenter.z())
        );*/
        Vector3i center = new Vector3i();

        ship.getChunkClaim()
            .getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), center);

        return JomlUtil.bp(center);
    }
    /*public static BlockPos getShipyardCenterBP(ServerShip ship) {
        if (ship == null) return null;
        Vector3dc shipyardCenter = ship.getTransform().getPositionInShip().add(0.5, 0.5, 0.5, new Vector3d());
        return PosUtil.toBlockPos(shipyardCenter);
    }*/

    public static void writeTag(@NotNull ServerShip ship, @NotNull CompoundTag tag) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            NbtIo.writeCompressed(tag, buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ShipBytesDataAttachment.getOrAdd(ship).bytes = buffer.toByteArray();
    }

    public static CompoundTag readTag(@NotNull ServerShip ship) {
        ShipBytesDataAttachment attachment = ship.getAttachment(ShipBytesDataAttachment.class);

        if (attachment == null)
            return null;

        ByteArrayInputStream buffer = new ByteArrayInputStream(attachment.bytes);
        CompoundTag tag;
        try {
            tag = NbtIo.readCompressed(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tag;
    }

    public static <T> T computeIfAbsent(ServerShip ship, Class<T> type, Supplier<T> attachSupplier) {
        T attchment = ship.getAttachment(type);
        if (attchment == null) {
            attchment = attachSupplier.get();
            ship.saveAttachment(type, attchment);
        }
        return attchment;
    }


    public static Long safeIdOf(@Nullable Ship ship) { return ship == null ? null : ship.getId(); }
}
