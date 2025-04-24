package com.lancas.vs_wap.event;

import com.lancas.vs_wap.event.api.IQuadEvent;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.NotImplementedException;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;

import java.util.ArrayList;
import java.util.List;

public class ConstraintAddEvent implements IQuadEvent<ServerLevel, String, VSConstraint, Integer> {

    private List<QuadConsumer<ServerLevel, String, VSConstraint, Integer>> listeners = new ArrayList<>();

    public void invokeAll(ServerLevel level, String key, VSConstraint constraint, Integer id) {
        for (var listener : listeners)
            listener.apply(level, key, constraint, id);
    }

    @Override
    public void addListener(QuadConsumer<ServerLevel, String, VSConstraint, Integer> listener) {
        listeners.add(listener);
    }

    @Override
    public void remove(QuadConsumer<ServerLevel, String, VSConstraint, Integer> listener) {
        throw new NotImplementedException("don't support remove listener now.");
    }
}
