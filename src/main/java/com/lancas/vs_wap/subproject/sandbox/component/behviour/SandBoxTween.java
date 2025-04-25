package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.TweenData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.time.StopWatch;

public class SandBoxTween extends AbstractComponentBehaviour<TweenData> {
    /*public static SandBoxTween of(TweenData.TweenFunction inFunction) {
        SandBoxTween tween = new SandBoxTween();
        tween.data.function = inFunction;
        return tween;
    }*/

    @Override
    protected TweenData makeData() { return new TweenData(null); }
    @Override
    public IExposedComponentData<TweenData> getExposedData() { return data; }

    //todo remove the behaviour if exceeds
    @Override
    public void serverTick(ServerLevel level) {
        if (data.function == null) return;
        SandBoxTransformData newTransformData = data.function.getNextTransform(ship.getTransform().getExposedData(), data.elapsedTime);
        if (newTransformData == null) return;

        ship.getTransform().set(newTransformData);  //todo sync?
        float dt = 0.05f;  //todo dt managed by server world
        data.elapsedTime += dt;
    }
}
