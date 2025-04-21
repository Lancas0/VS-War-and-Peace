package com.lancas.vs_wap.foundation;

public class LazyTicks {
    private final int lazyTickCnt;
    private int ticker;

    public LazyTicks(int inLazyTickCnt) {
        lazyTickCnt = inLazyTickCnt;
        ticker = lazyTickCnt;
    }

    public boolean shouldWork() {
        if (--ticker < 0) {
            ticker = lazyTickCnt;
            return true;
        }
        return false;
    }
}
