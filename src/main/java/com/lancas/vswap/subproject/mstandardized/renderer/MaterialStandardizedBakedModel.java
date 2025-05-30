package com.lancas.vswap.subproject.mstandardized.renderer;

import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.debug.EzDebug;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.List;
import java.util.Map;

public class MaterialStandardizedBakedModel implements BakedModel {
    public static double HALF_PI = 1.57079632;

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
    public static class ModEventBus {
        @SubscribeEvent
        public static void onModelBaked(ModelEvent.ModifyBakingResult event){
            // wrench item model
            Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
            ResourceLocation itemLocation = ForgeRegistries.ITEMS.getKey(WapItems.MATERIAL_STANDARDIZED.get());

            if (itemLocation == null) {
                throw new RuntimeException("Did not find Item in registry");
            }

            ModelResourceLocation moduleLocation = new ModelResourceLocation(itemLocation, "inventory");
            BakedModel existingModel = modelRegistry.get(moduleLocation);

            if (existingModel == null) {
                throw new RuntimeException("Did not find existingModel");
            } else if (existingModel instanceof MaterialStandardizedBakedModel) {
                throw new RuntimeException("Tried to warp module twice");
            } else {
                MaterialStandardizedBakedModel wrappedBakedModel = new MaterialStandardizedBakedModel(existingModel);
                event.getModels().put(moduleLocation, wrappedBakedModel);
            }
        }
    }


    private final BakedModel existingModel;
    private final Matrix4d baseTransformer = new Matrix4d();
    private final Matrix4d iconTransformer = new Matrix4d();
    public final Matrix4f getBaseTransformer() { return new Matrix4f(baseTransformer); }
    public final Matrix4f getIconTransformer() { return new Matrix4f(iconTransformer); }

    public BakedModel getOriginalModel() { return existingModel; }
    public MaterialStandardizedBakedModel(BakedModel existingModel) {
        this.existingModel = existingModel;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
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
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.existingModel.getParticleIcon();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return this.existingModel.getOverrides();
    }

    @Override
    public @NotNull BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        /*if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
            return this;
        }
        return this.existingModel.applyTransform(transformType,poseStack,applyLeftHandTransform);*/
        //poseStack.pushPose();
        //poseStack.setIdentity();

        //EzDebug.log("applying pose:" + poseStack.last().pose());
        //poseStack.popPose();
        //return this.applyTransform(transformType, poseStack, applyLeftHandTransform);
        /*if (transformType == ItemDisplayContext.GUI) {
            return this.existingModel.applyTransform(transformType,poseStack,applyLeftHandTransform);
        }
        return this;*/
        //this.getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        switch (transformType) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                iconTransformer.translationRotateScale(
                    new Vector3d(0.5, 0.705, 0.5),
                    new Quaterniond(new AxisAngle4d(-HALF_PI, 1, 0, 0)),
                    new Vector3d(0.28, 0.28, 0.001)
                );
                baseTransformer.translationRotateScale(
                    0.5, 0.5, 0.5,
                    0, 0, 0, 1,
                    0.4, 0.4, 0.4
                );
            }

            case GROUND -> {
                iconTransformer.translationRotateScale(
                    new Vector3d(0.5, 0.63, 0.5),
                    new Quaterniond(new AxisAngle4d(-HALF_PI, 1, 0, 0)),
                    new Vector3d(0.175, 0.175, 0.001)
                );
                baseTransformer.translationRotateScale(
                    0.5, 0.5, 0.5,
                    0, 0, 0, 1,
                    0.25, 0.25, 0.25
                );
            }

            case GUI -> {
                //double height = 0.6 + 0.5 * Math.sin(new Date().getTime() / 4000.0);
                //double angle = -HALF_PI * Math.sin(new Date().getTime() / 3000.0);
                //
                //double angle = -0.9;//-HALF_PI * Math.sin(new Date().getTime() / 6000.0);

                /*iconTransformer.translationRotateScale(
                    new Vector3d(0.5, 0.9, 0.4),
                    //new Quaterniond(new AxisAngle4d(HALF_PI, 0, 0, 0)),
                    //new Quaterniond(new AxisAngle4d(angle, 1, 0, 0)),
                    new Quaterniond(new AxisAngle4d(-1.09, 1, 0, 0)),
                    //new Vector3d(0.6, 0.6, 0.001)
                    new Vector3d(0.5, 0.5, 0.001)
                );*/
                iconTransformer.translationRotateScale(
                    new Vector3d(1 - 0.4, 0.5 - 0.2, 2),
                    new Quaterniond(),
                    new Vector3d(0.8, 0.8, 0.8)
                );
                baseTransformer.translationRotateScale(
                    new Vector3d(0.5, 0.5, 0.5),
                    new Quaterniond().rotateXYZ(Math.toRadians(24.4), Math.toRadians(-50.18), 0),
                    new Vector3d(0.65, 0.65, 0.65)
                );
            }

            case HEAD -> {
                iconTransformer.translationRotateScale(
                    0, 0, 0,
                    0, 0, 0, 1,
                    1, 1, 1
                );
                baseTransformer.translationRotateScale(
                    0, 0, 0,
                    0, 0, 0, 1,
                    1, 1, 1
                );
            }

            case NONE, FIXED -> {
                iconTransformer.translationRotateScale(
                    new Vector3d(0.5, 1.01, 0.5),
                    new Quaterniond(new AxisAngle4d(-HALF_PI, 1, 0, 0)),
                    new Vector3d(0.7, 0.7, 0.001)
                );
                baseTransformer.translationRotateScale(
                    0.5, 0.5, 0.5,
                    0, 0, 0, 1,
                    1, 1, 1
                );
            }

            default -> EzDebug.warn("unknown transformType:" + transformType);
        }

        //EzDebug.log("transformType:" + transformType);


        return this;
    }


}
