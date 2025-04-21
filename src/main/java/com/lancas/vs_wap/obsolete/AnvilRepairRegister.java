package com.lancas.vs_wap.obsolete;

/*
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilRepairRegister {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();  // 左侧输入物品（基础物品）
        ItemStack right = event.getRight(); // 右侧输入物品（材料）

        if (left.getItem() instanceof ShipSchemeItem && right.getItem() instanceof ShipSchemeRepairItem) {
            ItemStack output = left.copy();
            ShipSchemeItem.setFlawless(output);

            // 设置输出
            event.setOutput(output);
            event.setCost(1); //TODO 不消耗经验
            event.setMaterialCost(1); //TODO 不消耗右侧材料(现在是先-后+,设置为1和0没区别)
        }
    }


    @SubscribeEvent
    public static void onAnvilRepaired(AnvilRepairEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        right.grow(1);
        ShipSchemeRepairItem.damageByRepair(right, left);

        //TODO 决定是0还是其他值
        event.setBreakChance(0);

        event.setResult(Event.Result.ALLOW);
    }
}*/