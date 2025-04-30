package com.lancas.vs_wap.event.impl;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.api.ISingleEvent;
import com.lancas.vs_wap.event.listener.SingleRemoveAfterSuccessListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class SingleEventImpl<T> implements ISingleEvent<T> {

    private final Queue<Consumer<T>> listeners = new ConcurrentLinkedQueue<>();
    private final Set<Consumer<T>> toRemove = ConcurrentHashMap.newKeySet();

    //@Override
    public void invokeAll(T t) {
        var listenersIt = listeners.iterator();
        while (listenersIt.hasNext()) {
            var listener = listenersIt.next();
            if (toRemove.contains(listener)) {
                listenersIt.remove();
                toRemove.remove(listener);
                EzDebug.light("tri event remove a listener");
                continue;
            }

            listener.accept(t);
            if (listener instanceof SingleRemoveAfterSuccessListener<T> listenerType1) {
                if (listenerType1.isSuccess()) {
                    listenersIt.remove();
                    EzDebug.light("successfully remove after success");
                }
            }
        }
    }

    @Override
    public void addListener(@NotNull Consumer<T> listener) {
        listeners.add(listener);
    }
    @Override
    public void remove(@NotNull Consumer<T> listener) {
        toRemove.add(listener);
    }
}
