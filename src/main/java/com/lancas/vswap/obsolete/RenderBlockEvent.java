package com.lancas.vswap.obsolete;

/*
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderBlockEvent {
    @SubscribeEvent
    public static void onBlockRenderer(EntityRenderersEvent.RegisterRenderers event) {
        // 将方块的渲染设置为空模型
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getBlockModelShaper().registerModelForState(
            ModBlocks.INVISIBLE_BLOCK.get().defaultBlockState(),
            new ResourceLocation("minecraft:block/air") // 使用空气的模型
        );
    }
}*/