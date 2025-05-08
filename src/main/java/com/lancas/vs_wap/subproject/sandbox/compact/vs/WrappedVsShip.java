package com.lancas.vs_wap.subproject.sandbox.compact.vs;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.api.ISavedObject;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.IRigidbodyBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import com.lancas.vs_wap.subproject.sandbox.ship.IClientSandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.IServerSandBoxShip;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.UUID;
import java.util.stream.Stream;

//todo remove the wrapped ship when unload
public class WrappedVsShip implements IServerSandBoxShip, IClientSandBoxShip, ISavedObject<WrappedVsShip> {
    public long vsId;
    public UUID uuid;

    public Ship vsShipCache = null;

    private final VsRigidbodyWrapped rigidbody;

    public WrappedVsShip(UUID inUuid, Ship vsShip) {
        vsId = vsShip.getId();
        uuid = inUuid;
        rigidbody = new VsRigidbodyWrapped(this);
    }
    public WrappedVsShip(CompoundTag tag) {
        rigidbody = new VsRigidbodyWrapped(this);
        load(tag);
    }

    public long getVsId() { return vsId; }
    @Override
    public UUID getUuid() { return uuid; }

    /*@Override
    public int getRemainLifeTick() {
        return -1;  //vs don't support life time down
    }

    @Override
    public void setRemainLifeTick(int tick) {
        //vs don't support life time down
    }*/

    @Override
    public IRigidbodyBehaviour getRigidbody() { return rigidbody; }

    @Override
    public SandBoxShipBlockCluster getBlockCluster() {
        EzDebug.warn("not impl vs wrapped block cluster");
        return null;
    }

    @Override
    public Stream<IComponentBehaviour<?>> allAddedBehaviours() {
        return Stream.empty();
    }



    @Override
    public void clientTick(ClientLevel level) {
        vsShipCache = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(vsId);
        if (vsShipCache == null) {
            SandBoxClientWorld.INSTANCE.markShipDeleted(uuid);
            return;
        }
    }
    @Override
    public void serverTick(ServerLevel level) {
        vsShipCache = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(vsId);
        if (vsShipCache == null) {
            SandBoxServerWorld.getOrCreate(level).markShipDeleted(uuid);
            return;
        }
        rigidbody.serverTick(level);
    }
    @Override
    public void physTick() { }


    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putNumber("vs_id", vsId)
            .putUUID("uuid", uuid)
            .putCompound("rigidbody_data", rigidbody.getSavedData())
            .get();
    }
    @Override
    public WrappedVsShip load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readLongDo("vs_id", v -> vsId = v)
            .readUUIDDo("uuid", v -> uuid = v)
            .readCompoundDo("rigidbody_data", t -> rigidbody.loadSavedData(this, t));
        return this;
    }
}
