package com.lancas.vs_wap.ship.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;

public interface IShipSchemeData {
    //todo rotation
    /*default public ServerShip createShip(ServerLevel level, Vector3d shipPos, Quaternionfc rotation) {
        EzDebug.log("shipPos is " + shipPos);
        if (level == null) return null;

        AtomicBoolean anySolidBlock = new AtomicBoolean(false);


        //todo use (0, 0, 0) as start pos, may be there will be a baserock
        ServerShip newShip =
                VSGameUtilsKt.getShipObjectWorld(level)
                .createNewShipAtBlock(PosUtil.toV3I(shipPos), false, 1f, VSGameUtilsKt.getDimensionId(level));

        //todo experimentally initalize ship transform
        /.*newShip.unsafeSetTransform(new ShipTransformImpl(
                shipPos,
                newShip.getTransform().getPositionInShip(),
                new Quaterniond(rotation.x(), rotation.y(), rotation.z(), rotation.w()),  //todo: add a parameter so that can set rotate
                getScale()
        ));*./
        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(newShip, new ShipTeleportDataImpl(
                shipPos,
                newShip.getTransform().getShipToWorldRotation(),
                new Vector3d(),
                new Vector3d(),
                VSGameUtilsKt.getDimensionId(level),
                getScale().x() //todo 3d scale
        ));

        Vector3d shipyardCenterPos = newShip.getWorldToShip().transformPosition(new Vector3d(shipPos));


        forEach(level, (BlockPos offset, BlockState state, CompoundTag entityTag) -> {
            if (state.isAir()) return;
            anySolidBlock.set(true);

            if (VSGameUtilsKt.getShipManagingPos(level, offset) != null) {
                EzDebug.log("数据内位置在shipyard内");
                return;
            }

            //Place the current block
            BlockPos currentPos = PosUtil.toBlockPos(shipyardCenterPos).offset(offset);
            WorldUtil.setBlock(level, currentPos, state, entityTag);
            WorldUtil.updateBlock(level, currentPos);
        });

        //the ship is empty
        if (!anySolidBlock.get()) {
            VSGameUtilsKt.getShipObjectWorld(level).deleteShip(newShip);
            EzDebug.log("delete the ship because of there is no solid block");
            return null;
        }

        return newShip;
    }*/
    IShipSchemeData readShip(ServerLevel level, ServerShip ship);
    public IShipSchemeData clear();

    public IShipSchemeData setScale(Vector3dc scale);

    //public IShipSchemeData addBlock(BlockPos offset, BlockState state);
    //public IShipSchemeData addBlockEntity(BlockPos offset, BlockState state, CompoundTag entityTag);
    //public int getBlockCnt();

    public Vector3dc getScale();

    //public void forEach(Level level, TriConsumer<BlockPos, BlockState, CompoundTag> func);

    public ServerShip createShip(ServerLevel level);
    public ServerShip overwriteEmptyShip(ServerLevel level, ServerShip ship);

    public boolean isEmpty();

    public CompoundTag saved();
    public IShipSchemeData load(CompoundTag tag);

}
