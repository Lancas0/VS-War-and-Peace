package com.lancas.vs_wap.subproject.sandbox.thread.schedule.impl;

import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduleData;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.ScheduleState;

public abstract class AbstractScheduleData implements IScheduleData {
    protected ScheduleState state = ScheduleState.NOT_COMPLETE;

    @Override
    public ScheduleState getState() { return state; }
    @Override
    public void setState(ScheduleState newState) { state = newState; }
}
