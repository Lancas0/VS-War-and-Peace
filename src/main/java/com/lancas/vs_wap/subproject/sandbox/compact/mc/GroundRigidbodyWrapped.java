package com.lancas.vs_wap.subproject.sandbox.compact.mc;

import com.lancas.vs_wap.subproject.sandbox.component.behviour.IRigidbodyBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class GroundRigidbodyWrapped implements IRigidbodyBehaviour {
    private final GroundRigidbodyDataWrapper data = new GroundRigidbodyDataWrapper();

    @Override
    public IRigidbodyDataReader getDataReader() { return data; }
    @Override
    public IRigidbodyDataWriter getDataWriter() { return data; }

    @Override
    public void clientTick(ClientLevel level) {  }
    @Override
    public void serverTick(ServerLevel level) {  }

    @Override
    public CompoundTag getSavedData() {
        return new CompoundTag();
    }
    @Override
    public void loadSavedData(ISandBoxShip ship, CompoundTag saved) { }
    @Override
    public void loadData(ISandBoxShip inShip, RigidbodyData dataSrc) { }

    @Override
    public Class<?> getDataType() { return GroundRigidbodyWrapped.class; }
}
