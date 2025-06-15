package com.lancas.vswap.subproject.pondervs.outline;

import com.lancas.vswap.mixins.create.ui.OutlineParamsAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.outliner.AABBOutline;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class AbstractOBBOutline extends AABBOutline {
    protected final OutlineParamsAccessor paramsAccessor;

    public AbstractOBBOutline(AABB bb) {
        super(bb);
        paramsAccessor = (OutlineParamsAccessor)super.getParams();
    }

    protected abstract Matrix4f getAabbToObb();

    @Override
    public void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, float pt) {
        this.params.loadColor(this.colorTemp);
        Vector4f color = this.colorTemp;
        int lightmap = paramsAccessor.getLightmap();
        boolean disableLineNormals = paramsAccessor.getDisableLineNormals();

        ms.pushPose();
        ms.translate(-camera.x, -camera.y, -camera.z);
        ms.mulPoseMatrix(getAabbToObb());
        renderBoxLocal(ms, buffer, camera, this.bb, color, lightmap, disableLineNormals);
        ms.popPose();
    }

    protected void renderBoxLocal(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, AABB box, Vector4f color, int lightmap, boolean disableLineNormals) {
        Vector3f minPos = this.minPosTemp1;
        Vector3f maxPos = this.maxPosTemp1;
        boolean cameraInside = box.contains(camera);
        boolean cull = !cameraInside && !paramsAccessor.getDisableCull();
        float inflate = cameraInside ? -0.0078125F : 0.0078125F;
        //box = box.move(camera.scale((double)-1.0F));
        minPos.set((float)box.minX - inflate, (float)box.minY - inflate, (float)box.minZ - inflate);
        maxPos.set((float)box.maxX + inflate, (float)box.maxY + inflate, (float)box.maxZ + inflate);
        this.renderBoxFaces(ms, buffer, cull, this.params.getHighlightedFace(), minPos, maxPos, color, lightmap);
        float lineWidth = this.params.getLineWidth();
        if (lineWidth != 0.0F) {
            VertexConsumer consumer = buffer.getBuffer(RenderTypes.getOutlineSolid());
            this.renderBoxEdges(ms, consumer, minPos, maxPos, lineWidth, color, lightmap, disableLineNormals);
        }
    }
}
