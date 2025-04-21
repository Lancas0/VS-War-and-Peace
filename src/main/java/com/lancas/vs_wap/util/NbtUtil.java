package com.lancas.vs_wap.util;

import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NbtUtil {
    public static CompoundTag putBlockPos(CompoundTag tag, String key, BlockPos bp) {
        tag.putIntArray(key, new int[] { bp.getX(), bp.getY(), bp.getZ() });
        return tag;
    }
    @Nullable
    public static BlockPos getBlockPos(CompoundTag tag, String key) {
        int[] xyz = tag.getIntArray(key);
        if (xyz.length != 3)
            return null;

        return new BlockPos(xyz[0], xyz[1], xyz[2]);
    }

    public static CompoundTag putBlockPos(ItemStack stack, String key, BlockPos bp) {
        return putBlockPos(stack.getOrCreateTag(), key, bp);
    }
    @Nullable
    public static BlockPos getBlockPos(ItemStack stack, String key) { return getBlockPos(stack.getOrCreateTag(), key); }

    public static CompoundTag putEnum(CompoundTag tag, String key, Enum<?> value) {
        tag.putString(key, value.name());
        EzDebug.log("put enum name:" + value.name());
        return tag;
    }
    public static <T extends Enum<T>> T getEnum(CompoundTag tag, String key, Class<T> enumType) {
        String enumName = tag.getString(key);
        if (enumName.isEmpty())
            return null;

        EzDebug.log("tryget enum:" + enumName);

        return Enum.valueOf(enumType, enumName);
    }

    public static CompoundTag putEnum(ItemStack stack, String key, Enum<?> value) {
        return putEnum(stack.getOrCreateTag(), key, value);
    }
    public static <T extends Enum<T>> T getEnum(ItemStack stack, String key, Class<T> enumType) {
        return getEnum(stack.getOrCreateTag(), key, enumType);
    }


}
