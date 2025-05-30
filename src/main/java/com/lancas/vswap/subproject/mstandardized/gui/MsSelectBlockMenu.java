package com.lancas.vswap.subproject.mstandardized.gui;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.client2server.sync.SyncPlayerHoldingItemNbtPacketC2S;
import com.lancas.vswap.subproject.mstandardized.Category;
import com.lancas.vswap.subproject.mstandardized.ClientBlockSelection;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.*;
import com.simibubi.create.foundation.gui.element.BoxElement;
import com.simibubi.create.foundation.ponder.ui.*;
import com.simibubi.create.foundation.utility.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class MsSelectBlockMenu extends AbstractSimiScreen {
    protected final ItemStack targetStack;
    protected final InteractionHand holdingHand;
    protected final Category category;

    protected Item iconItem;
    protected final List<BlockItem> items = new ArrayList<>();

    //public static final String ASSOCIATED = "ponder.associated";
    //protected final List<Item> items;
   // private final double itemXmult = (double)0.5F;
    protected Rect2i itemArea;
    //protected final List<PonderChapter> chapters;
    /*private final double chapterXmult = (double)0.5F;
    private final double chapterYmult = (double)0.75F;
    protected Rect2i chapterArea;
    private final double mainYmult = 0.15;
    private ItemStack hoveredItem;
    public PonderTagScreen s;*/

    public MsSelectBlockMenu(ItemStack inTargetStack, InteractionHand inHoldingHand/*, Category inCategory*/) {
        targetStack = inTargetStack;
        category = MaterialStandardizedItem.getCategory(inTargetStack);
        holdingHand = inHoldingHand;

        if (category.isEmpty()) {
            EzDebug.warn("open select block ui with empty category!");
        }
        //category = inCategory;
        /*this.hoveredItem = ItemStack.EMPTY;
        this.tag = tag;
        this.items = new ArrayList();
        this.chapters = new ArrayList();*/
    }

    @Override
    protected void init() {
        super.init();

        items.clear();
        category.getAllBlock().map(b -> (BlockItem)b.asItem()).forEach(items::add);

        iconItem = category.getIconItem();

        int rowCount = Mth.ceil(items.size() / 11.0);//Mth.clamp((int)Math.ceil((double)items.size() / 11.0), 1, 3);
        LayoutHelper layout = LayoutHelper.centeredHorizontal(this.items.size(), rowCount, 28, 28, 8);
        this.itemArea = layout.getArea();
        int itemCenterX = (int)((double)this.width * (double)0.5F);
        int itemCenterY = this.getItemsY();

        for(BlockItem i : this.items) {
            PonderButton b = (new PonderButton(itemCenterX + layout.getX() + 4, itemCenterY + layout.getY() + 4)).showing(new ItemStack(i));
            b.withCallback((mouseX, mouseY) -> {
                /*MaterialStandardizedItem.setSelectingBlock(targetStack, i.getBlock());
                CompoundTag toSyncNbt = targetStack.getOrCreateTag();

                NetworkHandler.sendToServer(new SyncPlayerHoldingItemNbtPacketC2S(holdingHand, toSyncNbt));*/
                /*Player player = Minecraft.getInstance().player;
                if (player == null)
                    return;

                PlayerMsSelectionMemory.getMemory(player);*/

                ClientBlockSelection.setSelection(category.categoryName, i.getBlock());

                this.onClose();
            });

            this.addRenderableWidget(b);
            layout.next();
        }

        /*if (!this.tag.getMainItem().isEmpty()) {
            ResourceLocation registryName = RegisteredObjects.getKeyOrThrow(this.tag.getMainItem().getItem());
            PonderButton b = (new PonderButton(itemCenterX - layout.getTotalWidth() / 2 - 48, itemCenterY - 10)).showing(this.tag.getMainItem());
            b.withCustomBackground(Theme.c(Theme.Key.PONDER_BACKGROUND_IMPORTANT));
            if (PonderRegistry.ALL.containsKey(registryName)) {
                b.withCallback((mouseX, mouseY) -> {
                    this.centerScalingOn(mouseX, mouseY);
                    ScreenOpener.transitionTo(PonderUI.of(this.tag.getMainItem(), this.tag));
                });
            } else if (registryName.getNamespace().equals("create")) {
                b.withBorderColors(Theme.p(Theme.Key.PONDER_MISSING_CREATE)).animateColors(false);
            } else {
                b.withBorderColors(Theme.p(Theme.Key.PONDER_MISSING_VANILLA)).animateColors(false);
            }

            this.addRenderableWidget(b);
        }*/

        /*this.chapters.clear();
        this.chapters.addAll(PonderRegistry.TAGS.getChapters(this.tag));
        rowCount = Mth.clamp((int)Math.ceil((double)((float)this.chapters.size() / 3.0F)), 1, 3);
        layout = LayoutHelper.centeredHorizontal(this.chapters.size(), rowCount, 200, 38, 16);
        this.chapterArea = layout.getArea();
        int chapterCenterX = (int)((double)this.width * (double)0.5F);
        int chapterCenterY = (int)((double)this.height * (double)0.75F);

        for(PonderChapter chapter : this.chapters) {
            ChapterLabel label = new ChapterLabel(chapter, chapterCenterX + layout.getX(), chapterCenterY + layout.getY(), (mouseX, mouseY) -> {
                this.centerScalingOn(mouseX, mouseY);
                ScreenOpener.transitionTo(PonderUI.of(chapter));
            });
            this.addRenderableWidget(label);
            layout.next();
        }*/

    }

    /*protected void initBackTrackIcon(PonderButton backTrack) {
        backTrack.showing(this.tag);
    }*/

    /*public void tick() {
        super.tick();
        ++PonderUI.ponderTicks;
        this.hoveredItem = ItemStack.EMPTY;
        Window w = this.minecraft.getWindow();
        double mouseX = this.minecraft.mouseHandler.xpos() * (double)w.getGuiScaledWidth() / (double)w.getScreenWidth();
        double mouseY = this.minecraft.mouseHandler.ypos() * (double)w.getGuiScaledHeight() / (double)w.getScreenHeight();

        for(GuiEventListener child : this.children()) {
            if (child instanceof PonderButton button) {
                if (button.isMouseOver(mouseX, mouseY)) {
                    this.hoveredItem = button.getItem();
                }
            }
        }
    }*/

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderItems(graphics, mouseX, mouseY, partialTicks);
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

    protected void renderItems(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
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
    }

    public int getItemsY() {
        return (int)(0.15 * (double)this.height + (double)85.0F);
    }

    /*protected void renderChapters(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.chapters.isEmpty()) {
            int chapterX = (int)((double)this.width * (double)0.5F);
            int chapterY = (int)((double)this.height * (double)0.75F);
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate((float)chapterX, (float)chapterY, 0.0F);
            UIRenderHelper.streak(graphics, 0.0F, this.chapterArea.getX() - 10, this.chapterArea.getY() - 20, 20, 220);
            graphics.drawString(this.font, "More Topics to Ponder about", this.chapterArea.getX() - 5, this.chapterArea.getY() - 25, Theme.i(Theme.Key.TEXT_ACCENT_SLIGHT), false);
            ms.popPose();
        }
    }*/

    /*protected void renderWindowForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.disableDepthTest();
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0.0F, 0.0F, 200.0F);
        if (!this.hoveredItem.isEmpty()) {
            graphics.renderTooltip(this.font, this.hoveredItem, mouseX, mouseY);
        }

        ms.popPose();
        RenderSystem.enableDepthTest();
    }*/

    /*protected String getBreadcrumbTitle() {
        return this.tag.getTitle();
    }

    public ItemStack getHoveredTooltipItem() {
        return this.hoveredItem;
    }

    public boolean isEquivalentTo(NavigatableSimiScreen other) {
        if (other instanceof com.simibubi.create.foundation.ponder.ui.PonderTagScreen) {
            return this.tag == ((com.simibubi.create.foundation.ponder.ui.PonderTagScreen)other).tag;
        } else {
            return super.isEquivalentTo(other);
        }
    }*/

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    /*@Override
    public PonderTag getTag() {
        return this.tag;
    }*/

    /*public void removed() {
        super.removed();
        this.hoveredItem = ItemStack.EMPTY;
    }*/
}
