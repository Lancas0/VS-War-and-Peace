package com.lancas.vswap.subproject.sandbox.ship;

import com.lancas.vswap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PonderShipInitialState {
    //private volatile TransformPrimitive latestNetworkTransform = null;
    protected final TransformPrimitive prevTransform = new TransformPrimitive();
    protected final TransformPrimitive curTransform = new TransformPrimitive();
    protected final TransformPrimitive renderTransform = new TransformPrimitive();

    protected final RigidbodyData rigidData = new RigidbodyData();
    protected final BlockClusterData blockData = new BlockClusterData();

    protected final Queue<IClientBehaviour<?>> behaviours = new ConcurrentLinkedQueue<>();

    public PonderShipInitialState(SandBoxPonderShip ship) {
        prevTransform.set(ship.prevTransform);
        curTransform.set(ship.curTransform);
        renderTransform.set(ship.renderTransform);

        ship.rigidbody.getDataReader().getCopiedData(rigidData);
        ship.blockCluster.getDataReader().getCopiedData(blockData);

        behaviours.clear();  //todo save behviour
        //ship.behaviours.forEach(x -> x);
    }

    public void setState(SandBoxPonderShip ship) {
        ship.prevTransform.set(prevTransform);
        ship.curTransform.set(curTransform);
        ship.renderTransform.set(renderTransform);

        ship.rigidbody.loadData(ship, rigidData);
        ship.blockCluster.loadData(ship, blockData);
    }
}
