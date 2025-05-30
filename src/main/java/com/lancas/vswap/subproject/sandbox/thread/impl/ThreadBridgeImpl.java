package com.lancas.vswap.subproject.sandbox.thread.impl;

/*
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vs_wap.subproject.sandbox.thread.api.IThreadBridge;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ThreadBridgeImpl<TThread extends ISandBoxThread<?>> implements IThreadBridge<TThread> {

    private Queue<Consumer<TThread>> tasks = new ConcurrentLinkedQueue<>();
    @Override
    public void addTask(Consumer<TThread> task) {
        if (task != null)
            tasks.add(task);
    }

    //todo timeout or other thing
    public void doAllTasks(TThread thread) {
        while (!tasks.isEmpty()) {
            var task = tasks.poll();
            if (task != null)
                task.accept(thread);
        }
    }
}
*/