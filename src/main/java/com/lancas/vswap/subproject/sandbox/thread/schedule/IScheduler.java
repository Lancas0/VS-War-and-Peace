package com.lancas.vswap.subproject.sandbox.thread.schedule;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.thread.api.ISandBoxThread;

public interface IScheduler<TThread extends ISandBoxThread<?>, TData extends IScheduleData> {
    //public void handle(TShip ship, )
    public Class<?> getDataType();

    public default void safeHandle(TThread thread, IScheduleData data) {
        if (data == null) {
            EzDebug.warn("scheduler try to handle a null data!");
            return;
        }

        TData converted;
        try {
            converted = (TData)data;
        } catch (Exception e) {
            EzDebug.warn("scheduler try to handle a unmatched data!");
            return;
        }

        handleImpl(thread, converted);
    }
    //public void handle(ISandBoxThread<?> thread, TData data);

    public void handleImpl(TThread thread, TData data);
}
