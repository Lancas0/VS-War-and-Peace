package com.lancas.vswap.foundation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LazyTicks {
    private int lazyTickCnt;
    private int ticker;

    private LazyTicks() { }
    public LazyTicks(int inLazyTickCnt) { setLazyTicks(inLazyTickCnt); }

    public boolean shouldWork() {
        if (--ticker < 0) {
            ticker = lazyTickCnt;
            return true;
        }
        return false;
    }

    public void setLazyTicks(int inLazyTickCnt) {
        lazyTickCnt = inLazyTickCnt;
        ticker = inLazyTickCnt;
    }

    public void setOnceLazyTicks(int lazy) {
        ticker = lazy;
    }
}
