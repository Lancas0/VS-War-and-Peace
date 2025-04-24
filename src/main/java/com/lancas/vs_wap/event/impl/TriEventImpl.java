package com.lancas.vs_wap.event.impl;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.api.ITriEvent;
import com.lancas.vs_wap.event.listener.TriRemoveAfterSuccessListener;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TriEventImpl<T, U, V> implements ITriEvent<T, U, V> {

    private final List<TriConsumer<T, U, V>> listeners = new ArrayList<>();
    private final Set<TriConsumer<T, U, V>> toRemove = ConcurrentHashMap.newKeySet();

    //@Override
    public void invokeAll(T t, U u, V v) {
        var listenersIt = listeners.iterator();
        while (listenersIt.hasNext()) {
            var listener = listenersIt.next();
            if (toRemove.contains(listener)) {
                listenersIt.remove();
                toRemove.remove(listener);
                EzDebug.light("tri event remove a listener");
                continue;
            }

            listener.accept(t, u, v);
            if (listener instanceof TriRemoveAfterSuccessListener<T,U,V> listenerType1) {
                if (listenerType1.isSuccess()) {
                    listenersIt.remove();
                    EzDebug.light("successfully remove after success");
                }
            }
        }
    }

    @Override
    public void addListener(@NotNull TriConsumer<T, U, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void remove(@NotNull TriConsumer<T, U, V> listener) {
        toRemove.add(listener);
    }
}
