package com.lancas.vs_wap.content.ui;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.content.block.blockentity.ValkyrienBreechBE;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.foundation.network.client2server.ValkyrienBreechLaunchUsePackC2S;
import com.lancas.vs_wap.ship.render.AbstractSliderWidget;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ValkyrienBreechScreen extends AbstractSimiContainerScreen<ValkyrienBreechMenu> {
    private class Slider extends AbstractSliderWidget {
        public Slider(int x, int y, int width, int height) {
            super(x, y, width, height);
            /*onChange.addListener(f -> {
                getBe().nextLaunchUse = ValkyrienBreechBE.MAX_FUEL_F * f;
                getBe().setChanged();
            });*/
        }

        @Override
        public ResourceLocation getSliderBgTexture() { return null; } //{ return new ResourceLocation(ModMain.MODID, "textures/gui/progress_bar_1.png"); }
        @Override
        public ResourceLocation getSliderTexture() { return new ResourceLocation(ModMain.MODID, "textures/gui/progress_bar_1.png"); }

        @Override
        public float getValue01() { /*EzDebug.log("render get:" + getBe().getNextLaunchUse()); */return (float)getBe().getNextLaunchUse() / ValkyrienBreechBE.MAX_FUEL_F; }
        @Override
        public void setValue01(float newVal01) {
            int nextLaunchUse = (int)(ValkyrienBreechBE.MAX_FUEL_F * newVal01);
            getBe().setNextLaunchUse(nextLaunchUse);
            NetworkHandler.sendToServer(new ValkyrienBreechLaunchUsePackC2S(nextLaunchUse, getBe().getBlockPos()));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            //don't impl narration now
        }
    }


    private static final AllGuiTextures BG_BOTTOM = AllGuiTextures.SCHEMATICANNON_BOTTOM;
    private static final AllGuiTextures BG_TOP = AllGuiTextures.SCHEMATICANNON_TOP;

    private static final ResourceLocation BG_TEXTURE =
        new ResourceLocation(ModMain.MODID, "textures/gui/gui1_from_create.png");
    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        new ResourceLocation(ModMain.MODID, "textures/gui/progress_bar_1.png");

    private Slider slider;// = new Slider((this.width - imageWidth) / 2, 0, 100, 16);

    public ValkyrienBreechScreen(ValkyrienBreechMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    private float getStoreAmount01() { return (float)menu.contentHolder.storingFuel / ValkyrienBreechBE.MAX_FUEL_F; }
    private ValkyrienBreechBE getBe() { return menu.contentHolder; }

    @Override
    protected void init() {
        setWindowSize(BG_TOP.width, BG_TOP.height + BG_BOTTOM.height + 2 + AllGuiTextures.PLAYER_INVENTORY.height);
        setWindowOffset(-11, 0);
        super.init();

        slider = new Slider((this.width - imageWidth) / 2 + 38, (this.height - imageHeight) / 2 + 24, 158, 16);
        addRenderableWidget(slider);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        int invX = this.getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.width);
        int invY = this.topPos + BG_TOP.height + BG_BOTTOM.height + 2;
        this.renderPlayerInventory(graphics, invX, invY);
        //int x = this.leftPos;
       // int y = this.topPos;
        //BG_TOP.render(graphics, x, y);
        //BG_BOTTOM.render(graphics, x, y + BG_TOP.height);

        /*RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
           */
        // 5. 计算居中位置
        int x = (this.width - imageWidth) / 2;
        int y = (this.height - imageHeight) / 2;

        // 6. 绘制完整纹理
        graphics.blit(BG_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        //blit(graphics, x, y, 0, 0, imageWidth, imageHeight);

        float progress = (float)menu.contentHolder.storingFuel / ValkyrienBreechBE.MAX_FUEL_F;
        int fuel_progress = (int)(158 * progress);
        //EzDebug.log("storing:" + menu.contentHolder.storingFuel + ", max:" + ValkyrienBreechBE.MAX_FUEL_F + ", progress:" + progress + ", prog_width:" + fuel_progress);
        graphics.blit(PROGRESS_BAR_TEXTURE, x + 38, y + 24, 0, 0, fuel_progress, 16);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);

        int x = (this.width - imageWidth) / 2;
        int y = (this.height - imageHeight) / 2;

    }

    /*public ValkyrienBreechScreen(ValkyrienBreechMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }*/
    /*@Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBackground();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // 绘制背景
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        blit(poseStack, x, y, 0, 0, imageWidth, rows * 18 + 17);
        blit(poseStack, x, y + rows * 18 + 17, 0, 126, imageWidth, 96);
    }*/

}
