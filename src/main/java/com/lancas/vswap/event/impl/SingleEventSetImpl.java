package com.lancas.vswap.event.impl;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.api.ISingleEvent;
import com.lancas.vswap.event.listener.ICancelableListener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SingleEventSetImpl<T> implements ISingleEvent<T> {

    private final Set<Consumer<T>> listeners = ConcurrentHashMap.newKeySet();
    //private final Set<Consumer<T>> toRemove = ConcurrentHashMap.newKeySet();

    //@Override
    public void invokeAll(T t) {
        var listenersIt = listeners.iterator();
        while (listenersIt.hasNext()) {
            var listener = listenersIt.next();

            listener.accept(t);
            /* if (listener instanceof SingleRemoveAfterSuccessListener<T> listenerType1) {
                if (listenerType1.isSuccess()) {
                    listenersIt.remove();
                    EzDebug.light("successfully remove after success");
                }
            }*/
            if (listener instanceof ICancelableListener cancelable && cancelable.shouldCancel()) {
                listenersIt.remove();
                EzDebug.light("successfully remove listener");
            }
        }
    }

    @Override
    public void addListener(@NotNull Consumer<T> listener) {
        listeners.add(listener);
    }
    @Override
    public void remove(@NotNull Consumer<T> listener) {
        listeners.remove(listener);
    }
}
