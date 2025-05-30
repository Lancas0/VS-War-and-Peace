package com.lancas.vswap.content.item.items.docker;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.subproject.mstandardized.renderer.MaterialStandardizedBakedModel;
import com.lancas.vswap.util.JomlUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.*;


public class DockerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static DockerItemRenderer INSTANCE = new DockerItemRenderer();
    //private static final Map<UUID, TextureCacheEntry> TEXTURE_CACHE = new HashMap<>();

    public DockerItemRenderer() {
        //super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
        //    Minecraft.getInstance().getEntityModels());
        super(null, null);
    }


    @Override
    public void renderByItem(ItemStack stack,
                             @NotNull ItemDisplayContext transformType,
                             @NotNull PoseStack poseStack,
                             @NotNull MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {

        //EzDebug.log("using dockerItemRenderer");

        if (!(stack.getItem() instanceof Docker docker)) {
            EzDebug.error("the item using DockerItemRender is not docker!");
            return;
        }

        // 基础物品渲染
        /*Minecraft.getInstance().getItemRenderer().render(
            stack,
            transformType,
            false,
            poseStack,
            buffer,
            packedLight,
            packedOverlay,
            Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0)
        );*/

        // 自定义结构渲染
        //IShipSchemeRandomReader shipDataReader = docker.getShipDataReader(stack);
        BakedModel model = Minecraft.getInstance()
            .getItemRenderer()
            .getModel(stack, null, null, 0);

        if (!(model instanceof DockerBakedModel dockerModel)) {
            EzDebug.warn("ms item get model is not MaterialStandardizedBakedModel, model type:" + model.getClass());
            return;
        }

        RRWChunkyShipSchemeData shipData = Docker.getShipData(stack);
        if (shipData == null) return;
        renderShipStructure(dockerModel, shipData, poseStack, buffer, packedLight);
    }

    //todo cache?
    private void renderShipStructure(DockerBakedModel model,
                                     RRWChunkyShipSchemeData shipData,
                                     PoseStack poseStack,
                                     MultiBufferSource buffer,
                                     int packedLight) {
        poseStack.pushPose();
        poseStack.mulPoseMatrix(model.getTransformer(shipData));
        var localAABB = shipData.getLocalAabbContainsCoordinate();
        int xSize = JomlUtil.lengthX(localAABB);
        int ySize = JomlUtil.lengthY(localAABB);
        int zSize = JomlUtil.lengthZ(localAABB);
        //int maxSize = Math.max(xSize, Math.max(ySize, zSize));
        //if (maxSize == 0) return;

        //poseStack.scale(1f / maxSize, 1f / maxSize, 1f / maxSize);
        Vector3d center = localAABB.center(new Vector3d());
        //poseStack.scale(0.25F, 0.25F, 0.25F); // 缩放结构
        //poseStack.translate(xSize / 2f + 0.5f, ySize / 2f + 0.5f, zSize / 2f + 0.5f);
        //-x 屏幕左, x屏幕右
        //-z 远离
        //Vector3ic corner = new Vector3i(localAABB.minX(), localAABB.minY(), localAABB.minZ());

        //poseStack.translate(-xSize / 2f, ySize / 2f, -zSize / 2f);


        //final BlockPos[] firstBp = {null};
        shipData.foreachBlockInLocal((localBp, state) -> {
            poseStack.pushPose();
            /*poseStack.translate(
                localBp.getX() / 16.0F,
                localBp.getY() / 16.0F,
                localBp.getZ() / 16.0F
            );*/
            //if (firstBp[0] == null)
            //    firstBp[0] = localBp;
            //Vector3i offset = JomlUtil.i(localBp).sub(corner);
            Vector3d offset = JomlUtil.dLowerCorner(localBp).sub(center);
            poseStack.translate(offset.x(), offset.y(), offset.z());

            // 渲染方块模型
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                state,
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
            );

            //EzDebug.schedule("docker_size", "xMax:" + localAABB.getMax(0) + ", xMin:" + localAABB.getMin(0) + ", len:" + (localAABB.getMax(0) - localAABB.getMin(0)));
            //EzDebug.schedule("docker", "size:" + StrUtil.poslike(xSize, ySize, zSize) + ", center:" + StrUtil.F2(center) + ", localBp" + localBp.toShortString() + ", offset:" + StrUtil.F2(offset) + ", state:" + StrUtil.getBlockName(state) + ", localAABB:" + localAABB);
            //EzDebug.log("rendering docker with block:" + StrUtil.getBlockName(state));

            poseStack.popPose();
        });

        poseStack.popPose();
    }
}
