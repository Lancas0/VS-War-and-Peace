package com.lancas.vswap.mixins.client.option;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.handler.FlareEffectClientMgr;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.OptionInstance.SliderableValueSet;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(OptionInstance.class)
public abstract class MixinGammaOption {
    @Shadow
    @Final
    public Component caption;

    @Shadow
    @Final
    @Mutable
    public Function<Double, Component> toString;

    @Shadow
    @Final
    @Mutable
    private OptionInstance.ValueSet<Double> values;

    //@Shadow Double value;
    /*@Shadow
    @Final
    private Double initialValue;*/

    @Shadow
    @Final
    @Mutable
    private Codec<Double> codec;

    @Shadow
    @Final
    @Mutable
    private Consumer<Double> onValueUpdate;

    private static final double GAMMA_MIN = -5;
    private static final double GAMMA_MAX = 15;
    private static final double GAMMA_INTERVAL = 0.05;

    @Inject(at = @At("RETURN"), method = "<init>*", remap = false)
    protected void init(CallbackInfo info) {
        if (this.caption.getContents() instanceof TranslatableContents translatableContents)
            EzDebug.log("[OptionMixin] key is:" + translatableContents.getKey());

        if (this.caption.getContents() instanceof TranslatableContents translatableContents && translatableContents.getKey().equals("options.gamma")) {
            EzDebug.log("[OptionMixin] mixin gamma");
            this.onValueUpdate = this::onValueUpdate;
            this.toString = this::toString;
            this.values = new SliderableValueSet<Double>() {
                @Override
                public double toSliderValue(Double aDouble) {
                    return (aDouble - GAMMA_MIN) / (GAMMA_MAX - GAMMA_MIN);
                }
                @Override
                public Double fromSliderValue(double v) {
                    double gammaVal = v * (GAMMA_MAX - GAMMA_MIN) + GAMMA_MIN;
                    FlareEffectClientMgr.playerPreferredGamma = gammaVal;  //set preferred gamma when player use the slider
                    EzDebug.log("player preferred gamma set to " + gammaVal);
                    return gammaVal;
                }
                @Override
                public Optional<Double> validateValue(Double aDouble) {
                    return aDouble >= GAMMA_MIN && aDouble <= GAMMA_MAX ? Optional.of(aDouble) : Optional.empty();
                }
                @Override
                public Codec<Double> codec() { return gammaCodec(); }
            };

            this.codec = this.gammaCodec();

            //set intial value
            FlareEffectClientMgr.playerPreferredGamma = (double)get();
            EzDebug.log("[init] player preferred gamma set to " + FlareEffectClientMgr.playerPreferredGamma);
        }
    }



    private Component toString(Double gamma) {
        return Component.translatable("options.gamma").append(": ").append(Component.literal(Math.round(gamma * 100) + "%"));
    }
    private void onValueUpdate(Double brightness) {
        brightness = Math.round(brightness / GAMMA_INTERVAL) * GAMMA_INTERVAL;
        Minecraft.getInstance().options.gamma().set(brightness);
    }
    private Codec<Double> gammaCodec() {
        return Codec.DOUBLE.xmap(
            //todo temp
            value -> Math.max(GAMMA_MIN, Math.min(GAMMA_MAX, value)),
            value -> value
        );
    }


    @Shadow
    public abstract Object get();
}