package com.lancas.vswap.subproject.sandbox.api.component;

import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3ic;

public interface IComponentBehaviour<D extends IComponentData<D>> {
    public CompoundTag getSavedData();
    public void loadSavedData(ISandBoxShip ship, CompoundTag saved);
    public void loadData(ISandBoxShip inShip, D dataSrc);
    /*public default void loadDataUnsafe(ISandBoxShip ship, Object src) {
        loadData(ship, (D)src);
    }*/

    public Class<?> getDataType();

    //todo spread the method around different list like vs?
    //public default void serverTick(ServerLevel level) {}
    public default void physTick() {}

    public default void onBlockReplaced(Vector3ic localPos, BlockState oldState, BlockState newState) {}

    public default void onMarkDeleted() {}
}
