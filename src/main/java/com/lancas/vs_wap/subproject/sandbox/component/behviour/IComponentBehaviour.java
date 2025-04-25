package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.subproject.sandbox.component.data.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3ic;

public interface IComponentBehaviour<D extends IComponentData<D> & IExposedComponentData<D>> {
    public CompoundTag getSavedData();
    public void loadData(SandBoxServerShip ship, D src);

    public default void loadDataUnsafe(SandBoxServerShip ship, Object src) {
        loadData(ship, (D)src);
    }

    public IExposedComponentData<D> getExposedData();

    //todo spread the method around different list like vs?
    public default void serverTick(ServerLevel level) {}
    public default void physTick() {}

    public default void onBlockReplaced(Vector3ic localPos, BlockState oldState, BlockState newState) {}
}
