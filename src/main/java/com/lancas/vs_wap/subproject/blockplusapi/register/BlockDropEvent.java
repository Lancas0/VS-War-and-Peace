package com.lancas.vs_wap.subproject.blockplusapi.register;

/*
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockDropEvent {
    private static final Hashtable<ResourceLocation, List<LootPool>> additionDropPools = new Hashtable<>();
    private static final Hashtable<ResourceLocation, Boolean> cancelVanillaDrop = new Hashtable<>();

    public static void addDrops(ResourceLocation key, boolean cancelVanilla, @NotNull List<LootPool> addPools) {
        Boolean prevCancelVanilla = cancelVanillaDrop.computeIfAbsent(key, k -> false);
        cancelVanillaDrop.put(key, prevCancelVanilla || cancelVanilla);

        List<LootPool> drops = additionDropPools.computeIfAbsent(key, k -> new ArrayList<>());
        drops.addAll(addPools);
    }
    public static void addDrop(ResourceLocation key, boolean cancelVanilla, @NotNull LootPool addPool) {
        Boolean prevCancelVanilla = cancelVanillaDrop.computeIfAbsent(key, k -> false);
        cancelVanillaDrop.put(key, prevCancelVanilla || cancelVanilla);

        List<LootPool> drops = additionDropPools.computeIfAbsent(key, k -> new ArrayList<>());
        drops.add(addPool);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        EzDebug.warn("current event name:" + event.getName().toString());
        EzDebug.logs(additionDropPools.keySet(), r -> "has key:" + r.toString());
        Boolean cancelVanilla = cancelVanillaDrop.get(event.getName());
        if (cancelVanilla == null)  //no modify
            return;

        LootTable targetTable;
        if (cancelVanilla) {
            targetTable = LootTable.lootTable().build();
        } else {
            targetTable = event.getTable();
        }

        for (var pool : additionDropPools.get(event.getName())) {
            targetTable.addPool(pool);
        }

        event.setTable(targetTable);
    }

    private static void overrideVanillaLootTable(LootTableLoadEvent event) {

    }
}
*/