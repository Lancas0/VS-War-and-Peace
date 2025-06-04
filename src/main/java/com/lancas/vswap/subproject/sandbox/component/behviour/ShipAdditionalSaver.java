package com.lancas.vswap.subproject.sandbox.component.behviour;

import com.lancas.vswap.subproject.sandbox.api.component.IDataExposedBehaviour;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.ServerOnlyBehaviour;
import com.lancas.vswap.subproject.sandbox.component.data.ShipAdditionalSavedData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ShipAdditionalSaver extends ServerOnlyBehaviour<ShipAdditionalSavedData> implements IDataExposedBehaviour<ShipAdditionalSavedData> {
    public static @NotNull ShipAdditionalSaver getOrCreate(SandBoxServerShip ship) {
        ShipAdditionalSaver saver = ship.getBehaviour(ShipAdditionalSaver.class);
        if (saver == null) {
            saver = new ShipAdditionalSaver();
            ship.addBehaviour(saver, new ShipAdditionalSavedData());
        }
        return saver;
    }

    @Override
    protected ShipAdditionalSavedData makeInitialData() {
        return new ShipAdditionalSavedData();
    }

    @Override
    public void serverTick(ServerLevel level) { }

    @Override
    public Class<?> getDataType() { return ShipAdditionalSavedData.class; }

    @Override
    public ShipAdditionalSavedData getData() { return data; }
    public ShipAdditionalSavedData makeChang(Consumer<ShipAdditionalSavedData> consumer) {
        consumer.accept(data);
        return data;
    }
}
