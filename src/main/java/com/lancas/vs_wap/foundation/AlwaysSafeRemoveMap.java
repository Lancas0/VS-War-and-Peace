package com.lancas.vs_wap.foundation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class AlwaysSafeRemoveMap<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Set<K> toRemoveKeys = ConcurrentHashMap.newKeySet();
    private final Set<V> toRemoveVals = ConcurrentHashMap.newKeySet();
    private final Set<BiPredicate<K, V>> toRemovePredicate = ConcurrentHashMap.newKeySet();
    private AtomicBoolean toFlushRemove = new AtomicBoolean(false);

    public void markKeyRemoved(@NotNull K key) { toRemoveKeys.add(key); toFlushRemove.set(true); }
    public void markValRemoved(@NotNull V val) { toRemoveVals.add(val); toFlushRemove.set(true); }
    public void markRemoveIf(@NotNull BiPredicate<K, V> predicate) { toRemovePredicate.add(predicate); toFlushRemove.set(true); }

    public Stream<K> keys() {
        flushRemove();
        return map.keySet().stream();
    }
    public Stream<V> values() {
        flushRemove();
        return map.values().stream();
    }
    /*public Stream<Map.Entry<K, V>> removeFlushedEntries() {
        flushRemove();
        return map.entrySet().stream();
    }*/
    public Map<K, V> flushedMap() { flushRemove(); return map; }

    private void flushRemove() {
        if (toFlushRemove.compareAndSet(true, false)) {
            var removeKeysIt = toRemoveKeys.iterator();
            while (removeKeysIt.hasNext()) { K key = removeKeysIt.next(); removeKeysIt.remove(); map.remove(key); }
            var removeValsIt = toRemoveVals.iterator();
            while (removeValsIt.hasNext()) { V val = removeValsIt.next(); removeValsIt.remove(); map.values().remove(val); }
            var removePredicateIt = toRemovePredicate.iterator();
            while (removePredicateIt.hasNext()) {
                var predicate = removePredicateIt.next();
                removePredicateIt.remove();
                map.entrySet().removeIf(e -> predicate.test(e.getKey(), e.getValue()));
            }
        }
    }


    public int size() { flushRemove(); return map.size(); }
    public boolean isEmpty() { return size() == 0; }

    public boolean containsKey(Object key) { flushRemove(); return map.containsKey(key);}
    public boolean containsValue(Object value) { flushRemove(); return map.containsValue(value);}

    public V get(Object key) { flushRemove(); return map.get(key);}
    public @Nullable V put(K key, V value) { flushRemove(); return map.put(key, value); }
    public @Nullable V putIfAbsent(K key, V value) { flushRemove(); return map.putIfAbsent(key, value); }
    /*public V remove(Object key) {
        K k;
        try { k = (K)key; }
        catch (Exception e) { return null; }

        if (k != null)
            markKeyRemoved(k);
        return map.get(k);
    }*/

    public void putAll(@NotNull Map<? extends K, ? extends V> m) { map.putAll(m); }
    public void clear() { map.clear(); }
}
