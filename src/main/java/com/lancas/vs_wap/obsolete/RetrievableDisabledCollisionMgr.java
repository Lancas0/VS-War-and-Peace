package com.lancas.vs_wap.obsolete;

/*
import com.lancas.einherjar.ModMain;
import com.lancas.einherjar.util.WorldUtil;
import com.lancas.einherjar.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Hashtable;

public class RetrievableDisabledCollisionMgr extends SavedData {
    public static RetrievableDisabledCollisionMgr getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            RetrievableDisabledCollisionMgr::load,
            RetrievableDisabledCollisionMgr::new,
            ModMain.MODID + "_retrievable_disabled_collisions"
        );
    };

    private Hashtable<Long, Long> disabledCollisions;


    public void disableCollisionBetween(ServerLevel level, long a, long b) {
        var shipWorldAccessor = WorldUtil.shipWorldAccessorOf(level);

    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return new NbtBuilder().get();
    }
    public static RetrievableDisabledCollisionMgr load(CompoundTag tag) {
        RetrievableDisabledCollisionMgr disCollisions = new RetrievableDisabledCollisionMgr();
        return disCollisions;
    }


}*/