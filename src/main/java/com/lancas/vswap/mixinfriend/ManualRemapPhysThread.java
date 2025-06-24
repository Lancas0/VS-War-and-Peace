package com.lancas.vswap.mixinfriend;

import com.lancas.vswap.mixins.valkyrien.VsPhysThreadAccessor;
import org.valkyrienskies.core.impl.shadow.At;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public interface ManualRemapPhysThread {
    public default VsPhysThreadAccessor getAccessor() {
        return (VsPhysThreadAccessor)this;
    }

    public default boolean getShouldStop() { return getAccessor().getD(); }

    public default At getPhysItem() { return getAccessor().getB(); }

    //NOT SURE
    public default ReentrantLock getTickerLock() { return getAccessor().getH(); }

    public default int getPhysTickCnt() { return getAccessor().getG(); }
    public default int getAndIncPhysTickCnt() {
        int tickCnt = getPhysTickCnt();
        getAccessor().setG(tickCnt + 1);
        return tickCnt;
    }

    //NOT SURE
    public default Condition getPhysTickFull() { return getAccessor().getI(); }
    //NOT SURE
    public default Condition getPhysTickAvailable() { return getAccessor().getJ(); }

    //NOT SURE
    public default long getPostponeNano() { return getAccessor().getE(); }
    public default void setPostponeNano(long nano) { getAccessor().setE(nano); }


    public default Queue<Long> getTimestampQueue() { return getAccessor().getF(); }

    //NOT SURE
    public default ReentrantLock getPhysLock() { return getAccessor().getK(); }

    //NOT SURE
    public default Condition getPhysCondition() { return getAccessor().getL(); }

    public default int getTargetFrame() { return getAccessor().getC(); }
}
