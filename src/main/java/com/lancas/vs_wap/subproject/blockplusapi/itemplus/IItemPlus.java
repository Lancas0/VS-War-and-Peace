package com.lancas.vs_wap.subproject.blockplusapi.itemplus;

import java.util.Hashtable;
import java.util.List;
import java.util.function.Supplier;

public interface IItemPlus {
    public abstract List<ItemAdder> getAdders();

    public static final Hashtable<Class<? extends IItemPlus>, List<ItemAdder>> addersCache = new Hashtable<>();
    public static List<ItemAdder> addersIfAbsent(Class<? extends IItemPlus> type, Supplier<List<ItemAdder>> addersSupplier) {
        if (!addersCache.containsKey(type))
            addersCache.put(type, addersSupplier.get());

        return addersCache.get(type);
    }
}
