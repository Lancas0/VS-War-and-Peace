package com.lancas.vswap.subproject.pondervs.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.element.BoxElement;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.element.AnimatedOverlayElement;
import com.simibubi.create.foundation.ponder.ui.PonderUI;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class NoPointLineTextElement extends AnimatedOverlayElement {
    protected Supplier<String> textGetter = () -> "(?) No text was provided";
    protected String bakedText;
    protected int y;
    protected Vec3 vec;
    protected boolean nearScene = false;
    protected int color;

    public NoPointLineTextElement() {
        this.color = PonderPalette.WHITE.getColor();
    }

    @Override
    protected void render(PonderScene scene, PonderUI screen, GuiGraphics graphics, float partialTicks, float fade) {
        if (this.bakedText == null) {
            this.bakedText = (String)this.textGetter.get();
        }

        if (!(fade < 0.0625F)) {
            PonderScene.SceneTransform transform = scene.getTransform();
            Vec2 sceneToScreen = this.vec != null ? transform.sceneToScreen(this.vec, partialTicks) : new Vec2((float)(screen.width / 2), (float)((screen.height - 200) / 2 + this.y - 8));
            boolean settled = transform.xRotation.settled() && transform.yRotation.settled();
            float pY = settled ? (float)((int)sceneToScreen.y) : sceneToScreen.y;
            float yDiff = ((float)screen.height / 2.0F - sceneToScreen.y - 10.0F) / 100.0F;
            float targetX = (float)screen.width * Mth.lerp(yDiff * yDiff, 0.75F, 0.625F);
            if (this.nearScene) {
                targetX = Math.min(targetX, sceneToScreen.x + 50.0F);
            }

            if (settled) {
                targetX = (float)((int)targetX);
            }

            int textWidth = (int)Math.min((float)screen.width - targetX, 180.0F);
            List<FormattedText> lines = screen.getFontRenderer().getSplitter().splitLines(this.bakedText, textWidth, Style.EMPTY);
            int boxWidth = 0;

            for(FormattedText line : lines) {
                boxWidth = Math.max(boxWidth, screen.getFontRenderer().width(line));
            }

            int boxHeight = screen.getFontRenderer().wordWrapHeight(this.bakedText, boxWidth);
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(0.0F, pY, 400.0F);
            (new BoxElement()).withBackground(Theme.c(Theme.Key.PONDER_BACKGROUND_FLAT)).gradientBorder(Theme.p(Theme.Key.TEXT_WINDOW_BORDER)).at(targetX - 10.0F, 3.0F, 100.0F).withBounds(boxWidth, boxHeight - 1).render(graphics);
            int brighterColor = Color.mixColors(this.color, -35, 0.5F);
            brighterColor = 16777215 & brighterColor | -16777216;
            /*if (this.vec != null) {
                ms.pushPose();
                ms.translate(sceneToScreen.x, 0.0F, 0.0F);
                double lineTarget = (double)((targetX - sceneToScreen.x) * fade);
                ms.scale((float)lineTarget, 1.0F, 1.0F);
                graphics.fillGradient(0, 0, 1, 1, -100, brighterColor, brighterColor);
                graphics.fillGradient(0, 1, 1, 2, -100, -11974327, -13027015);
                ms.popPose();
            }*/

            ms.translate(0.0F, 0.0F, 400.0F);

            for(int i = 0; i < lines.size(); ++i) {
                graphics.drawString(screen.getFontRenderer(), ((FormattedText)lines.get(i)).getString(), targetX - 10.0F, (float)(3 + 9 * i), (new Color(brighterColor)).scaleAlpha(fade).getRGB(), false);
            }

            ms.popPose();
        }
    }

    public int getColor() {
        return this.color;
    }

    public class Builder {
        private PonderScene scene;

        public Builder(PonderScene scene) {
            this.scene = scene;
        }

        public Builder colored(PonderPalette inColor) {
            color = inColor.getColor();
            return this;
        }

        public Builder pointAt(Vec3 inVec) {
            vec = inVec;
            return this;
        }

        public Builder independent(int inY) {
            y = inY;
            return this;
        }

        public Builder independent() {
            return this.independent(0);
        }

        public Builder text(String defaultText) {
            textGetter = this.scene.registerText(defaultText);
            return this;
        }

        public Builder sharedText(ResourceLocation key) {
            textGetter = () -> PonderLocalization.getShared(key);
            return this;
        }

        public Builder sharedText(String key) {
            return this.sharedText(new ResourceLocation(this.scene.getNamespace(), key));
        }

        public Builder placeNearTarget() {
            nearScene = true;
            return this;
        }

        public Builder attachKeyFrame() {
            this.scene.builder().addLazyKeyframe();
            return this;
        }
    }
}