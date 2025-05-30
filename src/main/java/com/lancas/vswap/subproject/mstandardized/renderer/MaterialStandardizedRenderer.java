package com.lancas.vswap.subproject.mstandardized.renderer;

import com.lancas.vswap.debug.EzDebug;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import com.lancas.vswap.subproject.mstandardized.Category;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;

public class MaterialStandardizedRenderer extends BlockEntityWithoutLevelRenderer {
    public static MaterialStandardizedRenderer INSTANCE = new MaterialStandardizedRenderer();

    public MaterialStandardizedRenderer() { super(null, null); }

    @Override
    public void renderByItem(ItemStack stack,
                             @NotNull ItemDisplayContext transformType,
                             @NotNull PoseStack poseStack,
                             @NotNull MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {

        if (!(stack.getItem() instanceof MaterialStandardizedItem ms)) {
            //EzDebug.error("the item using DockerItemRender is not MaterialStandardizedItem!");
            return;
        }

        /*Category category = MaterialStandardizedItem.getCategory(stack);
        ItemStack iconStack;
        if (category.isEmpty()) {
            //EzDebug.warn("fail to get category when rendering");
            iconStack = Items.DIRT.getDefaultInstance();  //default dirt icon
        } else {
            iconStack = category.getIconItem().getDefaultInstance();
        }*/

        //ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        //BakedModel itemModel = itemRenderer.getModel(icon, null, null, 0);
        ItemStack iconStack = MaterialStandardizedItem.getIconItem(stack).getDefaultInstance();

        BakedModel model = Minecraft.getInstance()
            .getItemRenderer()
            .getModel(stack, null, null, 0);

        if (!(model instanceof MaterialStandardizedBakedModel msModel)) {
            EzDebug.warn("ms item get model is not MaterialStandardizedBakedModel, model type:" + model.getClass());
            return;
        }

        renderBase(stack, msModel, poseStack, transformType, buffer, packedLight, packedOverlay);
        renderIcon(msModel, iconStack, poseStack, buffer, packedLight, packedOverlay);

        /*poseStack.pushPose();
        poseStack.mulPoseMatrix((new Matrix4f()).scale(1, 1, 0.001f));

        Minecraft.getInstance().getItemRenderer()
            .render(icon, ItemDisplayContext.GUI, false, poseStack, buffer, packedLight, packedOverlay, itemModel);

        poseStack.popPose();*/
    }

    private void renderBase(ItemStack stack, MaterialStandardizedBakedModel model, PoseStack poseStack, ItemDisplayContext ctx, MultiBufferSource buffer, int light, int overlay) {
        //todo getItemModel and inst MSBakedModel, get existing model, to render base
        //todo avoid get itemRender mulit times
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();


        poseStack.pushPose();
        poseStack.mulPoseMatrix(model.getBaseTransformer());

        itemRenderer.render(
            stack,
            ctx,
            true,
            poseStack,
            buffer,
            light,
            overlay,
            model.getOriginalModel()
        );

        poseStack.popPose();
        /*poseStack.translate(0.5, 0.5, 0.5);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.STONE.defaultBlockState(), poseStack, buffer, light, overlay);
        poseStack.translate(-0.5, -0.5, -0.5);*/
    }
    private void renderIcon(MaterialStandardizedBakedModel model, ItemStack iconStack, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        poseStack.pushPose();
        try {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BakedModel iconModel = itemRenderer.getModel(iconStack, null, null, 0);

            // 坐标系变换
            /*poseStack.translate(0.5, 1, 0.5);
            poseStack.scale(0.5f, 0.5f, 0.001f);
            poseStack.mulPoseMatrix(new Matrix4f().rotate(new AxisAngle4f(1.57f, 0f, 1f, 0f)));
            poseStack.translate(0, 8, 0);*/
            poseStack.mulPoseMatrix(model.getIconTransformer());

            Minecraft.getInstance().getItemRenderer()
                .render(iconStack, ItemDisplayContext.GUI, false, poseStack, buffer, light, overlay, iconModel);
        } finally {
            poseStack.popPose();
        }
    }
}
