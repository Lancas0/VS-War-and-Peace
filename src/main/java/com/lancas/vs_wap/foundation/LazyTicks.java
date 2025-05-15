package com.lancas.vs_wap.foundation;

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
