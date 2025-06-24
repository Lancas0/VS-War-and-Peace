package com.lancas.vswap.content.item.items.docker;

import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DockerBakedModel implements BakedModel {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
    public static class ModEventBus {
        @SubscribeEvent
        public static void onModelBaked(ModelEvent.ModifyBakingResult event){
            // wrench item model
            Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
            ModelResourceLocation location1 = new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(WapItems.DOCKER.get()), "inventory");
            //ModelResourceLocation location2 = new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(WapItems.DOCKER.get()), "inventory");

            BakedModel existingModel1 = modelRegistry.get(location1);
            //BakedModel existingModel2 = modelRegistry.get(location2);
            if (existingModel1 == null/* || existingModel2 == null*/) {
                throw new RuntimeException("Did not find Obsidian Hidden in registry");
            } else if (existingModel1 instanceof DockerBakedModel/* || existingModel2 instanceof DockerBakedModel*/) {
                throw new RuntimeException("Tried to replaceObsidian Hidden twice");
            } else {
                DockerBakedModel wrappedBakedModel1 = new DockerBakedModel(existingModel1);
                event.getModels().put(location1, wrappedBakedModel1);

                //DockerBakedModel wrappedBakedModel2 = new DockerBakedModel(existingModel2);
                //event.getModels().put(location2, wrappedBakedModel2);
            }
        }
    }

    private final BakedModel existingModel;
    private Function<RRWChunkyShipSchemeData, Matrix4f> transformer;
    public Matrix4f getTransformer(RRWChunkyShipSchemeData schemeData) { return transformer.apply(schemeData); }

    public DockerBakedModel(BakedModel existingModel) {
        this.existingModel = existingModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
        return this.existingModel.getQuads(pState, pDirection, pRandom);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        throw new AssertionError("IForgeBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.existingModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.existingModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.existingModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }



    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.existingModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.existingModel.getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        /*if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
            return this;
        }
        return this.existingModel.applyTransform(transformType,poseStack,applyLeftHandTransform);*/

        //poseStack.pushPose();
        //poseStack.setIdentity();

        //EzDebug.log("applying pose:" + poseStack.last().pose());
        //poseStack.popPose();
        //return this.applyTransform(transformType, poseStack, applyLeftHandTransform);
        /*transformer = switch (transformType) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND -> (data) -> {
                var localAABB = data.getLocalAabbContainsCoordinate();
                int xSize = JomlUtil.lengthX(localAABB);
                int ySize = JomlUtil.lengthY(localAABB);
                int zSize = JomlUtil.lengthZ(localAABB);

                poseStack.(-xSize / 2f, ySize / 2f, -zSize / 2f);
            }
            case GROUND -> (data) -> {
                var localAABB = data.getLocalAabbContainsCoordinate();
                //int xSize = JomlUtil.lengthX(localAABB);
                int ySize = JomlUtil.lengthY(localAABB);
                //int zSize = JomlUtil.lengthZ(localAABB);

                return new Matrix4d().translate(/*-xSize / 2f*./0, ySize / 2f, /*-zSize / 2f*./0);
            }
        }*/
        if (transformType == ItemDisplayContext.GUI) {
            return this.existingModel.applyTransform(transformType,poseStack,applyLeftHandTransform);
        }

        transformer = switch (transformType) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND -> (data) -> {
                var localAABB = data.getLocalAabbContainsShape();
                int xSize = JomlUtil.lengthX(localAABB);
                int ySize = JomlUtil.lengthY(localAABB);
                int zSize = JomlUtil.lengthZ(localAABB);

                float scale = MathUtil.min(1f / xSize, 1f / ySize, 1f / zSize);

                return new Matrix4f().translationRotateScale(
                    xSize / 2f * scale, ySize / 2f * scale, zSize / 2f * scale,
                    0, 0, 0, 1,
                    scale, scale, scale
                );
                //return new Matrix4f().translate(-xSize / 2f, ySize / 2f, -zSize / 2f);
            };
            case GROUND, NONE, FIXED -> (data) -> {  //todo apply ship scale
                var localAABB = data.getLocalAabbContainsShape();
                //int xSize = JomlUtil.lengthX(localAABB);
                int ySize = JomlUtil.lengthY(localAABB);
                //int zSize = JomlUtil.lengthZ(localAABB);

                return new Matrix4f().translate(/*-xSize / 2f*/0, ySize / 2f, /*-zSize / 2f*/0);
            };
            case GUI -> {
                EzDebug.warn("Dock Model should never have transformer type GUI for now");
                yield (data) -> new Matrix4f();
            }
            case HEAD -> (data) -> new Matrix4f();
        };



        return this;
    }
}