package com.lancas.vs_wap.event.impl;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.api.IBiEvent;
import com.lancas.vs_wap.event.api.ILazyEventParam;
import com.lancas.vs_wap.event.listener.BiRemoveAfterSuccessListener;
import com.lancas.vs_wap.foundation.BiTuple;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class BiLazyEvent<T extends ILazyEventParam<T>, U extends ILazyEventParam<U>> implements IBiEvent<T, U> {
    private final byte[] invokingLock = new byte[0];

    private final List<BiConsumer<T, U>> listeners = new ArrayList<>();
    private final Set<BiConsumer<T, U>> toRemove = ConcurrentHashMap.newKeySet();

    private final Map<Object, BiTuple<T, U>> waitParams = new Hashtable<>();

    //@Override
    public void invokeAll() {
        //avoid
        synchronized (invokingLock) {
            for (var param : waitParams.values()) {
                var listenersIt = listeners.iterator();
                while (listenersIt.hasNext()) {
                    var listener = listenersIt.next();
                    if (toRemove.contains(listener)) {
                        listenersIt.remove();
                        toRemove.remove(listener);
                        EzDebug.light("tri event remove a listener");
                        continue;
                    }

                    listener.accept(param.getFirst(), param.getSecond());
                    if (listener instanceof BiRemoveAfterSuccessListener<T, U> listenerType1) {
                        if (listenerType1.isSuccess()) {
                            listenersIt.remove();
                            EzDebug.light("successfully remove after success");
                        }
                    }
                }
            }
            waitParams.clear();
        }
    }

    public void schedule(Object key, T t, U u) {
        if (key == null) return;

        //only can schedule when not invoking
        synchronized (invokingLock) {
            waitParams.put(key, new BiTuple<>(t.getImmutable(), u.getImmutable()));
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
