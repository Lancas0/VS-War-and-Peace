package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxTransform;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;

import java.util.UUID;

public interface ISandBoxShip {
    public UUID getUuid();

    public AABBdc getWorldAABB();
    public AABBic getLocalAABB();

    public SandBoxTransform getTransform();
    public SandBoxShipBlockCluster getCluster();
}
