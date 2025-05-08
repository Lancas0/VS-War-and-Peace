package com.lancas.vs_wap.subproject.sandbox.ship;


import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.IRigidbodyBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;

import java.util.UUID;
import java.util.stream.Stream;

public interface ISandBoxShip {
    public UUID getUuid();

    //public int getRemainLifeTick();
    //public void setRemainLifeTick(int tick);

    public IRigidbodyBehaviour getRigidbody();
    public SandBoxShipBlockCluster getBlockCluster();

    public Stream<IComponentBehaviour<?>> allAddedBehaviours();
}
