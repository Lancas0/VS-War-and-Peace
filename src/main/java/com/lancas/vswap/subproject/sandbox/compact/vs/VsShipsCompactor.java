package com.lancas.vswap.subproject.sandbox.compact.vs;

import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.subproject.sandbox.api.ISavedObject;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class VsShipsCompactor implements ISavedObject<VsShipsCompactor> {
    private final Map<UUID, WrappedVsShip> wrappedVsShips = new ConcurrentHashMap<>();
    private final Map<Long, UUID> vsIdToUuid = new ConcurrentHashMap<>();

    public WrappedVsShip getWrappedVsShip(UUID uuid) {
        return wrappedVsShips.get(uuid);
    }
    public WrappedVsShip getWrappedVsShip(Long vsId) {
        UUID uuid = vsIdToUuid.get(vsId);
        if (uuid == null)
            return null;

        return wrappedVsShips.get(uuid);
    }
    public WrappedVsShip wrapOrGet(Ship vsShip) {
        long vsId = vsShip.getId();
        WrappedVsShip wrappedVsShip = getWrappedVsShip(vsId);
        if (wrappedVsShip != null)
            return wrappedVsShip;

        UUID uuid = UUID.randomUUID();
        wrappedVsShip = new WrappedVsShip(uuid, vsShip);

        vsIdToUuid.put(vsId, uuid);
        wrappedVsShips.put(uuid, wrappedVsShip);

        return wrappedVsShip;
    }
    public WrappedVsShip remove(long vsId) {
        UUID uuid = vsIdToUuid.remove(vsId);
        if (uuid == null) return null;

        return wrappedVsShips.remove(uuid);
    }
    public WrappedVsShip remove(UUID uuid) {
        var wrappedVs = wrappedVsShips.get(uuid);

        if (wrappedVs != null)
            vsIdToUuid.remove(wrappedVs.getVsId());

        return wrappedVs;
    }
    public void clear() {
        wrappedVsShips.clear();
        vsIdToUuid.clear();
    }

    public Stream<WrappedVsShip> allWrapped() { return wrappedVsShips.values().stream(); }
    public Stream<ISandBoxShip> allWrappedAsSandBox() { return allWrapped().map(s -> s); }


    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putEach("wrapped_vs_ship", wrappedVsShips.values(), WrappedVsShip::saved)
            .putMap("vs_id_to_uuid", vsIdToUuid, (k, v) ->
                new NbtBuilder().putLong("vs_id", k).putUUID("uuid", v).get()
            ).get();
    }
    @Override
    public VsShipsCompactor load(CompoundTag tag) {
        wrappedVsShips.clear();
        vsIdToUuid.clear();

        NbtBuilder.modify(tag)
            .readEachCompoundDo("wrapped_vs_ship", t -> {
                var wrapped = new WrappedVsShip(t);
                wrappedVsShips.put(wrapped.getUuid(), wrapped);
            })
            .readMapOverwrite("vs_id_to_uuid",
                t -> new BiTuple<>(t.getLong("vs_id"), t.getUUID("uuid")),
            vsIdToUuid);

        return this;
    }
}
