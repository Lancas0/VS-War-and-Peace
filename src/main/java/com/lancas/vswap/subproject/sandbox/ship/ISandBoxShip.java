package com.lancas.vswap.subproject.sandbox.ship;


import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vswap.subproject.sandbox.component.behviour.IRigidbodyBehaviour;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public interface ISandBoxShip {
    public UUID getUuid();
    public @Nullable ISandBoxWorld<?> getWorld();

    //public int getRemainLifeTick();
    //public void setRemainLifeTick(int tick);

    public IRigidbodyBehaviour getRigidbody();
    public SandBoxShipBlockCluster getBlockCluster();

    public Stream<IComponentBehaviour<?>> allAddedBehaviours();

    public void onMarkDeleted();

    public void physTick(double dt);
}
