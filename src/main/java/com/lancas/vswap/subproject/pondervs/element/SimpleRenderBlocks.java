package com.lancas.vswap.subproject.pondervs.element;

import com.jozufozu.flywheel.core.model.ModelUtil;
import com.jozufozu.flywheel.core.model.ShadeSeparatedBufferedData;
import com.jozufozu.flywheel.core.model.ShadeSeparatingVertexConsumer;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.ponder.element.AnimatedSceneElement;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.HashMap;
import java.util.Map;

import static com.simibubi.create.foundation.ponder.element.WorldSectionElement.DOC_WORLD_SECTION;

public class SimpleRenderBlocks extends AnimatedSceneElement {

    private final HashMap<BlockPos, BlockState> blocks = new HashMap<>();
    private final Matrix4f transformer = new Matrix4f();
    public SimpleRenderBlocks(Map<BlockPos, BlockState> inBlocks) {
        blocks.putAll(inBlocks);
    }

    public SimpleRenderBlocks withTransformer(Matrix4fc inTrans) {
        transformer.set(inTrans);
        return this;
    }

    public SimpleRenderBlocks copy() {
        return new SimpleRenderBlocks(blocks).withTransformer(transformer);
    }

    @Override
    protected void renderLayer(PonderWorld world, MultiBufferSource buffer, RenderType type, PoseStack ms, float fade, float pt) {

        /*var dispatcher = Minecraft.getInstance().getBlockRenderer();
        var renderer = dispatcher.getModelRenderer();
        BlockPos.betweenClosed(new BlockPos(0, 0, 0), new BlockPos(10, 0, 10)).forEach(pos -> {
            BlockState state = Blocks.WATER.defaultBlockState();
            FluidState fluidState = state.getFluidState();
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            if (state.getRenderShape() == RenderShape.MODEL) {
                BakedModel model = dispatcher.getBlockModel(state);
                BlockEntity blockEntity = world.getBlockEntity(pos);
                ModelData modelData = model.getModelData(world, pos, state, blockEntity != null ? blockEntity.getModelData() : ModelData.EMPTY);
                long seed = state.getSeed(pos);
                random.setSeed(seed);
                if (model.getRenderTypes(state, random, modelData).contains(type)) {
                    renderer.tesselateBlock(world, model, state, pos, poseStack, shadeSeparatingWrapper, true, random, seed, OverlayTexture.NO_OVERLAY, modelData, type);
                }
            }
            if (!fluidState.isEmpty() && ItemBlockRenderTypes.getRenderLayer(fluidState) == type) {
                dispatcher.renderLiquid(pos, world, shadedBuilder, state, fluidState);
            }
            poseStack.popPose();
        });*/
        SuperByteBufferCache bufferCache = CreateClient.BUFFER_CACHE;
        int code = hashCode() ^ world.hashCode();
        Pair<Integer, Integer> key = Pair.of(Integer.valueOf(code), Integer.valueOf(RenderType.chunkBufferLayers().indexOf(type)));

        SuperByteBuffer contraptionBuffer = bufferCache.get(DOC_WORLD_SECTION, key, () -> {
            return buildStructureBuffer(world, type);
        });
        if (contraptionBuffer.isEmpty()) {
            return;
        }

        ms.pushPose();
        ms.mulPoseMatrix(transformer);
        int light = lightCoordsFromFade(fade);
        contraptionBuffer.light(light).renderInto(ms, buffer.getBuffer(type));
        ms.popPose();
        //ms.translate(10, 0, 10);


    }

    @Override
    protected void renderFirst(PonderWorld world, MultiBufferSource buffer, PoseStack ms, float fade, float pt) {

    }

    @Override
    protected void renderLast(PonderWorld world, MultiBufferSource buffer, PoseStack ms, float fade, float pt) {

    }



    public final PoseStack poseStack = new PoseStack();
    public final RandomSource random = RandomSource.createNewThreadLocalInstance();
    public final ShadeSeparatingVertexConsumer shadeSeparatingWrapper = new ShadeSeparatingVertexConsumer();
    public final BufferBuilder shadedBuilder = new BufferBuilder(512);
    public final BufferBuilder unshadedBuilder = new BufferBuilder(512);



    private SuperByteBuffer buildStructureBuffer(PonderWorld world, RenderType layer) {
        BlockRenderDispatcher dispatcher = ModelUtil.VANILLA_RENDERER;
        ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        shadedBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        unshadedBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        shadeSeparatingWrapper.prepare(shadedBuilder, unshadedBuilder);
        //world.setMask(Selection.of(new BoundingBox(0, 0, 0, 40 , 0, 40)));
        ModelBlockRenderer.enableCaching();

        blocks.forEach((pos, state) -> {
            FluidState fluidState = state.getFluidState();
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            if (state.getRenderShape() == RenderShape.MODEL) {
                BakedModel model = dispatcher.getBlockModel(state);
                //BlockEntity blockEntity = world.getBlockEntity(pos);
                ModelData modelData = model.getModelData(world, pos, state, /*blockEntity != null ? blockEntity.getModelData() : */ModelData.EMPTY);
                long seed = state.getSeed(pos);
                random.setSeed(seed);
                if (model.getRenderTypes(state, random, modelData).contains(layer)) {
                    renderer.tesselateBlock(world, model, state, pos, poseStack, shadeSeparatingWrapper, true, random, seed, OverlayTexture.NO_OVERLAY, modelData, layer);
                }
            }
            if (!fluidState.isEmpty() && ItemBlockRenderTypes.getRenderLayer(fluidState) == layer) {
                dispatcher.renderLiquid(pos, world, shadedBuilder, state, fluidState);
            }
            poseStack.popPose();
        });

        ModelBlockRenderer.clearCache();
        //world.clearMask();
        shadeSeparatingWrapper.clear();
        ShadeSeparatedBufferedData bufferedData = ModelUtil.endAndCombine(shadedBuilder, unshadedBuilder);
        SuperByteBuffer sbb = new SuperByteBuffer(bufferedData);
        bufferedData.release();
        return sbb;
    }
}
