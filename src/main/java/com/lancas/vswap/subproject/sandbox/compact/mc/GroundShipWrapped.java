package com.lancas.vswap.subproject.sandbox.compact.mc;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.api.ISavedObject;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vswap.subproject.sandbox.component.behviour.IRigidbodyBehaviour;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import com.lancas.vswap.subproject.sandbox.ship.IClientSandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.IServerSandBoxShip;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public class GroundShipWrapped implements IServerSandBoxShip, IClientSandBoxShip, ISavedObject<GroundShipWrapped> {
    private UUID uuid;
    private final GroundRigidbodyWrapped rigidbody = new GroundRigidbodyWrapped();

    private GroundShipWrapped() {}
    public GroundShipWrapped(CompoundTag tag) { load(tag); }
    public GroundShipWrapped(UUID inUuid) { uuid = inUuid; }

    @Override
    public void clientTick(ClientLevel level) { }
    @Override
    public void serverTick(ServerLevel level) { }
    @Override
    public void physTick() { }

    @Override
    public UUID getUuid() { return uuid; }

    @Override
    public @Nullable ISandBoxWorld<?> getWorld() {
        throw new NotImplementedException();
    }

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
    public void onMarkDeleted() {
        EzDebug.warn("ground ship should never be deleted!");
    }


    @Override
    public CompoundTag saved() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", uuid);
        return tag;
    }
    @Override
    public GroundShipWrapped load(CompoundTag tag) {
        uuid = tag.getUUID("uuid");
        return this;
    }
}
