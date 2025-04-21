package com.lancas.vs_wap.event.impl;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.api.IQuadEvent;
import com.lancas.vs_wap.event.listener.QuadRemoveAfterSuccessListener;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuadEventImpl<T, U, V, W> implements IQuadEvent<T, U, V, W> {

    private final List<QuadConsumer<T, U, V, W>> listeners = new ArrayList<>();
    private final Set<QuadConsumer<T, U, V, W>> toRemove = ConcurrentHashMap.newKeySet();

    @Override
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
    public void add(@NotNull QuadConsumer<T, U, V, W> listener) {
        listeners.add(listener);
    }
    @Override
    public void remove(@NotNull QuadConsumer<T, U, V, W> listener) {
        toRemove.add(listener);
    }
}
