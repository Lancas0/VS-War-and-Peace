package com.lancas.vswap.content.block.blocks.industry.shredder;

import com.jozufozu.flywheel.core.PartialModel;
import com.lancas.vswap.content.WapPartialModels;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.StrUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;

import java.lang.Math;

public class ShredderRenderer extends SafeBlockEntityRenderer<ShredderBe> {


    public ShredderRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    protected void renderSafe(ShredderBe be, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        BlockState blockState = be.getBlockState();
        PartialModel partial = WapPartialModels.SHREDDER_BLADE;

        if (!(blockState.getBlock() instanceof Shredder shredder)) {
            EzDebug.schedule("ShredderRenderer block", "ShredderRenderer get block is not Shredder but:" + StrUtil.getBlockName(blockState));
            return;
        }
        Direction.Axis rotateAxis = shredder.getRotationAxis(blockState);
        float time = AnimationTickHolder.getRenderTime();// / 20.0F;
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


        CachedBufferer.partial(partial, blockState)
            .translate(translation1)
            .rotateCentered(Direction.fromAxisAndDirection(rotateAxis, Direction.AxisDirection.POSITIVE), AngleHelper.rad(angle))
            .rotateCentered(rotation)
            .light(packedLight)
            .renderInto(poseStack, solid);
        CachedBufferer.partial(partial, blockState)
            .translate(translation2)
            .rotateCentered(Direction.fromAxisAndDirection(rotateAxis, Direction.AxisDirection.POSITIVE), AngleHelper.rad(-angle))
            .rotateCentered(rotation)
            .light(packedLight)
            .renderInto(poseStack, solid);
    }
}
