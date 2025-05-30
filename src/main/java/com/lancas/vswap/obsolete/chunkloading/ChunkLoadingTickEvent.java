package com.lancas.vswap.obsolete.chunkloading;

/*
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkLoadingTickEvent {
    @SubscribeEvent
    public static void onServerTickEnd(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel level : event.getServer().getAllLevels()){
            ChunkManagement.getOrCreate(level).tick(level);
            //CustomChunkManager manager = ModDataManager.get(level);
            // 处理加载/卸载逻辑
        }
    }
}
*/