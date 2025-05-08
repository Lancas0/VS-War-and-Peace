package com.lancas.vs_wap.ship.render;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.impl.SingleEventSetImpl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

//todo value vadilator
public abstract class AbstractSliderWidget extends AbstractWidget {
    public AbstractSliderWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public abstract ResourceLocation getSliderBgTexture();
    public abstract ResourceLocation getSliderTexture();
    public abstract float getValue01();
    public abstract void setValue01(float newVal01);

    public SingleEventSetImpl<Float> onChange = new SingleEventSetImpl<>();
    /*public ConsumptionSlider(int x, int y, int width, int height,
                             int min, int max, double initialValue) {
        super(x, y, width, height, Component.empty());
        this.minValue = min;
        this.maxValue = max;
        this.value = (initialValue - min) / (max - min);
    }*/

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (getSliderBgTexture() != null)
            graphics.blit(getSliderBgTexture(), getX(), getY(), 0, 0, width, height, width, height * 2);

        int sliderX = (int)(getValue01() * width);
        //graphics.blit(getSliderTexture(), getX() + sliderX, getY(), 0, height, 8, height, width, height*2);
        graphics.blit(getSliderTexture(), getX() + sliderX, getY(), 0, 0, 8, height);

        //EzDebug.log("getValue01:" + getValue01() + ", w:" + width + ", render slider x:" + (getX() + sliderX));
    }
    @Override
    public void onClick(double mouseX, double mouseY) {
        updateValueFromMouse((float)mouseX);
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        updateValueFromMouse((float)mouseX);
    }

    private void updateValueFromMouse(float mouseX) {
        float newValue01 = (mouseX - getX()) / width;
        newValue01 = Math.min(Math.max(0, newValue01), 1);
        setValue01(newValue01);

        //EzDebug.log("mouseX:" + mouseX + ", getX:" + getX() + ", width:" + width + ", newVal:" + newValue01);

        onChange.invokeAll(getValue01());
    }
}