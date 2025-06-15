package com.lancas.vswap.content.block.render;

/*
import com.lancas.vswap.util.JomlUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.minecart.CouplingRenderer;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Mod.EventBusSubscriber
public class TestLineRenderer {
    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        PoseStack ms = event.getPoseStack();
        SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
        VertexConsumer vConsumer = buffer.getLateBuffer(RenderType.LINES);
        float partialTicks = AnimationTickHolder.getPartialTicks();
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        Vector3f p = JomlUtil.f(player.getPosition(1f));

        /.*int lightmap = 0;
        Vector3f normal = new Vector3f(0, 1, 0);
        vConsumer.vertex(p.x, p.y, p.z).color(0, 0, 0, 1).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(normal.x, normal.y, normal.z).endVertex();
        vConsumer.vertex(p.x + 1, p.y, p.z).color(0, 0, 0, 1).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(normal.x, normal.y, normal.z).endVertex();
        vConsumer.vertex(p.x + 1, p.y, p.z + 1).color(0, 0, 0, 1).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(normal.x, normal.y, normal.z).endVertex();
        vConsumer.vertex(p.x + 1, p.y, p.z + 1).color(0, 0, 0, 1).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(normal.x, normal.y, normal.z).endVertex();
        vConsumer.vertex(p.x, p.y, p.z + 1).color(0, 0, 0, 1).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(normal.x, normal.y, normal.z).endVertex();
        vConsumer.vertex(p.x, p.y, p.z).color(0, 0, 0, 1).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(normal.x, normal.y, normal.z).endVertex();
        *./
        Vector3f start = new Vector3f(p.x + 1, p.y, p.z + 1);
        Vector3f end = new Vector3f(p.x + 1, p.y, p.z + 2);


        //Vector3f diff = this.diffPosTemp;
        Vector3f diff = new Vector3f().set((float) (end.x - start.x), (float) (end.y - start.y), (float) (end.z - start.z));
        float length = Mth.sqrt((diff.x() * diff.x()) + (diff.y() * diff.y()) + (diff.z() * diff.z()));
        float hAngle = AngleHelper.deg(Mth.atan2(diff.x(), diff.z()));
        float hDistance = Mth.sqrt((diff.x() * diff.x()) + (diff.z() * diff.z()));
        float vAngle = AngleHelper.deg(Mth.atan2(hDistance, diff.y())) - 90.0f;
        ms.pushPose();
        ms.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        ((TransformStack) ((TransformStack) TransformStack.cast(poseStack).translate(start.x - camera.x, start.y - camera.y, start.z - camera.z)).rotateY(hAngle)).rotateX(vAngle);
        bufferCuboidLine(poseStack.last(), consumer, new Vector3f(), Direction.SOUTH, length, width, color, lightmap, disableNormals);
        Vector3f minPos = this.minPosTemp;
        Vector3f maxPos = this.maxPosTemp;
        float halfWidth = width / 2.0f;
        minPos.set(origin.x() - halfWidth, origin.y() - halfWidth, origin.z() - halfWidth);
        maxPos.set(origin.x() + halfWidth, origin.y() + halfWidth, origin.z() + halfWidth);
        switch (AnonymousClass1.$SwitchMap$net$minecraft$core$Direction[direction.ordinal()]) {
            case 1:
                minPos.add(0.0f, -length, 0.0f);
                break;
            case 2:
                maxPos.add(0.0f, length, 0.0f);
                break;
            case 3:
                minPos.add(0.0f, 0.0f, -length);
                break;
            case 4:
                maxPos.add(0.0f, 0.0f, length);
                break;
            case 5:
                minPos.add(-length, 0.0f, 0.0f);
                break;
            case 6:
                maxPos.add(length, 0.0f, 0.0f);
                break;
        }
        bufferCuboid(pose, consumer, minPos, maxPos, color, lightmap, disableNormals);


        Vector4f posTransformTemp = this.posTransformTemp;
        Vector3f normalTransformTemp = this.normalTransformTemp;
        float minX = minPos.x();
        float minY = minPos.y();
        float minZ = minPos.z();
        float maxX = maxPos.x();
        float maxY = maxPos.y();
        float maxZ = maxPos.z();
        Matrix4f posMatrix = pose.pose();
        posTransformTemp.set(minX, minY, maxZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x0 = posTransformTemp.x();
        double y0 = posTransformTemp.y();
        double z0 = posTransformTemp.z();
        posTransformTemp.set(minX, minY, minZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x1 = posTransformTemp.x();
        double y1 = posTransformTemp.y();
        double z1 = posTransformTemp.z();
        posTransformTemp.set(maxX, minY, minZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x2 = posTransformTemp.x();
        double y2 = posTransformTemp.y();
        double z2 = posTransformTemp.z();
        posTransformTemp.set(maxX, minY, maxZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x3 = posTransformTemp.x();
        double y3 = posTransformTemp.y();
        double z3 = posTransformTemp.z();
        posTransformTemp.set(minX, maxY, minZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x4 = posTransformTemp.x();
        double y4 = posTransformTemp.y();
        double z4 = posTransformTemp.z();
        posTransformTemp.set(minX, maxY, maxZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x5 = posTransformTemp.x();
        double y5 = posTransformTemp.y();
        double z5 = posTransformTemp.z();
        posTransformTemp.set(maxX, maxY, maxZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x6 = posTransformTemp.x();
        double y6 = posTransformTemp.y();
        double z6 = posTransformTemp.z();
        posTransformTemp.set(maxX, maxY, minZ, 1.0f);
        posTransformTemp.mul(posMatrix);
        double x7 = posTransformTemp.x();
        double y7 = posTransformTemp.y();
        double z7 = posTransformTemp.z();
        float r = color.x();
        float g = color.y();
        float b = color.z();
        float a = color.w();
        Matrix3f normalMatrix = pose.normal();
        if (disableNormals) {
            normalTransformTemp.set(0.0f, 1.0f, 0.0f);
        } else {
            normalTransformTemp.set(0.0f, -1.0f, 0.0f);
        }
        normalTransformTemp.mul(normalMatrix);
        float nx0 = normalTransformTemp.x();
        float ny0 = normalTransformTemp.y();
        float nz0 = normalTransformTemp.z();
        consumer.vertex(x0, y0, z0).color(r, g, b, a).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx0, ny0, nz0).endVertex();
        consumer.vertex(x1, y1, z1).color(r, g, b, a).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx0, ny0, nz0).endVertex();
        consumer.vertex(x2, y2, z2).color(r, g, b, a).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx0, ny0, nz0).endVertex();
        consumer.vertex(x3, y3, z3).color(r, g, b, a).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx0, ny0, nz0).endVertex();
        normalTransformTemp.set(0.0f, 1.0f, 0.0f);
        normalTransformTemp.mul(normalMatrix);
        float nx1 = normalTransformTemp.x();
        float ny1 = normalTransformTemp.y();
        float nz1 = normalTransformTemp.z();
        consumer.vertex(x4, y4, z4).color(r, g, b, a).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx1, ny1, nz1).endVertex();
        consumer.vertex(x5, y5, z5).color(r, g, b, a).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx1, ny1, nz1).endVertex();
        consumer.vertex(x6, y6, z6).color(r, g, b, a).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx1, ny1, nz1).endVertex();
        consumer.vertex(x7, y7, z7).color(r, g, b, a).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx1, ny1, nz1).endVertex();
        if (disableNormals) {
            normalTransformTemp.set(0.0f, 1.0f, 0.0f);
        } else {
            normalTransformTemp.set(0.0f, 0.0f, -1.0f);
        }
        normalTransformTemp.mul(normalMatrix);
        float nx2 = normalTransformTemp.x();
        float ny2 = normalTransformTemp.y();
        float nz2 = normalTransformTemp.z();
        consumer.vertex(x7, y7, z7).color(r, g, b, a).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx2, ny2, nz2).endVertex();
        consumer.vertex(x2, y2, z2).color(r, g, b, a).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx2, ny2, nz2).endVertex();
        consumer.vertex(x1, y1, z1).color(r, g, b, a).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx2, ny2, nz2).endVertex();
        consumer.vertex(x4, y4, z4).color(r, g, b, a).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx2, ny2, nz2).endVertex();
        if (disableNormals) {
            normalTransformTemp.set(0.0f, 1.0f, 0.0f);
        } else {
            normalTransformTemp.set(0.0f, 0.0f, 1.0f);
        }
        normalTransformTemp.mul(normalMatrix);
        float nx3 = normalTransformTemp.x();
        float ny3 = normalTransformTemp.y();
        float nz3 = normalTransformTemp.z();
        consumer.vertex(x5, y5, z5).color(r, g, b, a).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx3, ny3, nz3).endVertex();
        consumer.vertex(x0, y0, z0).color(r, g, b, a).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx3, ny3, nz3).endVertex();
        consumer.vertex(x3, y3, z3).color(r, g, b, a).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx3, ny3, nz3).endVertex();
        consumer.vertex(x6, y6, z6).color(r, g, b, a).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx3, ny3, nz3).endVertex();
        if (disableNormals) {
            normalTransformTemp.set(0.0f, 1.0f, 0.0f);
        } else {
            normalTransformTemp.set(-1.0f, 0.0f, 0.0f);
        }
        normalTransformTemp.mul(normalMatrix);
        float nx4 = normalTransformTemp.x();
        float ny4 = normalTransformTemp.y();
        float nz4 = normalTransformTemp.z();
        consumer.vertex(x4, y4, z4).color(r, g, b, a).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx4, ny4, nz4).endVertex();
        consumer.vertex(x1, y1, z1).color(r, g, b, a).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx4, ny4, nz4).endVertex();
        consumer.vertex(x0, y0, z0).color(r, g, b, a).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx4, ny4, nz4).endVertex();
        consumer.vertex(x5, y5, z5).color(r, g, b, a).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx4, ny4, nz4).endVertex();
        if (disableNormals) {
            normalTransformTemp.set(0.0f, 1.0f, 0.0f);
        } else {
            normalTransformTemp.set(1.0f, 0.0f, 0.0f);
        }
        normalTransformTemp.mul(normalMatrix);
        float nx5 = normalTransformTemp.x();
        float ny5 = normalTransformTemp.y();
        float nz5 = normalTransformTemp.z();
        consumer.vertex(x6, y6, z6).color(r, g, b, a).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx5, ny5, nz5).endVertex();
        consumer.vertex(x3, y3, z3).color(r, g, b, a).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx5, ny5, nz5).endVertex();
        consumer.vertex(x2, y2, z2).color(r, g, b, a).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx5, ny5, nz5).endVertex();
        consumer.vertex(x7, y7, z7).color(r, g, b, a).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(nx5, ny5, nz5).endVertex();




        poseStack.popPose();



        buffer.draw();
        //RenderSystem.enableCull();
        ms.popPose();
        //ContraptionPlayerPassengerRotation.frame();
    }

}
*/