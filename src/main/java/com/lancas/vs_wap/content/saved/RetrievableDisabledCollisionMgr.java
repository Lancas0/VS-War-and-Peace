package com.lancas.vs_wap.content.saved;

import com.lancas.vs_wap.foundation.api.NoOrderTuple;
import com.lancas.vs_wap.util.WorldUtil;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class RetrievableDisabledCollisionMgr {
    //don't save because collision disable info don't save by vs

    private static final HashSet<NoOrderTuple<Long>> disabledCollisions = new HashSet<>();

    public static boolean disableCollisionBetween(ServerLevel level, @NotNull Long a, @NotNull Long b) {
        if (a.equals(b)) return false;

        var shipWorld = WorldUtil.shipWorldOf(level);
        if (!shipWorld.disableCollisionBetweenBodies(a, b))
            return false;

        disabledCollisions.add(new NoOrderTuple<>(a, b));
        return true;
    }
    public static boolean enableCollisionBetween(ServerLevel level, @NotNull Long a, @NotNull Long b) {
        if (a.equals(b)) return false;

        var shipWorld = WorldUtil.shipWorldOf(level);
        if (!shipWorld.enableCollisionBetweenBodies(a, b))
            return false;

        disabledCollisions.remove(new NoOrderTuple<>(a, b));
        return true;
    }
    public static int retrieveAllCollisionsOf(ServerLevel level, Long a) {
        if (a == null) return 0;

        var shipWorld = WorldUtil.shipWorldOf(level);
        int retrieveCnt = 0;
        var collisionsIt = disabledCollisions.iterator();
        while (collisionsIt.hasNext()) {
            var tuple = collisionsIt.next();

            if (!tuple.has(a)) continue;

            if (shipWorld.enableCollisionBetweenBodies(tuple.a(), tuple.b())) {
                retrieveCnt++;
            }
            collisionsIt.remove();
        }
        return retrieveCnt;
    }
    public static boolean hasDisabledCollision(ServerLevel level, Long a) {
        return disabledCollisions.stream().anyMatch(tuple -> tuple.has(a));
    }
}
