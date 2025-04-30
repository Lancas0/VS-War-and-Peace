package com.lancas.vs_wap.subproject.sandbox.thread;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SandBoxThreadRegistry<TWorld extends ISandBoxWorld> {
    private final Map<Class<?>, ISandBoxThread<TWorld>> threads = new ConcurrentHashMap<>();

    public void register(ISandBoxThread<TWorld> thread) {
        threads.put(thread.getClass(), thread);
    }
    public ISandBoxThread<TWorld> getThread(Class<?> type) {
        return threads.get(type);
    }

    public void notifyStart(Class<? extends ISandBoxThread<TWorld>> type) {
        if (threads.containsKey(type)) {
            threads.get(type).start();
        } else {
            EzDebug.warn("the thread " + type.getName() + ", is not registered!");
        }
    }
    public void notifyAllStart() {
        for (ISandBoxThread<TWorld> thread : threads.values())
            thread.start();
    }

    public void notifyPause(Class<? extends ISandBoxThread<TWorld>> type) {
        if (threads.containsKey(type)) {
            threads.get(type).pause();
        } else {
            EzDebug.warn("the thread " + type.getName() + ", is not registered!");
        }
    }
    public void notifyAllPause() {
        for (ISandBoxThread<TWorld> thread : threads.values())
            thread.pause();
    }

}
