package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.abs.BothSideBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.data.ExpireTickerData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;

public class SandBoxExpireTicker extends BothSideBehaviour<ExpireTickerData> {
    @Override
    protected ExpireTickerData makeInitialData() { return new ExpireTickerData(0); }

    @Override
    public void clientTick(ClientLevel level) {
        if (--data.tick < 0)
            SandBoxClientWorld.INSTANCE.markShipDeleted(ship.getUuid());
    }
    @Override
    public void serverTick(ServerLevel level) {
        if (--data.tick < 0)
            SandBoxServerWorld.markShipDeleted(level, ship.getUuid());
    }

    @Override
    public Class<?> getDataType() { return ExpireTickerData.class; }
}
