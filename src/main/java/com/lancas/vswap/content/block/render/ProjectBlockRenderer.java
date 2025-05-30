package com.lancas.vswap.content.block.render;

import com.lancas.vswap.content.block.blockentity.ProjectBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectBlockRenderer implements BlockEntityRenderer<ProjectBlockEntity> {
    @Override
    public void render(ProjectBlockEntity be, float v, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // 获取目标方块状态
        BlockState representState = be.getRepresentBlock();

        // 绘制基础方块
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            representState, poseStack, buffer, packedLight, packedOverlay
        );

        // 添加投影特效
        VertexConsumer overlay = buffer.getBuffer(RenderType.translucent());
        //ModelUtil.renderTransparentOverlay(poseStack, overlay, 0x40FF0000);
    }
}
