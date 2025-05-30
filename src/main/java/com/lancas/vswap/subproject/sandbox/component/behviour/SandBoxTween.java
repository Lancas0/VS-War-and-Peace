package com.lancas.vswap.subproject.sandbox.component.behviour;

import com.lancas.vswap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vswap.subproject.sandbox.api.component.IServerBehaviour;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.BothSideBehaviour;
import com.lancas.vswap.subproject.sandbox.component.data.TweenData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;

public class SandBoxTween extends BothSideBehaviour<TweenData>
    implements IServerBehaviour<TweenData>, IClientBehaviour<TweenData> {
    /*public static SandBoxTween of(TweenData.TweenFunction inFunction) {
        SandBoxTween tween = new SandBoxTween();
        tween.data.function = inFunction;
        return tween;
    }*/

    @Override
    protected TweenData makeInitialData() { return new TweenData(null, 0, false); }
    //@Override
    //public IComponentDataReader<TweenData> getExposedData() { return data; }

    //todo remove the behaviour if exceeds
    @Override
    public synchronized void serverTick(ServerLevel level) {
        double dt = 0.05;  //todo dt managed by server world, as a param in serverTick
        tick(dt);
    }
    @Override
    public synchronized void clientTick(ClientLevel level) {
        double dt = 0.01667;  //todo dt managed by client world, as a param in clientTick
        tick(dt);
    }

    private void tick(double dt) {
        if (data.function == null || data.curve == null) return;
        if (data.elapsedTime > data.duration && !data.loop) return;  //todo remove the behaviour by timeout

        double t01InThisLoop = (data.elapsedTime / data.duration);  //todo ping pong loop
        t01InThisLoop -= (int)t01InThisLoop;

        double curveT01 = data.curve.evaluate(t01InThisLoop);
        TransformPrimitive newTransformData = data.function.getNextTransform(
            ship.getRigidbody().getDataReader().getTransform(),
            curveT01
        );

        if (newTransformData != null) {
            ship.getRigidbody().getDataWriter().setTransform(newTransformData);
        }

        data.elapsedTime += dt;
    }

    @Override
    public Class<TweenData> getDataType() { return TweenData.class; }
}
