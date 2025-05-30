package com.lancas.vswap.content.ui;

import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.client2server.sync.SyncPlayerHoldingItemNbtPacketC2S;
import com.lancas.vswap.ship.data.IShipSchemeData;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.ponder.ui.LayoutHelper;
import com.simibubi.create.foundation.ponder.ui.PonderButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DockerStateScreen extends AbstractSimiScreen {
    protected final ItemStack targetStack;
    protected final InteractionHand holdingHand;
    protected final IShipSchemeData shipData;

    protected final Docker docker;

    protected Rect2i itemArea;

    public DockerStateScreen(ItemStack inTargetStack, InteractionHand inHoldingHand) {
        targetStack = inTargetStack;
        holdingHand = inHoldingHand;

        if (targetStack.getItem() instanceof Docker d) {
            shipData = d.getShipData(targetStack);
            docker = d;
        } else {
            EzDebug.warn("fail to get shipData");
            shipData = null;
            docker = null;
        }
    }

    @Override
    protected void init() {
        super.init();

        if (shipData == null)
            return;

        LayoutHelper layout = LayoutHelper.centeredHorizontal(2, 1, 28, 28, 8);
        this.itemArea = layout.getArea();
        int itemCenterX = (int)((double)this.width * (double)0.5F);
        int itemCenterY = this.getItemsY();

        PonderButton bShip = (new PonderButton(itemCenterX + layout.getX() + 4, itemCenterY + layout.getY() + 4)).showing(WapItems.DOCKER.get().getDefaultInstance());//.showingElement(RenderElement.of(AllGuiTextures.JEI_ARROW));//.showing(Items.DIRT.getDefaultInstance());
        layout.next();
        PonderButton bBlock = (new PonderButton(itemCenterX + layout.getX() + 4, itemCenterY + layout.getY() + 4)).showing(Items.STONE.getDefaultInstance());

        boolean isScale1 = Math.abs(shipData.getScale().x() - 1) < 1E-4;
        if (!isScale1) {  //scale is not 1
            bBlock.setAlpha(0.5f);
        }

        bShip.withCallback(() -> {
            Docker.setMode(targetStack, Docker.PlaceMode.VsShip);
            CompoundTag toSyncNbt = targetStack.getOrCreateTag();
            NetworkHandler.sendToServer(new SyncPlayerHoldingItemNbtPacketC2S(holdingHand, toSyncNbt));
            this.onClose();
        });
        bBlock.withCallback(() -> {
            if (!isScale1)
                return;

            Docker.setMode(targetStack, Docker.PlaceMode.Blocks);
            CompoundTag toSyncNbt = targetStack.getOrCreateTag();
            NetworkHandler.sendToServer(new SyncPlayerHoldingItemNbtPacketC2S(holdingHand, toSyncNbt));
            this.onClose();
        });

        this.addRenderableWidget(bShip);
        this.addRenderableWidget(bBlock);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        //super.renderWindow(graphics, mouseX, mouseY, partialTicks);
        //this.renderItems(graphics, mouseX, mouseY, partialTicks);
        /*this.renderChapters(graphics, mouseX, mouseY, partialTicks);

        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate((double)(this.width / 2 - 120), (double)this.height * 0.15 - (double)40.0F, (double)0.0F);
        ms.pushPose();
        int x = 59;
        int y = 31;
        String title = "Temp Title";//this.tag.getTitle();
        int streakHeight = 35;
        UIRenderHelper.streak(graphics, 0.0F, x - 4, y - 12 + streakHeight / 2, streakHeight, 240);
        (new BoxElement()).withBackground(Theme.c(Theme.Key.PONDER_BACKGROUND_FLAT)).gradientBorder(Theme.p(Theme.Key.PONDER_IDLE)).at(21.0F, 21.0F, 100.0F).withBounds(30, 30).render(graphics);
        graphics.drawString(this.font, Lang.translateDirect("ponder.pondering", new Object[0]), x, y - 6, Theme.i(Theme.Key.TEXT_DARKER), false);
        y += 8;
        x += 0;
        ms.translate((float)x, (float)y, 0.0F);
        ms.translate(0.0F, 0.0F, 5.0F);
        graphics.drawString(this.font, title, 0, 0, Theme.i(Theme.Key.TEXT), false);
        ms.popPose();
        ms.pushPose();
        ms.translate(23.0F, 23.0F, 10.0F);
        ms.scale(1.66F, 1.66F, 1.66F);
        this.tag.render(graphics, 0, 0);
        ms.popPose();
        ms.popPose();
        ms.pushPose();
        int w = (int)((double)this.width * 0.45);
        x = (this.width - w) / 2;
        y = this.getItemsY() - 10 + Math.max(this.itemArea.getHeight(), 48);
        String desc = this.tag.getDescription();
        int h = this.font.wordWrapHeight(desc, w);
        (new BoxElement()).withBackground(Theme.c(Theme.Key.PONDER_BACKGROUND_FLAT)).gradientBorder(Theme.p(Theme.Key.PONDER_IDLE)).at((float)(x - 3), (float)(y - 3), 90.0F).withBounds(w + 6, h + 6).render(graphics);
        ms.translate(0.0F, 0.0F, 100.0F);
        FontHelper.drawSplitString(ms, this.font, desc, x, y, w, Theme.i(Theme.Key.TEXT));
        ms.popPose();*/
    }

    /*protected void renderItems(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.items.isEmpty()) {
            int x = (int)((double)this.width * (double)0.5F);
            int y = this.getItemsY();
            String relatedTitle = Lang.translateDirect("ponder.associated", new Object[0]).getString();
            int stringWidth = this.font.width(relatedTitle);
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate((float)x, (float)y, 0.0F);
            (new BoxElement()).withBackground(Theme.c(Theme.Key.PONDER_BACKGROUND_FLAT)).gradientBorder(Theme.p(Theme.Key.PONDER_IDLE)).at((float)(this.windowWidth - stringWidth) / 2.0F - 5.0F, (float)(this.itemArea.getY() - 21), 100.0F).withBounds(stringWidth + 10, 10).render(graphics);
            ms.translate(0.0F, 0.0F, 200.0F);
            graphics.drawCenteredString(this.font, relatedTitle, this.windowWidth / 2, this.itemArea.getY() - 20, Theme.i(Theme.Key.TEXT));
            ms.translate(0.0F, 0.0F, -200.0F);
            UIRenderHelper.streak(graphics, 0.0F, 0, 0, this.itemArea.getHeight() + 10, this.itemArea.getWidth() / 2 + 75);
            UIRenderHelper.streak(graphics, 180.0F, 0, 0, this.itemArea.getHeight() + 10, this.itemArea.getWidth() / 2 + 75);
            ms.popPose();
        }
    }*/

    public int getItemsY() {
        return (int)(0.15 * (double)this.height + (double)85.0F);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
