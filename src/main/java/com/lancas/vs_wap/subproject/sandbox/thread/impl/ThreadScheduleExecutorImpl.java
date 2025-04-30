package com.lancas.vs_wap.subproject.sandbox.thread.impl;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduler;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduleData;
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vs_wap.subproject.sandbox.thread.api.IScheduleExecutor;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadScheduleExecutorImpl<TThread extends ISandBoxThread<?>> implements IScheduleExecutor<TThread> {
    //schedule
    private final Map<Class<?>, IScheduler<TThread, ? extends IScheduleData>> schedulers = new ConcurrentHashMap<>();
    private final Queue<IScheduleData> scheduleData = new ConcurrentLinkedQueue<>();
    @Override
    public void register(IScheduler<TThread, ? extends IScheduleData> scheduler) {
        schedulers.put(scheduler.getClass(), scheduler);
    }
    @Override
    public void schedule(IScheduleData schedulerData) {
        if (schedulerData == null || !schedulers.containsKey(schedulerData.getSchedulerType())) {
            EzDebug.warn("The scheduleData:" + schedulerData + " is not valid for sandBoxServerThread");
            return;
        }

        scheduleData.add(schedulerData);
    }

    public void doScheduleAll(TThread thread) {
        //schedule works
        var scheduleDataIt = scheduleData.iterator();
        while (scheduleDataIt.hasNext()) {
            IScheduleData data = scheduleDataIt.next();
            IScheduler<TThread, ?> scheduler = schedulers.get(data.getSchedulerType());

            if (scheduler == null) {
                EzDebug.warn("there is a invalid scheduleData added!" + data.getClass().getName());
                scheduleDataIt.remove();
                continue;
            }

            scheduler.safeHandle(thread, data);
            EzDebug.log("current state data:" + data.getState());
            switch (data.getState()) {
                case FAILED -> {
                    EzDebug.warn("handle data failed!");
                    scheduleDataIt.remove();
                }
                case SUCCESS, CANCELED -> scheduleDataIt.remove();
                case NOT_COMPLETE -> {}
                default -> {
                    EzDebug.error("unknown scheduleData state:" + data.getState());
                    scheduleDataIt.remove();
                }
            }
        }
    }
}