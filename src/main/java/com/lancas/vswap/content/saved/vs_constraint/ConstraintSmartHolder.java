package com.lancas.vswap.content.saved.vs_constraint;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.EventMgr;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConstraintSmartHolder implements INBTSerializable<CompoundTag> {
    /*public static class WaitForLevel {
        protected ConstraintSmartHolder holder = null;
        protected Function<ServerLevel, ConstraintSmartHolder> holderSupplier;

        public WaitForLevel(ConstraintTarget t0, ConstraintTarget t1) {
            holderSupplier = l -> new ConstraintSmartHolder(t0, t1, l);
        }
        public WaitForLevel(CompoundTag saved) {
            holderSupplier = l -> new ConstraintSmartHolder(l, saved);
        }

        public ConstraintSmartHolder get(@NotNull ServerLevel level) {
            if (holder != null)
                return holder;
            return (holder = holderSupplier.apply(level));
        }
        public @Nullable CompoundTag getSaved() {
            if (holder != null)
                return holder.serializeNBT();
            return null;
        }
    }*/

    protected ConstraintTarget target0;  //is actually final
    protected ConstraintTarget target1;
    //protected final ServerLevel level;
    protected final HashSet<String> constraintKeys = new HashSet<>();

    protected volatile boolean removed = false;
    protected final Object mutex = new Object();

    public ConstraintSmartHolder(ConstraintTarget t0, ConstraintTarget t1, ServerLevel level) {
        target0 = t0;
        target1 = t1;
        //level = inLevel;

        if (!target0.exist(level) || !target1.exist(level)) {
            removed = true;
            EzDebug.warn("create a remove constraint holder, ship0 exist:" + target0.exist(level) + ", ship1 exist:" + target1.exist(level));
            return;
        }

        addListener();
    }
    public ConstraintSmartHolder(CompoundTag saved) {
        //level = inLevel;
        deserializeNBT(saved);

        if (!removed)
            addListener();
    }
    protected void addListener() {
        EventMgr.Server.onVsShipUnloaded.addListener(id -> {
            if (target0.isSameShip(id) || target1.isSameShip(id)) {
                EzDebug.highlight("ConstraintSmartHolder remove constraint because unload vsShip");
                setRemoved();
            }
        });
    }

    public ConstraintSmartHolder addConstraint(ServerLevel level, BiFunction<Long, Long, VSConstraint> constraintGetter) {
        return addConstraint(level, UUID.randomUUID().toString(), constraintGetter);  //default use uuid to generate random uuid
    }
    //expected unique key
    public ConstraintSmartHolder addConstraint(ServerLevel level, String key, BiFunction<Long, Long, VSConstraint> constraintGetter) {
        if (removed)  //第一次检测，减少mutex占用
            return this;

        synchronized (mutex) {
            if (removed)  //第二次检测，确保removed不为true
                return this;  //ship is removed, no use to add constraint

            if (constraintKeys.contains(key)) {
                EzDebug.warn("add key " + key + " is existed, will remove the former constraint");
                ConstraintsMgr.removeInLevelOrAddingConstraint(level, key);
            }
            constraintKeys.add(key);
            VSConstraint constraint = constraintGetter.apply(target0.getId(level), target1.getId(level));
            ConstraintsMgr.addConstraint(level, key, constraint);
            EzDebug.highlight("add constraint " + key);
        }
        return this;
    }

    public void setRemoved() {
        synchronized (mutex) {
            removed = true;
            constraintKeys.forEach(ConstraintsMgr::removeAnyWithKey);
            constraintKeys.clear();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        return new NbtBuilder()
            .putCompound("target0", target0.serializeNBT())
            .putCompound("target1", target1.serializeNBT())
            .putEach("keys", constraintKeys, NbtBuilder::tagOfString)
            .get();
    }
    @Override
    public void deserializeNBT(CompoundTag tag) {
       NbtBuilder.modify(tag)
            .readCompoundDo("target0", t -> target0 = new ConstraintTarget(t))
            .readCompoundDo("target1", t -> target1 = new ConstraintTarget(t))
            .readEachCompoundOverwrite("keys", NbtBuilder::stringOf, constraintKeys)
            .get();
    }
}
