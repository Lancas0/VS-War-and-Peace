package com.lancas.vswap.mixins.create.bugfix;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.ui.*;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.infrastructure.ponder.PonderIndex;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(PonderUI.class)
public class PonderUIFix {

    @Shadow (remap = false)
    private PonderButton left;
    @Shadow (remap = false)
    private PonderButton right;
    @Shadow (remap = false)
    private PonderButton scan;
    @Shadow (remap = false)
    private PonderButton chap;
    @Shadow (remap = false)
    private PonderButton userMode;
    @Shadow (remap = false)
    private PonderButton close;
    @Shadow (remap = false)
    private PonderButton replay;
    @Shadow (remap = false)
    private PonderButton slowMode;


    /*@Inject(method = "init", at = @At(value = "INVOKE", target = ""), remap = false)
    public void setButtonZLevels(CallbackInfo ci) {
        left.atZLevel(600);
        right.atZLevel(600);
        scan.atZLevel(600);
        chap.atZLevel(600);
        userMode.atZLevel(600);
        close.atZLevel(600);
        replay.atZLevel(600);
        slowMode.atZLevel(600);
    }*/
    /*@Inject(method = "init", at = @At("TAIL"), remap = false)
    public void setButtonZLevels(CallbackInfo ci) {
        left.atZLevel(600);
        right.atZLevel(600);
        scan.atZLevel(600);
        chap.atZLevel(600);
        userMode.atZLevel(600);
        close.atZLevel(600);
        replay.atZLevel(600);
        slowMode.atZLevel(600);
    }*/
}
