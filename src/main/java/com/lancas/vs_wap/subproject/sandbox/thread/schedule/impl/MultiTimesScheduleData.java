package com.lancas.vs_wap.subproject.sandbox.thread.schedule.impl;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultiTimesScheduleData extends AbstractScheduleData {
    protected AtomicInteger scheduledTimes = new AtomicInteger(0);

    public int getScheduledTimes() { return scheduledTimes.get(); }

}