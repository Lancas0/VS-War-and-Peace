package com.lancas.vswap.subproject.sandbox.component.behviour;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.ServerOnlyBehaviour;
import com.lancas.vswap.subproject.sandbox.component.data.HierarchyData;
import com.lancas.vswap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vswap.subproject.sandbox.ship.IServerSandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SandBoxHierarchyBehaviour extends ServerOnlyBehaviour<HierarchyData> {

    @Override
    protected HierarchyData makeInitialData() { return new HierarchyData(); }

    public void addChild(SandBoxServerShip childShip, @Nullable IConstraint constraint) {
        SandBoxServerWorld world = ship.getWorld();

        //HierarchyData.ChildShipData prevChildData = data.childrenData.get(childShip.getUuid());
        if (data.childrenData.containsKey(childShip.getUuid())) {
            EzDebug.warn("already have same child, will remove prev constraint and try add new constaint");

            data.childrenData.get(childShip.getUuid())
                .ifPresent(cUuid -> {
                    if (world == null) {
                        EzDebug.warn("fail to remove prev constraint because get null World!");
                    } else {
                        world.getConstraintSolver().markConstraintRemoved(cUuid);
                    }
                });
        }

        data.childrenData.put(
            childShip.getUuid(),
            Optional.ofNullable(constraint).map(IConstraint::getUuid)
        );

        if (world == null) {
            EzDebug.warn("the ship world is null! fail to aftermath arrangement");
            return;
        }
        //aftermath arrangements, however it's better to handle before add addChild
        //todo after
        if (constraint != null)
            world.getConstraintSolver().putIfAbsent(constraint.getUuid(), constraint);
        if (!world.containsShip(childShip.getUuid()))
            world.addShipImpl(childShip, true);  //default sync to client
    }
    public void abandon(UUID childUuid) {
        SandBoxServerWorld world = ship.getWorld();

        if (!data.childrenData.containsKey(childUuid))
            return;

        data.childrenData.get(childUuid).ifPresent(cUuid -> {
            if (world == null) {
                EzDebug.error("ship world is null! fail to remove constraint");
            } else {
                world.getConstraintSolver().markConstraintRemoved(cUuid);
            }
        });
    }

    /*public @Nullable HierarchyData.ChildShipData getChildData(UUID childUuid) {
        return data.childrenData.stream()
            .filter(d -> d.childShipUuid().equals(childUuid))
            .findFirst()
            .orElse(null);
    }*/

    public @Nullable UUID getConstraintUuidOfChild(UUID childUuid) {
        var optionalConstraintUuid = data.childrenData.get(childUuid);
        if (optionalConstraintUuid == null)
            return null;

        return optionalConstraintUuid.orElse(null);
    }

    @Override
    public void serverTick(ServerLevel level) { }

    @Override
    public void onMarkDeleted() {
        SandBoxServerWorld world = ship.getWorld();
        if (world == null) {
            EzDebug.error("world is null! fail to remove all children");
            return;
        }

        data.childrenData.forEach((cShipUuid, cUuid) -> {
            cUuid.ifPresent(x -> world.getConstraintSolver().markConstraintRemoved(x));
            world.markShipDeleted(cShipUuid);  //naturally avoid recursive deleting
        });
    }

    @Override
    public Class<?> getDataType() {
        return HierarchyData.class;
    }
}
