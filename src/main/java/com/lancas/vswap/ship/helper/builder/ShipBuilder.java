package com.lancas.vswap.ship.helper.builder;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.register.ServerShipEvent;
import com.lancas.vswap.ship.data.IShipSchemeData;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.WorldUtil;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class ShipBuilder {
    private ServerShip ship;
    //public final BlockPos shipyardCenterBP;
    private final ServerLevel level;
    private boolean canceled = false;

    private final BlockPos initialBP;
    private final Vector3dc localOrigin;  //the center of initalBP

    //todo may I can make fake ship data to pre calculate ship data before ship is created
    //todo if create immediately, sometimes will crash
    public ShipBuilder(BlockPos pos, @NotNull ServerLevel inLevel, double scale, boolean immediate) {
        /*pos = inPos;
        dimID = inDimID;
        scale = inScale;
        ServerShip newShip = VSGameUtilsKt.getShipObjectWorld(sLevel).createNewShipAtBlock(
            JomlUtil.i(pos.above()),
            false,
            1.0,
            VSGameUtilsKt.getDimensionId(sLevel)
        );*/
        level = inLevel;
        ship = VSGameUtilsKt.getShipObjectWorld(level)
            .createNewShipAtBlock(JomlUtil.i(pos), immediate, scale, VSGameUtilsKt.getDimensionId(level));


        initialBP = JomlUtil.bpContaining(ship.getTransform().getPositionInShip()); //ShipUtil.getCenterShipBP(ship); todo use real center coordinate
        localOrigin = JomlUtil.dCenter(initialBP);
        Vector3dc initalWP = ship.getTransform().getPositionInWorld();

        /*Vector3dc shipPosCenter = ship.getTransform().getPositionInShip();//ship.getWorldToShip().transformPosition(ship.getTransform().getPositionInWorld(), new Vector3d());
        initalBP = new BlockPos(
            (int)Math.floor(shipPosCenter.x()),
            (int)Math.floor(shipPosCenter.y()),
            (int)Math.floor(shipPosCenter.z())
        );*/

        /*Vector3dc shipPosCenter = ship.getTransform().getPositionInShip();//ship.getWorldToShip().transformPosition(ship.getTransform().getPositionInWorld(), new Vector3d());
        BlockPos myShipPos = new BlockPos(
            (int)Math.floor(shipPosCenter.x()),
            (int)Math.floor(shipPosCenter.y()),
            (int)Math.floor(shipPosCenter.z())
        );
        EzDebug.Log("official ship pos is " + StringUtil.toF2String(ship.getTransform().getPositionInShip()) + "my ship pos is " + myShipPos);*/

        //shipyardCenterBP = myShipPos;

        //shipyardCenterBP = JomlUtil.bp(ship.getTransform().getPositionInShip());

        //ShipUtil.getShipyardCenterBP()
        //shipyardCenterBP = ShipUtil.getShipyardCenterBP(ship);
        /*    new Vector3d(
            Math.round(ship.getTransform().getPositionInShip().x() - 0.5),
            Math.round(ship.getTransform().getPositionInShip().y() - 0.5),
            Math.round(ship.getTransform().getPositionInShip().z() - 0.5)
        );*/
    }
    /*public ShipBuilder(BlockPos pos, @NotNull ServerLevel inLevel, @NotNull ServerShip srcShip) {

    }*/
    /*public ShipBuilder(BlockPos pos, @NotNull ServerLevel inLevel, @NotNull IShipSchemeData schemeData) {
        level = inLevel;
        ship = schemeData.createShip(level);

        initialBP = ShipUtil.getCenterShipBP(ship);
        localOrigin = JomlUtil.dCenter(initialBP);
        Vector3dc initalWP = ship.getTransform().getPositionInWorld();

        /.*schemeData.forEach(level, (offset, state, beNbt) -> {
            if (beNbt == null)
                addBlock(offset, state);
            else
                addBlock(offset, state, beNbt);
        });*./
    }*/

    private ShipBuilder(ServerShip inShip, ServerLevel inLevel, BlockPos inInitialBp) {
        ship = inShip;
        level = inLevel;
        initialBP = inInitialBp;
        localOrigin = JomlUtil.dCenter(initialBP);
    }

    public static ShipBuilder copy(BlockPos pos, @NotNull ServerLevel inLevel, @NotNull ServerShip srcShip) {
        ShipBuilder builder = new ShipBuilder(pos, inLevel, srcShip.getTransform().getShipToWorldScaling().x(), false);

        ShipUtil.foreachBlock(srcShip, inLevel, (blockPos, state, blockEntity) -> {
            //EzDebug.Log("add block:" + pos + ", " + state.getBlock().getName().getString() + ", " + (blockEntity != null ? blockEntity.toString() : null));
            BlockPos offset = blockPos.subtract(builder.initialBP);

            if (blockEntity == null)
                builder.addBlockAtOffset(offset, state);
            else
                builder.addBlockAtOffset(offset, state, blockEntity.saveWithFullMetadata());
        });
        return builder;
    }
    public static ShipBuilder modify(ServerLevel level, ServerShip ship) {
        if (ship == null) return null;
        return new ShipBuilder(
            ship, level, JomlUtil.bpContaining(ship.getTransform().getPositionInShip())
        );
    }

    //all add is offset to initalBp
    public BlockPos getInitialBP() { return initialBP; }
    public Vector3dc getLocalOrigin() { return localOrigin; }

    /*@ApiStatus.Experimental
    public ShipBuilder reset() {
        ShipUtil.foreachBlock(ship, level, (pos, state, blockEntity) -> {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_NONE);
        });
        //experimental
        resetAttachment();

        ship.setTransformProvider(null);

        return this;
    }*/
    public ShipBuilder resetBlocks() {
        AtomicInteger resetCnt = new AtomicInteger();
        ShipUtil.foreachBlock(ship, level, (pos, state, blockEntity) -> {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            resetCnt.getAndIncrement();
        });

        EzDebug.warn("reset cnt:" + resetCnt);
        //resetBlocksOf(level, ship);
        return this;
    }
    /*public void resetBlocksOf(ServerLevel level, ServerShip ship) {
        if (ship == null) return;
        ShipUtil.foreachBlock(ship, level, (pos, state, blockEntity) -> {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_NONE);
        });
    }*/
    public ShipBuilder overwriteByScheme(IShipSchemeData shipSchemeData) {
        resetBlocks();

        /* (shipSchemeData == null) return this;
        shipSchemeData.forEach(level, (offset, state, beNbt) -> {
            if (beNbt == null)
                addBlock(offset, state);
            else
                addBlock(offset, state, beNbt);
        });*/
        shipSchemeData.overwriteEmptyShip(level, ship);
        return this;
    }
    public ShipBuilder foreachBlock(TriConsumer<BlockPos, BlockState, BlockEntity> action) {
        //EzDebug.Log("add block:" + pos + ", " + state.getBlock().getName().getString() + ", " + (blockEntity != null ? blockEntity.toString() : null));
        //BlockPos offset = blockPos.subtract(initialBP);
        ShipUtil.foreachBlock(ship, level, action);
        return this;
    }

    public boolean isEmpty() {
        AtomicBoolean empty = new AtomicBoolean(true);
        ShipUtil.foreachBlock(ship, level, (pos, state, blockEntity) -> {
            if (!empty.get()) return;
            if (!level.getBlockState(pos).isAir()) {
                empty.set(false);
            }
        });
        return empty.get();
    }
    public boolean isOnlyBlock() {
        AtomicInteger blockCnt = new AtomicInteger(0);

        ShipUtil.foreachBlock(ship, level, (pos, state, blockEntity) -> {
            if (blockCnt.get() >= 2) return;

            if (!level.getBlockState(pos).isAir()) {
                blockCnt.incrementAndGet();
            }
        });

        return blockCnt.get() == 1;
    }
    public int getBlockCnt() {
        AtomicInteger blockCnt = new AtomicInteger(0);

        ShipUtil.foreachBlock(ship, level, (pos, state, blockEntity) -> {
            if (!level.getBlockState(pos).isAir()) {
                blockCnt.incrementAndGet();
            }
        });

        return blockCnt.get();
    }
    /*@ApiStatus.Experimental
    public ShipBuilder resetAttachment() {
        resetAttachmentsOf(ship);
        return this;
    }

    public static ServerShip resetAttachmentsOf(ServerShip ship) {
        List<Class<?>> attachTypes = new ArrayList<>(
            ((ServerShipDataV4)(Object)ship).getPersistentAttachedData().keySet()
        );
        for (var type : attachTypes) {
            ship.saveAttachment(type, null);
        }
        return ship;
    }*/

    //todo cost effience!
    public ShipBuilder addBlockAtOffset(BlockPos offset, BlockState state) {
        if (canceled) return this;
        if (state.isAir()) return this;

        BlockPos realPos = initialBP.offset(offset);
        WorldUtil.setBlock(level, realPos, state, null);
        WorldUtil.updateBlock(level, realPos);
        //level.getChunk(realPos).setBlockState(realPos, state, true);

        return this;
    }
    public ShipBuilder addBlockAtOffset(BlockPos offset, BlockState state, CompoundTag beNbt) {
        if (canceled) return this;
        if (state.isAir()) return this;

        BlockPos realPos = initialBP.offset(offset);
        WorldUtil.setBlock(level, realPos, state, beNbt);
        WorldUtil.updateBlock(level, realPos);

        return this;
    }

    public ShipBuilder addBlockAtActual(BlockPos actual, BlockState state, @Nullable CompoundTag beNbt) {
        if (canceled) return this;
        if (state.isAir()) return this;

        WorldUtil.setBlock(level, actual, state, beNbt);
        WorldUtil.updateBlock(level, actual);

        return this;
    }



    public ShipBuilder copyBlock(BlockPos from, BlockPos offset, boolean removeOrigin) {
        if (canceled) return this;

        BlockState fromState = level.getBlockState(from);
        BlockEntity fromBe = level.getBlockEntity(from);

        if (fromState.isAir()) return this;
        if (fromBe == null) {
            addBlockAtOffset(offset, fromState);
        } else {
            addBlockAtOffset(offset, fromState, fromBe.saveWithFullMetadata());
        }

        if (removeOrigin) {
            level.setBlock(from, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        return this;
    }
    public ShipBuilder setNoCollisionWithPlayer() {
        if (canceled) return this;

        ship.setSlug(ship.getSlug() + "[player_no_collision]");
        return this;
    }
    public ShipBuilder setStatic(boolean isStatic) {
        if (canceled) return this;
        ship.setStatic(isStatic);
        return this;
    }
    public ShipBuilder setTransformProvider(ServerShipTransformProvider tp) {
        if (canceled) return this;
        ship.setTransformProvider(tp);
        return this;
    }

    public Vector3d calUpdatedShipPos() {
        return ship.getInertiaData().getCenterOfMassInShip().add(0.5, 0.5, 0.5, new Vector3d());
    }
    public Vector3d calUpdatedWorldPos() {
        return ship.getShipToWorld().transformPosition(calUpdatedShipPos());
    }

    /// be careful: the PosInShip and WorldPos of ShipTransform will not update immediately
    /// use other method or calUpdateShipPos(), calUpdatedWorldPos() to calculate the real pos
    public ShipBuilder setWorldPos(Vector3dc worldPos) {
        if (canceled) return this;

        //EzDebug.log(" to set worldPos is " + worldPos);
        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(
            ship,
            new ShipTeleportDataImpl(
                worldPos,
                ship.getTransform().getShipToWorldRotation(),
                ship.getVelocity(),
                ship.getOmega(),
                VSGameUtilsKt.getDimensionId(level),
                ship.getTransform().getShipToWorldScaling().x()
            )
        );

        //todo use teleport? use transform setting?
        /*ShipTransformImpl newTransform = new ShipTransformImpl(
            worldPos,
            ship.getTransform().getPositionInShip(),
            ship.getTransform().getShipToWorldRotation(),
            ship.getTransform().getShipToWorldScaling()
        );
        ((ShipDataCommon)ship).setTransform(newTransform);*/
        return this;
    }
    public ShipBuilder setWorldPos(Vec3 worldPos) {
        if (canceled) return this;
        return setWorldPos(JomlUtil.d(worldPos));
    }
    /*public Vector3d getGeometricalCenter(Vector3d dest) {
        AABBic shipAABB = ship.getShipAABB();
        if (shipAABB == null) return null;
        return shipAABB.center(dest);
    }
    public Vector3d getInitalBlockCenter() {
        return JomlUtil.dCenter(initalBP);
    }*/
    /*
    @Deprecated //todo private
    public ShipBuilder setWorldPos(Vec3 worldPos) {
        if (canceled) return this;
        return setWorldPos(JomlUtil.d(worldPos));
    }*/

    /*public ShipBuilder setRotation(Quaterniondc q) {
        if (canceled) return this;

        ShipTransformImpl newTransform = new ShipTransformImpl(
            ship.getTransform().getPositionInWorld(),
            ship.getTransform().getPositionInShip(),
            ship.getTransform().getShipToWorldRotation().mul(q, new Quaterniond()),
            ship.getTransform().getShipToWorldScaling()
        );
        ((ShipDataCommon)ship).setTransform(newTransform);
        return this;
    }*/
    public ShipBuilder rotate(Quaterniondc q) {
        if (canceled) return this;

        ShipTransformImpl newTransform = new ShipTransformImpl(
            ship.getTransform().getPositionInWorld(),
            ship.getTransform().getPositionInShip(),
            ship.getTransform().getShipToWorldRotation().mul(q, new Quaterniond()),
            ship.getTransform().getShipToWorldScaling()
        );
        ((ShipDataCommon)ship).setTransform(newTransform);
        return this;
    }
    public ShipBuilder setRotation(Quaterniondc q) {
        if (canceled) return this;

        ShipTransformImpl newTransform = new ShipTransformImpl(
            ship.getTransform().getPositionInWorld(),
            ship.getTransform().getPositionInShip(),
            q,
            ship.getTransform().getShipToWorldScaling()
        );
        ((ShipDataCommon)ship).setTransform(newTransform);
        return this;
    }
    public ShipBuilder rotateForwardTo(Vector3d newForward) {
        if (canceled) return this;
        return this.rotate(new Quaterniond().rotateTo(new Vector3d(0, 0, 1), newForward));
    }
    public ShipBuilder rotateForwardTo(Vec3 newForward) {
        if (canceled) return this;
        return this.rotateForwardTo(JomlUtil.d(newForward));
    }

    public ShipBuilder move(Vector3dc movement) {
        if (canceled) return this;
        //Vector3d worldPos = ship.getTransform().getPositionInWorld().get(new Vector3d());
        Vector3d updatedWorldPos = calUpdatedWorldPos();
        return setWorldPos(updatedWorldPos.add(movement));
    }
    public ShipBuilder moveShipPosToWorldPos(Vector3dc shipPos, Vector3dc moveTo) {
        if (canceled) return this;

        Vector3d localShipPos = shipPos.sub(localOrigin, new Vector3d());

        return moveLocalPosToWorldPos(localShipPos, moveTo);
    }
    public ShipBuilder moveLocalPosToWorldPos(Vector3dc localPos, Vector3dc moveTo) {
        if (canceled) return this;

        //Vector3d updatedWorldPos = calUpdatedWorldPos();
        //do not use updatedWorldPos because set world pos need unupdated worldPos
        Vector3d localPosInWorld = ship.getShipToWorld().transformPosition(localOrigin.add(localPos, new Vector3d()));
        Vector3d movement = moveTo.sub(localPosInWorld, new Vector3d());

        return move(movement);
    }

    /*public ShipBuilder moveGeometricPosToWorldPos(Vector3dc shipPos, Vector3dc moveTo) {
        if (canceled) return this;

        Vector3dc shipyardCenter = ship.getTransform().getPositionInShip();
        Vector3d shipCenter2geoCenter = getGeometricalCenter(new Vector3d()).sub(ship.getInertiaData().getCenterOfMassInShip());

        Vector3d worldPos = ship.getTransform().getShipToWorld().transformPosition(shipPos, new Vector3d());
        Vector3d movement = moveTo.sub(worldPos, new Vector3d()).sub(shipCenter2geoCenter);
        EzDebug.Log(
            "aabb:" + StringUtil.toFullString(ship.getShipAABB()) +
            "geoCenter:" + StringUtil.toF2String(getGeometricalCenter(new Vector3d())) +
            ", shipCenter:" + StringUtil.toF2String(shipyardCenter) +
            ", massCenter2geoCenter:" + StringUtil.toF2String(shipCenter2geoCenter)
        );
        return move(movement);
    }*/

    /*public ShipBuilder moveLocalPosToWorldPos(Vector3i offset, Vector3dc moveTo) {
        //todo not expected action
        return moveShipPosToWorldPos(
            JomlUtil.dCenter(shipyardCenterBP.offset(JomlUtil.bp(offset))),
            moveTo
        );
    }*/
    public ShipBuilder moveFaceTo(Direction dir, Vector3dc moveTo) {
        if (canceled) return this;
        AABBic aabb = ship.getShipAABB();
        if (aabb == null) return this;  //todo should not be called

        Vector3d faceCenterInShip = aabb.center(new Vector3d());
        switch (dir) {
            case UP -> faceCenterInShip.setComponent(1, aabb.maxY());  //xz upper face, set y max
            case DOWN -> faceCenterInShip.setComponent(1, aabb.minY());
            case SOUTH -> faceCenterInShip.setComponent(2, aabb.maxZ());  //xy forward face, set z max
            case NORTH -> faceCenterInShip.setComponent(2, aabb.minZ());
            case EAST -> faceCenterInShip.setComponent(0, aabb.maxX());  //yz left face, set x max
            case WEST -> faceCenterInShip.setComponent(0, aabb.minX());

            default -> faceCenterInShip.setComponent(1, aabb.maxY());  //should never be called
        }
        Vector3d localPos = faceCenterInShip.sub(localOrigin, new Vector3d());
        //return moveGeometricPosToWorldPos(shipPos, moveTo);
        return moveLocalPosToWorldPos(localPos, moveTo);
    }
    public ShipBuilder moveFaceTo(Direction dir, Vec3 moveTo) {
        if (canceled) return this;
        return moveFaceTo(dir, JomlUtil.d(moveTo));
    }

    public ShipBuilder setWorldVelocity(Vector3dc velocity) {
        if (canceled) return this;

        ServerShipEvent.delayedShipEvents.add(() -> {
            VSGameUtilsKt.getShipObjectWorld(level).teleportShip(
                ship,
                new ShipTeleportDataImpl(
                    ship.getTransform().getPositionInWorld(),
                    ship.getTransform().getShipToWorldRotation(),
                    velocity,
                    ship.getOmega(),
                    VSGameUtilsKt.getDimensionId(level),
                    ship.getTransform().getShipToWorldScaling().x()
                )
            );
        });
        /*VSGameUtilsKt.getShipObjectWorld(level).teleportShip(
            ship,
            new ShipTeleportDataImpl(
                ship.getTransform().getPositionInWorld(),
                ship.getTransform().getShipToWorldRotation(),
                velocity,
                ship.getOmega(),
                VSGameUtilsKt.getDimensionId(level),
                ship.getTransform().getShipToWorldScaling().x()
            )
        );*/
        EzDebug.log("set velocity:" + velocity.get(new Vector3d()).toString(format()));

        return this;
    }
    public ShipBuilder setWorldVelocity(double vx, double vy, double vz) {
        if (canceled) return this;
        return setWorldVelocity(new Vector3d(vx, vy, vz));
    }
    public ShipBuilder setLocalVelocity(Vector3dc velocity) {
        if (canceled) return this;

        Vector3d worldVel = ship.getTransform().getShipToWorldRotation().transform(velocity, new Vector3d());
        return setWorldVelocity(worldVel);
    }
    public ShipBuilder setLocalVelocity(double vx, double vy, double vz) {
        if (canceled) return this;
        return setLocalVelocity(new Vector3d(vx, vy, vz));
    }

    public <T> ShipBuilder attach(Class<T> cls, T val) {
        if (canceled) return this;
        ship.saveAttachment(cls, val);
        return this;
    }

    public ShipBuilder withShipDo(Consumer<ServerShip> consumer) {
        if (canceled) return this;
        if (consumer != null) {
            consumer.accept(ship);
        }
        return this;
    }

    public ShipBuilder doIf(Predicate<ShipBuilder> predicate, Consumer<ShipBuilder> action) {
        if (predicate == null || action == null) return this;
        if (predicate.test(this))
            action.accept(this);
        return this;
    }
    public ShipBuilder doIfElse(Predicate<ShipBuilder> predicate, Consumer<ShipBuilder> action, Consumer<ShipBuilder> elseAction) {
        if (predicate == null) return this;

        if (predicate.test(this)) {
            if (action != null) action.accept(this);
        } else {
            if (elseAction != null) elseAction.accept(this);
        }

        return this;
    }

    public ShipBuilder cancelIf(Predicate<ServerShip> predicate) {
        if (predicate == null || canceled) return this;  //ship is null when it is canceled
        if (predicate.test(ship)) {
            this.cancel();
        }
        return this;
    }

    public ShipBuilder cancel() {
        VSGameUtilsKt.getShipObjectWorld(level).deleteShip(ship);
        ship = null;
        canceled = true;
        return this;
    }

    /*
    public ShipBuilder Local2World(@Nullable ServerShip parentShip) {  //do nothing when parentShip is null: already in world space
        if (canceled || parentShip == null) return this;
        ShipTransform st = ship.getTransform();
        Matrix4dc loc2WorldM = parentShip.getTransform().getShipToWorld();
        this.setWorldPos(loc2WorldM.transformPosition(st.getPositionInWorld(), new Vector3d()));
        this.rotateForwardTo(st.);
    }*/
    public ServerShip get() { return ship; }
    public long getId() { return ship.getId(); }


    public @Nullable Vector3d getFaceCenterInWorld(Direction face) {
        AABBic shipAABB = ship.getShipAABB();
        if (shipAABB == null)
            return null;

        return ship.getShipToWorld().transformPosition(JomlUtil.dFaceCenter(shipAABB, face));
    }
    public @Nullable Vector3d getFaceCenterInShip(Direction face) {
        AABBic shipAABB = ship.getShipAABB();
        if (shipAABB == null)
            return null;

        return JomlUtil.dFaceCenter(shipAABB, face);
    }



    private static NumberFormat format() {
        NumberFormat df;
        df = NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.setGroupingUsed(false);
        return df;
    }


}
