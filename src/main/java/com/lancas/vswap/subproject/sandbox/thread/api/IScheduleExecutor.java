package com.lancas.vswap.subproject.sandbox.thread.api;

import com.lancas.vswap.subproject.sandbox.thread.schedule.IScheduler;
import com.lancas.vswap.subproject.sandbox.thread.schedule.IScheduleData;

public interface IScheduleExecutor<TThread extends ISandBoxThread<?>> {
    public void register(IScheduler<TThread, ? extends IScheduleData> scheduler);
    public void schedule(IScheduleData schedulerData);
}