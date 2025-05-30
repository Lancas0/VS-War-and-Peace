package com.lancas.vswap.obsolete;

/*
import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.util.JomlUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestRenderer {

    public static Quaternionf getRotationQuaternion(Vector3d u, Vector3d v) {
        // 标准化输入向量
        Vector3d uNormalized = new Vector3d(u).normalize();
        Vector3d vNormalized = new Vector3d(v).normalize();

        // 计算点积和叉积
        double dot = uNormalized.dot(vNormalized);
        Vector3d cross = new Vector3d();
        uNormalized.cross(vNormalized, cross);

        // 处理向量相同的情况
        if (dot >= 1.0 - 1e-6) {
            return new Quaternionf(0, 0, 0, 1); // 单位四元数（无旋转）
        }

        // 处理向量相反的情况
        if (dot <= -1.0 + 1e-6) {
            // 选择任意垂直轴（例如，绕 Y 轴旋转 180 度）
            return new Quaternionf(0, 1, 0, 0).normalize();
        }

        // 计算半角三角函数
        double angle = Math.acos(dot);
        double halfAngle = angle * 0.5;
        double sinHalfAngle = Math.sin(halfAngle);

        // 归一化旋转轴
        cross.normalize();

        // 构造四元数
        return new Quaternionf(
            cross.x * sinHalfAngle,
            cross.y * sinHalfAngle,
            cross.z * sinHalfAngle,
            Math.cos(halfAngle)
        ).normalize();
    }
    /.*public static Quaternionf getAroundPlayerRotation(Vector3d lookVec) {
        lookVec = lookVec.normalize();
        double degZ = Math.acos(lookVec.dot(new Vector3d(1, 0, 0)));

        return new Quaternionf().rotateTo()

    }*./
    private static Quaterniond toQuaternoion(Vector3d euler) {
        double cy = Math.cos(euler.x * 0.5);
        double sy = Math.sin(euler.x * 0.5);
        double cp = Math.cos(euler.y * 0.5);
        double sp = Math.sin(euler.y * 0.5);
        double cr = Math.cos(euler.z * 0.5);
        double sr = Math.sin(euler.z * 0.5);

        Quaterniond q = new Quaterniond(
            cy * cp * sr - sy * sp * cr,
            sy * cp * sr + cy * sp * cr,
            sy * cp * cr - cy * sp * sr,
            cy * cp * cr + sy * sp * sr
        );

        return q;
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        //EzDebug.Log("subscribed event is triggered");

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        //if (stack.getItem() != AllItems.ShipSchemeItem.get() || !stack.hasTag()) return;

        IShipSchemeData shipSchemeData = ShipSchemeItem.getSchemeData(stack);
        if (shipSchemeData == null) return;

        //EzDebug.Log("ready to renderer");

        //todo 重复代码(in ShipSchemeItem)
        // 计算投影原点
        /.*HitResult hit = player.pick(SchemeTranslateAndRotateRegister.distance, 0.0f, false);
        // 投影原点（世界坐标）
        BlockPos previewOrigin;
        if (hit.getType() == HitResult.Type.BLOCK) {
            previewOrigin = ((BlockHitResult) hit).getBlockPos().relative(((BlockHitResult) hit).getDirection());
        } else {
            previewOrigin = BlockPos.containing(player.getEyePosition().add(player.getViewVector(1.0f).scale(SchemeTranslateAndRotateRegister.distance)));
        }*./
        //Vec3 previewOrigin = player.getEyePosition().add(player.getLookAngle().scale(SchemeTranslateAndRotateRegister.distance));

        // 渲染结构投影
        renderStructurePreview(
            event.getPoseStack(),
            mc.level,
            JomlUtil.d(player.getEyePosition()),
            JomlUtil.d(player.getLookAngle()),
            //getRotationQuaternion(new Vector3d(1, 0, 0), JomlUtil.d(player.getLookAngle())),
            new Quaternionf().rotateTo(new Vector3f(1, 0, 0), JomlUtil.f(player.getLookAngle())),
            shipSchemeData
        );
        //EzDebug.Log(player.getLookAngle().toString());
    }

    private static void renderStructurePreview(PoseStack poseStack, Level level, Vector3d playerPos, Vector3d offsetByPlayer, Quaternionf rotationAroundPlayer, IShipSchemeData shipSchemeData) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderType renderType = RenderType.translucent(); // 半透明渲染类型

        // 启用混合和深度调整
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        float scale = (float)shipSchemeData.getScale().x();  //todo 3d rotation

        // 应用相机偏移
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.pushPose();

        poseStack.translate(
            -camera.getPosition().x,
            -camera.getPosition().y,
            -camera.getPosition().z
        );
        //poseStack.rotateAround(rotationAroundPlayer, (float)playerPos.x, (float)playerPos.y, (float)playerPos.z);
        //poseStack.rotateAround(getRotationQuaternion(new Vector3d(1, 0, 0), ));

        Vector3d origin = playerPos.add(offsetByPlayer);


        shipSchemeData.forEach(level, (offset, state, entityTag) -> {
            //BlockPos worldPos = origin.offset(offset);
            Vector3d worldPos = origin.add(JomlUtil.d(offset).mul(scale));

            poseStack.pushPose();
            poseStack.translate(worldPos.x, worldPos.y, worldPos.z);
            //poseStack.rotateAround(rotationAroundPlayer, (float)worldPos.x,  (float)worldPos.y,  (float)worldPos.z);
            //poseStack.scale(scale, scale, scale);

            // 获取实际光照 todo bright light
            //int packedLight = LevelRenderer.getLightColor(level, worldPos);

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    state,
                    poseStack,
                    bufferSource,
                    //packedLight,
                    15,
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    renderType
            );

            poseStack.popPose();
            //EzDebug.Log("renderer " + state.getBlock().getName().getString() + " at " + worldPos.toShortString());
        });

        // 提交批次并恢复状态
        bufferSource.endBatch(renderType);
        poseStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}*/