package com.lancas.vswap.subproject.sandbox.constraint;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.EventMgr;
import com.lancas.vswap.foundation.AlwaysSafeRemoveMap;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.api.ISavedObject;
import com.lancas.vswap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SandBoxConstraintSolver implements ISavedObject<SandBoxConstraintSolver> {
    public static ObjectMapper constraintMapper = new ObjectMapper();
    static {
        constraintMapper.setVisibility(constraintMapper.getSerializationConfig().getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
    }

    public SandBoxConstraintSolver(ISandBoxWorld<?> inWorld) {
        world = inWorld;

        SandBoxEventMgr.onRemoveShip.addListener((fromWorld, ship) -> {
            if (world != fromWorld) return;
            constraints.markRemoveIf((k, c) -> c.involveShip(ship));
            EzDebug.highlight("remove constraint by involved sa ship");
        });
        EventMgr.Server.onVsShipUnloaded.addListener((unloadVsId) -> {
            constraints.markRemoveIf((k, c) -> c.involveVsShip(unloadVsId));
            EzDebug.highlight("remove constraint by involved vs ship");
        });
    }

    protected final ISandBoxWorld<?> world;
    public ISandBoxWorld<?> getWorld() { return world; }

    protected final AlwaysSafeRemoveMap<UUID, IConstraint> constraints = new AlwaysSafeRemoveMap<>();
    //private final ConcurrentHashMap<UUID, IConstraint> constraints = new ConcurrentHashMap<>();
    //private final Set<UUID> toRemoveUuid = ConcurrentHashMap.newKeySet();
    //private final Set<Predicate<IConstraint>> toRemove = ConcurrentHashMap.newKeySet();

    public void addConstraint(@NotNull IConstraint constraint) {
        /*if (constraint instanceof AbstractOnVsConstraint<?> vsConstraint) {
            EventMgr.Server.onVsShipUnloaded.addListener(new CancelableMonoListener<Long>() {
                boolean removed = false;
                @Override
                public void accept(Long unloadShipId) {
                    if (vsConstraint.getVsShipId() == unloadShipId) {
                        markConstraintRemoved(vsConstraint.getUuid());
                        removed = true;
                    }
                }
                @Override
                public boolean shouldCancel() { return removed; }
            });
        }*/
        constraints.put(constraint.getUuid(), constraint);
    }
    public boolean contains(UUID constraintUuid) {  return constraints.containsKey(constraintUuid); }
    public void putIfAbsent(UUID constraintUuid, IConstraint constraint) { constraints.putIfAbsent(constraintUuid, constraint); }
    public void markConstraintRemoved(UUID constraintUuid) {
        constraints.markKeyRemoved(constraintUuid);
    }
    public <T extends IConstraint> T getConstraint(UUID uuid) {
        if (uuid == null)
            return null;

        try {
            return (T)constraints.get(uuid);
        } catch (Exception e) {
            EzDebug.warn("fail to get a existed constraint with uuid:" + uuid + ", exp:" + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public void solve() {
        constraints.values().parallel().forEach(c -> c.project(world, this));
    }
    public void tick() {
        constraints.values().forEach(c -> c.tick(world.getWorld(), this));
    }

    /*private void flushRemove() {
        var toRemoveIt = toRemove.iterator();
        while (toRemoveIt.hasNext()) {
            var removePredicate = toRemoveIt.next();
            toRemoveIt.remove();
            constraints.remove(removeUuid);
        }
    }*/
    /*public Stream<IConstraint> flushedConstraints() {
        var toRemoveIt = toRemove.iterator();
        while (toRemove.)
        if (toRemove.isEmpty())
            return constraints.values().stream();

        constraints.values().stream().iterator().remove();
    }*/


    @Override
    public CompoundTag saved() {
        //flushRemove();
        return new NbtBuilder().putJacksonMap(
            "constraints", constraints.flushedMap(), constraintMapper, constraintMapper
        ).get();
    }
    @Override
    public SandBoxConstraintSolver load(CompoundTag tag) {
        Map<UUID, IConstraint> deserializeStorage = new HashMap<>();
        NbtBuilder.modify(tag).readJacksonMap("constraints",
            UUID.class, IConstraint.class,
            constraintMapper, constraintMapper, deserializeStorage
        );

        constraints.clear();
        constraints.putAll(deserializeStorage);

        return this;
    }
}
