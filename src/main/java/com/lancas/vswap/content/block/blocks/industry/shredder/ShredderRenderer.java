package com.lancas.vswap.content.block.blocks.industry.shredder;

import com.jozufozu.flywheel.core.PartialModel;
import com.lancas.vswap.content.WapPartialModels;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.StrUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;

import java.lang.Math;

public class ShredderRenderer extends ShaftRenderer<ShredderBe> {

    public ShredderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ShredderBe be, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        super.renderSafe(be, v, poseStack, bufferSource, packedLight, overlay);

        BlockState blockState = be.getBlockState();
        PartialModel blade = WapPartialModels.SHREDDER_BLADE;

        if (!(blockState.getBlock() instanceof Shredder shredder)) {
            EzDebug.schedule("ShredderRenderer block", "ShredderRenderer get block is not Shredder but:" + StrUtil.getBlockName(blockState));
            return;
        }

        Direction.Axis rotateAxis = shredder.getRotationAxis(blockState);
        float time = AnimationTickHolder.getRenderTime(be.getLevel()) / 5.0F;
        float angle = time * Math.abs(be.getSpeed()) % 360.0F;

        VertexConsumer solid = bufferSource.getBuffer(RenderType.cutout());

        Vector3f translation1 = new Vector3f();
        Vector3f translation2 = new Vector3f();
        Shredder.RenderFriend.getCrusherOffset(blockState, translation1, translation2);
        Quaternionf rotation = Shredder.RenderFriend.getCrusherRotation(blockState, new Quaternionf());
        /*Vector3f addTran = switch (rotateAxis) {
            case X -> new Vector3f(0, 0, MOVE_SIDE);
            case Y -> {
                EzDebug.schedule("PulverizerRenderer rotateAxis", "rotateAxis can't be Y");
                yield new Vector3f();
            }
            case Z -> new Vector3f(MOVE_SIDE, 0, 0);
        };
        translation1.add(addTran);
        translation2.sub(addTran);*/


        CachedBufferer.partial(blade, blockState)
            .translate(translation1)
            .rotateCentered(Direction.fromAxisAndDirection(rotateAxis, Direction.AxisDirection.POSITIVE), AngleHelper.rad(angle))
            .rotateCentered(rotation)
            .light(packedLight)
            .renderInto(poseStack, solid);
        CachedBufferer.partial(blade, blockState)
            .translate(translation2)
            .rotateCentered(Direction.fromAxisAndDirection(rotateAxis, Direction.AxisDirection.POSITIVE), AngleHelper.rad(-angle))
            .rotateCentered(rotation)
            .light(packedLight)
            .renderInto(poseStack, solid);
    }

    /*protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
    protected void renderShaft(SawBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        KineticBlockEntityRenderer.renderRotatingBuffer(be, this.getRotatedModel(be), ms, buffer.getBuffer(RenderType.solid()), light);
        AllBlockEntityTypes.ENCASED_COGWHEEL
    }*/
}
