package com.lancas.vswap.util;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.awt.*;
import java.util.function.Consumer;

public class ItemUtil {
    public static ItemStack giveItem(Player player, Item item, Consumer<ItemStack> modifer) {
        if (player == null)
            return null;

        ItemStack stack = new ItemStack(item); // 替换为你的目标物品

        if (modifer != null) {
            modifer.accept(stack);
        }
        player.addItem(stack);
        return stack;
    }

    public static int barColorByDurability(float current, float max) {
        float durabilityRatio = Math.max(0.0f, current / max);
        return Color.HSBtoRGB(durabilityRatio / 3.0f, 1.0f, 1.0f); // 绿色渐变
    }
    public static int barColorByDamage(float current, float max) {
        float durabilityRatio = Math.max(0.0f, 1.0f - current / max);
        return Color.HSBtoRGB(durabilityRatio / 3.0f, 1.0f, 1.0f); // 绿色渐变
    }

    public static int barWidthByDurability(float current, float max) {
        return Math.round(13.0f * current / max);
    }
    public static int barWidthByDamage(float current, float max) {
        return Math.round(13.0f * (1.0f - current / max));
    }


    public static boolean isInBackpack(int inventoryIx) {
        //0 is boot
        //1 is leggings
        //2 is chestplate
        //3 is helmet
        return switch (inventoryIx) {
            case 0, 1, 2, 3 ->
                false;

            default -> true;
        };
    }
    public static boolean isInArmorSlots(int inventoryIx) { return !isInBackpack(inventoryIx); }
    /*public static String getPureName(ItemStack itemStack) {
        return itemStack.getHoverName().getString().replaceAll("")
    }*/

    public static void dropNoRandom(Level level, ItemStack stack, Vector3dc dropPos, @Nullable Consumer<ItemEntity> withEntityDo) { dropNoRandom(level, stack, dropPos.x(), dropPos.y(), dropPos.z(), withEntityDo); }
    /*public static @Nullable ItemEntity dropNoRandom(Level level, ItemStack stack, double x, double y, double z) {
        if (stack.isEmpty())
            return null;

        ItemEntity dropEntity = new ItemEntity(level, x, y, z, stack);
        //float $$12 = 0.05F;
        //dropEntity.setDeltaMovement(p_18993_.random.triangle((double)0.0F, 0.11485000171139836), p_18993_.random.triangle(0.2, 0.11485000171139836), p_18993_.random.triangle((double)0.0F, 0.11485000171139836));
        level.addFreshEntity(dropEntity);
        return dropEntity;
    }*/
    public static void dropNoRandom(Level level, ItemStack stack, double x, double y, double z, @Nullable Consumer<ItemEntity> withEntityDo) {
        if (stack.isEmpty())
            return;

        ItemEntity dropEntity = new ItemEntity(level, x, y, z, stack);
        if (withEntityDo != null)
            withEntityDo.accept(dropEntity);
        //float $$12 = 0.05F;
        //dropEntity.setDeltaMovement(p_18993_.random.triangle((double)0.0F, 0.11485000171139836), p_18993_.random.triangle(0.2, 0.11485000171139836), p_18993_.random.triangle((double)0.0F, 0.11485000171139836));
        level.addFreshEntity(dropEntity);
        //return dropEntity;
    }
}
