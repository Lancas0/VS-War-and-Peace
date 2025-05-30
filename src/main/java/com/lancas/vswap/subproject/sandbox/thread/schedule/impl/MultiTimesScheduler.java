package com.lancas.vswap.subproject.sandbox.thread.schedule.impl;

import com.lancas.vswap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vswap.subproject.sandbox.thread.schedule.IScheduler;

public abstract class MultiTimesScheduler<TThread extends ISandBoxThread<?>, TData extends MultiTimesScheduleData>
    implements IScheduler<TThread, TData> {

    protected abstract void handleNoWorryTimes(TThread thread, TData data);

    @Override
    public void handleImpl(TThread thread, TData data) {
        handleNoWorryTimes(thread, data);
        data.scheduledTimes.incrementAndGet();
    }
}
