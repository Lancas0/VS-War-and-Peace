package com.lancas.vswap.event.impl;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.api.IBiEvent;
import com.lancas.vswap.event.listener.BiRemoveAfterSuccessListener;
import com.lancas.vswap.event.listener.ICancelableListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class BiEventImpl<T, U> implements IBiEvent<T, U> {

    private final List<BiConsumer<T, U>> listeners = new ArrayList<>();
    private final Set<BiConsumer<T, U>> toRemove = ConcurrentHashMap.newKeySet();

    //@Override
    public void invokeAll(T t, U u) {
        var listenersIt = listeners.iterator();
        while (listenersIt.hasNext()) {
            var listener = listenersIt.next();
            if (toRemove.contains(listener)) {
                listenersIt.remove();
                toRemove.remove(listener);
                EzDebug.light("tri event remove a listener");
                continue;
            }

            boolean removed = false;

            listener.accept(t, u);
            if (listener instanceof BiRemoveAfterSuccessListener<T, U> listenerType1) {
                if (listenerType1.isSuccess() && !removed) {
                    removed = true;
                    listenersIt.remove();
                    EzDebug.light("successfully remove after success");
                }
            }
            if (listener instanceof ICancelableListener cancelable) {
                if (cancelable.shouldCancel() && !removed) {
                    removed = true;
                    listenersIt.remove();
                    EzDebug.light("successfully remove listener");
                }
            }

        }
    }

    @Override
    public void addListener(@NotNull BiConsumer<T, U> listener) {
        listeners.add(listener);
    }

    @Override
    public void remove(@NotNull BiConsumer<T, U> listener) {
        toRemove.add(listener);
    }
}
