package com.lancas.vswap.event.impl;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.api.IQuadEvent;
import com.lancas.vswap.event.listener.QuadRemoveAfterSuccessListener;
import com.lancas.vswap.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuadEventImpl<T, U, V, W> implements IQuadEvent<T, U, V, W> {

    private final List<QuadConsumer<T, U, V, W>> listeners = new ArrayList<>();
    private final Set<QuadConsumer<T, U, V, W>> toRemove = ConcurrentHashMap.newKeySet();

    //@Override
    public void invokeAll(T t, U u, V v, W w) {
        var listenersIt = listeners.iterator();
        while (listenersIt.hasNext()) {
            var listener = listenersIt.next();
            if (toRemove.contains(listener)) {
                listenersIt.remove();
                toRemove.remove(listener);
                EzDebug.light("tri event remove a listener");
                continue;
            }

            listener.apply(t, u, v, w);
            if (listener instanceof QuadRemoveAfterSuccessListener<T,U,V,W> listenerType1) {
                if (listenerType1.isSuccess())
                    listenersIt.remove();
            }
        }
    }

    @Override
    public QuadEventImpl<T, U, V, W> addListener(@NotNull QuadConsumer<T, U, V, W> listener) {
        listeners.add(listener);
        return this;
    }
    @Override
    public QuadEventImpl<T, U, V, W> remove(@NotNull QuadConsumer<T, U, V, W> listener) {
        toRemove.add(listener);
        return this;
    }
}
