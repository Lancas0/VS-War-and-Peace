package com.lancas.vswap.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IterateUtil {
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
            .mapToObj(i -> temp[temp.length - i - 1]);
    }

    public static <T> Iterable<T> iterable(Stream<T> stream) {
        return stream::iterator;
    }

    public static <T> ListIterator<T> listIterator(List<T> list, int fromIx) {
        return list.listIterator(fromIx);
    }
    public static <T> Iterable<T> listIterable(List<T> list, int fromIx) {
        return () -> list.listIterator(fromIx);
    }

    public static <T> Iterable<T> reverseListIterable(List<T> list) {
        ListIterator<T> listIterator = listIterator(list, list.size());

        return new Iterable<T>() {
            @Override
            public @NotNull Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return listIterator.hasPrevious();
                    }
                    @Override
                    public T next() {
                        return listIterator.previous();
                    }
                };
            }
        };
    }


    //public static <T> Iterator<T> reverseIterator(Collection)
}
