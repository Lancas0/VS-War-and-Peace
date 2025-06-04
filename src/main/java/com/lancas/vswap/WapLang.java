package com.lancas.vswap;

import com.lancas.vswap.util.StrUtil;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class WapLang {
    /*public static Component fatalPropellant() {
        return Component.translatable()
    }*/
    public static String degreeFormatter(double v) { return StrUtil.F0(v) + "°"; }
    public static String percent01Format(double v) { return StrUtil.F0(v * 100) + "%"; }
}
