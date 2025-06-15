package com.lancas.vswap.content.block.blocks.industry.projector;

import com.lancas.vswap.content.WapPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectorLenRenderer extends SafeBlockEntityRenderer<ProjectorLenBe> {
    public ProjectorLenRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(ProjectorLenBe be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        double rad = be.scale.getValue(partialTick) * ProjectorLenBe.SCALE_TO_RAD;

        BlockState state = be.getBlockState();
        VertexConsumer solid = bufferSource.getBuffer(RenderType.cutout());

        SuperByteBuffer buffer = CachedBufferer.partial(WapPartialModels.PROJECTOR_LEN_TOP, state);

        buffer.rotateCentered(Direction.UP, (float)rad)
            .light(packedLight)
            .renderInto(poseStack, solid);
    }

}