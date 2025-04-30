package com.lancas.vs_wap.subproject.sandbox.api.component;

import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3ic;

public interface IComponentBehaviour<D extends IComponentData<D>> {
    public CompoundTag getSavedData();
    public void loadData(ISandBoxShip ship, D src);
    public default void loadDataUnsafe(ISandBoxShip ship, Object src) {
        loadData(ship, (D)src);
    }

    public Class<D> getDataType();

    //todo spread the method around different list like vs?
    //public default void serverTick(ServerLevel level) {}
    public default void physTick() {}

    public default void onBlockReplaced(Vector3ic localPos, BlockState oldState, BlockState newState) {}
}
