package com.lancas.vswap.event;

import com.lancas.vswap.event.api.ITriEvent;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;

import java.util.ArrayList;
import java.util.List;

public class ConstraintRemoveEvent implements ITriEvent<ServerLevel, String, VSConstraint> {

    private final List<TriConsumer<ServerLevel, String, VSConstraint>> listeners = new ArrayList<>();


    public void invokeAll(ServerLevel level, String key, VSConstraint constraint) {
        for (var listener : listeners)
            listener.accept(level, key, constraint);
    }

    @Override
    public void addListener(@NotNull TriConsumer<ServerLevel, String, VSConstraint> listener) {
        listeners.add(listener);
    }

    @Override
    public void remove(@NotNull TriConsumer<ServerLevel, String, VSConstraint> listener) {
        throw new NotImplementedException("don't support remove listener now.");
    }
}
