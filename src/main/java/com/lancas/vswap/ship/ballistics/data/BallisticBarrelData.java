package com.lancas.vswap.ship.ballistics.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BallisticBarrelData {
    public final double barrelRealLen;

    public BallisticBarrelData(double inBarrelRealLen) {
        barrelRealLen = inBarrelRealLen;
    }
}
