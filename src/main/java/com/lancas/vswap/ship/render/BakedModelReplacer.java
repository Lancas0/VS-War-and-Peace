package com.lancas.vswap.ship.render;

/*
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BakedModelReplacer {
    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        ModelResourceLocation location = new ModelResourceLocation(ItemRegistry.obsidianWrench.get().getRegistryName(), "inventory");
        IBakedModel existingModel = modelRegistry.get(location);
        if (existingModel == null) {
            throw new RuntimeException("Did not find Obsidian Hidden in registry");
        } else if (existingModel instanceof ObsidianWrenchBakedModel) {
            throw new RuntimeException("Tried to replaceObsidian Hidden twice");
        } else {
            ObsidianWrenchBakedModel obsidianWrenchBakedModel = new ObsidianWrenchBakedModel(existingModel);
            event.getModelRegistry().put(location, obsidianWrenchBakedModel);
        }
    }
}*/