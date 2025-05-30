package com.lancas.vswap.foundation.handler.multiblock.alog;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.function.Consumer;


//default order is increasing order (allow greater one push)
public class MonotonicStack<T> {
    protected Deque<T> stack = new ArrayDeque<>();
    protected Comparator<T> comparator;

    public MonotonicStack(Comparator<T> inComparator) {
        comparator = inComparator;
    }

    public void push(T element) {
        while (!stack.isEmpty() && comparator.compare(stack.peek(), element) > 0) {  //if stack top > element
            stack.pop();
        }
        stack.push(element);
    }
    public void pushWithPrePushPostPopCallback(T element, Consumer<T> callback) {
        while (!stack.isEmpty() && comparator.compare(stack.peek(), element) > 0) {  //if stack top > element
            callback.accept(stack.pop());
        }
        stack.push(element);
    }

    public T pop() { return stack.pop(); }

    public T top() { return stack.peek(); }

    public boolean isEmpty() { return stack.isEmpty(); }
    public int size() { return stack.size(); }
}