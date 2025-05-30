package com.lancas.vswap.content.capacity;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface ICache<T> {
    public boolean canAdd(T element);
    public boolean add(T element);
    public @Nullable T peek();
    public @Nullable T pop();

    public int flush();

    public boolean isEmpty();
    public int size();

    public static <TT> ICache<TT> of(List<TT> inList, Predicate<TT> flushCallback) {
        return new ICache<TT>() {
            private List<TT> list = inList;
            @Override
            public boolean canAdd(TT element) { return true; }  //don't expect inModifiable list
            @Override
            public boolean add(TT element) { return list.add(element); }
            @Override
            public @Nullable TT peek() { return list.isEmpty() ? null : list.get(0); }
            @Override
            public @Nullable TT pop() {
                if (list.isEmpty())
                    return null;
                return list.remove(0);
            }

            @Override
            public int flush() {
                int flushCnt = 0;
                for (int i = list.size() - 1; i >= 0; --i) {
                    if (flushCallback.test(list.get(i))) {
                        flushCnt++;
                        list.remove(i);
                    }
                }
                return flushCnt;
            }

            @Override
            public boolean isEmpty() { return list.isEmpty(); }
            @Override
            public int size() { return list.size(); }
        };
    }
}
