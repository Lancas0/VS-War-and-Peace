package com.lancas.vs_wap.subproject.sandbox.compact.vs;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.IRigidbodyBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

//todo use lazy update
public class VsRigidbodyWrapped implements IRigidbodyBehaviour {
    private WrappedVsShip ship = null;
    private final VsRigidbodyDataWrapper data = new VsRigidbodyDataWrapper();

    public VsRigidbodyWrapped(WrappedVsShip inShip) { ship = inShip; data.setShipCache(ship.vsShipCache); }
    public VsRigidbodyWrapped(WrappedVsShip inShip, CompoundTag tag) { loadSavedData(inShip, tag); data.setShipCache(ship.vsShipCache); }

    @Override
    public synchronized void clientTick(ClientLevel level) { }  //
    @Override
    public synchronized void serverTick(ServerLevel level) {
        if (ship == null || ship.vsShipCache == null) return;
        data.setShipCache(ship.vsShipCache);

        ServerShip vsShip = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(ship.getVsId());
        if (vsShip == null) {
            EzDebug.warn("can't find the wrapped vs ship");
            return;
        }

        //data.updateSnapshots(vsShip);
        if (data.getMass() < 1E-6 || data.isStatic()) {
            //EzDebug.log("applying force is cleared due to zeroMass or static, mass:" + data.mass + ", static?:" + data.isStatic);
            data.updates.clear();
            return;
        }

        var updateIt = data.updates.iterator();
        while (updateIt.hasNext()) {
            var update = updateIt.next();
            if (update == null) {
                updateIt.remove();
                continue;
            }

            try {
                update.update(level, vsShip);
            } catch (Exception e) {
                EzDebug.warn("fail to update the rigidbody data");
                e.printStackTrace();
            }
            updateIt.remove();
        }
    }

    //todo don't save it for now
    @Override
    public CompoundTag getSavedData() {
        return new NbtBuilder()
            .putCompound("data", data.saved())
            .get();
    }
    @Override
    public void loadSavedData(ISandBoxShip inShip, CompoundTag saved) {
        ship = (WrappedVsShip)inShip;
        NbtBuilder.modify(saved)
            .readCompoundDo("data", data::load);

        data.setShipCache(ship.vsShipCache);
    }
    @Override
    public void loadData(ISandBoxShip inShip, RigidbodyData dataSrc) {
        ship = (WrappedVsShip)inShip;
        data.copyData(dataSrc);

        data.setShipCache(ship.vsShipCache);
    }

    @Override
    public Class<VsRigidbodyDataWrapper> getDataType() { return VsRigidbodyDataWrapper.class; }

    @Override
    public IRigidbodyDataReader getDataReader() { return data; }
    @Override
    public IRigidbodyDataWriter getDataWriter() { return data; }
}
