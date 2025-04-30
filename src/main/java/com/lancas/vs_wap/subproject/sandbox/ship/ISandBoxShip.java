package com.lancas.vs_wap.subproject.sandbox.ship;


import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxRigidbody;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;

import java.util.Queue;
import java.util.UUID;
import java.util.stream.Stream;

public interface ISandBoxShip {
    public UUID getUuid();

    public int getRemainLifeTick();
    public void setRemainLifeTick(int tick);

    public SandBoxRigidbody getRigidbody();
    public SandBoxShipBlockCluster getBlockCluster();

    public Stream<IComponentBehaviour> allAddedBehaviours();
}
