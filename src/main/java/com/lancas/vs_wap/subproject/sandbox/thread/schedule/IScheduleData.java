package com.lancas.vs_wap.subproject.sandbox.thread.schedule;

public interface IScheduleData {
    public ScheduleState getState();
    public void setState(ScheduleState newState);

    public Class<?> getSchedulerType();
}
