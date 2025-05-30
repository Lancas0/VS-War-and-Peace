package com.lancas.vswap.event.impl;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.api.ILazyEventParam;
import com.lancas.vswap.event.api.ITriEvent;
import com.lancas.vswap.event.listener.TriRemoveAfterSuccessListener;
import com.lancas.vswap.foundation.TriTuple;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TriLazyEvent<T extends ILazyEventParam<T>, U extends ILazyEventParam<U>, V extends ILazyEventParam<V>> implements ITriEvent<T, U, V> {
    //private final Object invokingLock = new Object();

    private final List<TriConsumer<T, U, V>> listeners = new ArrayList<>();
    private final Set<TriConsumer<T, U, V>> toRemove = ConcurrentHashMap.newKeySet();

    private final Map<Object, TriTuple<T, U, V>> waitParams = new ConcurrentHashMap<>();

    //@Override
    public synchronized void invokeAll() {
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

                listener.accept(param.getFirst(), param.getSecond(), param.getThird());
                if (listener instanceof TriRemoveAfterSuccessListener<T, U, V> listenerType1) {
                    if (listenerType1.isSuccess()) {
                        listenersIt.remove();
                        EzDebug.light("successfully remove after success");
                    }
                }
            }
        }
        waitParams.clear();
    }

    public void schedule(Object key, T t, U u, V v) {
        if (key == null) return;
        waitParams.put(key, new TriTuple<>(t.getImmutable(), u.getImmutable(), v.getImmutable()));
        //only can schedule when not invoking
        /*synchronized (invokingLock) {

        }*/
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
