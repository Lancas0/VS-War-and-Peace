package com.lancas.vswap.mixins.sandbox;

import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class SandBoxLevelRenderMixin {
    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    private void renderShips(PoseStack poseStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        //AtomicInteger count = new AtomicInteger();
        //SandBoxClientWorld.INSTANCE.allRenderers().forEach(ship -> count.incrementAndGet());
        //EzDebug.log("render ship count:" +  count.get());

        SandBoxClientWorld.INSTANCE.allClientShips().forEach(s -> {
            RenderHelper.renderShip(s, poseStack, bufferSource, partialTicks);
            s.postRender();
        });
    }

    /*public void render(PoseStack poseStack, Vector3d centerPos, BlockState state) {
        BlockPos blockPos = JomlUtil.bpContaining(centerPos);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        //if (camera.fru)
        poseStack.pushPose();
        poseStack.translate(
            centerPos.x - camera.getPosition().x,
            centerPos.y - camera.getPosition().y,
            centerPos.z - camera.getPosition().z
        );
        poseStack.translate(-0.5, -0.5, -0.5); // 由于centerPos是方块中心，还是要对齐方块模型原点
        //now it's render related to world

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().tesselateBlock(
            Minecraft.getInstance().level,
            dispatcher.getBlockModel(state),
            state,
            blockPos,
            poseStack,
            bufferSource.getBuffer(/.*ItemBlockRenderTypes.getMovingBlockRenderType(state)*./RenderType.solid()),  //or transparent for trans block
            false, // 检查相邻方块遮挡
            RandomSource.create(),
            state.getSeed(blockPos),
            OverlayTexture.NO_OVERLAY
        );

        //提交并恢复矩阵  //todo should do?
        //Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        poseStack.popPose();
    }

    public void renderBlock(
        BlockState state,
        Vector3d precisePos, // 浮点坐标（如 x=1.5, y=2.3, z=3.7）
        //LevelAccessor level,
        PoseStack poseStack
        //VertexConsumer buffer,
        //boolean checkSides,
        //RandomSource random,
        //BakedModel model
    ) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        // 1. 分解坐标的整数和小数部分
        BlockPos blockPos = new BlockPos(
            (int) Math.floor(precisePos.x),
            (int) Math.floor(precisePos.y),
            (int) Math.floor(precisePos.z)
        );
        Vector3d renderOffset = precisePos.sub(JomlUtil.d(camera.getPosition()), new Vector3d());

        int packedLight = LevelRenderer.getLightColor(Minecraft.getInstance().level, blockPos);


        // 2. 平移矩阵
        poseStack.pushPose();
        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
        //vs says that:
        //poseStack.translate(offsetX, offsetY, offsetZ);
        //poseStack.mulPose(new Quaternionf(renderTransform.getShipToWorldRotation()));
        //poseStack.translate(-0.5d, -0.5d, -0.5d);

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().tesselateBlock(
            Minecraft.getInstance().level,
            dispatcher.getBlockModel(state),
            state,
            blockPos,
            poseStack,
            bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(state)),  //or transparent for trans block
            true, // 检查相邻方块遮挡
            RandomSource.create(),
            state.getSeed(blockPos),
            OverlayTexture.NO_OVERLAY
        );
        //提交并恢复矩阵  //todo should do?
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

        poseStack.popPose();

    }*/
}