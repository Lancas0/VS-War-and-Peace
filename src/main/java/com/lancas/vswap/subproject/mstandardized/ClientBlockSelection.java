package com.lancas.vswap.subproject.mstandardized;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.ResourcesUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Objects;

public class ClientBlockSelection {
    private static final Hashtable<String, String> selection = new Hashtable<>();

    public static void setSelection(String categoryName, Block block) { setSelection(categoryName, ResourcesUtil.blockId(block)); }
    public static void setSelection(String categoryName, String blockId) {
        if (!CategoryRegistry.getCategory(categoryName).isEmpty())
            selection.put(categoryName, blockId);
        else
            EzDebug.warn("category with name " + categoryName + " is empty");
    }
    public static @Nullable Block getSelectedBlockIfExist(String categoryName) {
        Category category = CategoryRegistry.getCategory(categoryName);
        if (category.isEmpty()) {
            EzDebug.warn("get empty category with name:" + categoryName);
            return null;
        }

        String selectedId = selection.get(categoryName);
        if (selectedId != null) {
            Block selected = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(selectedId));
            if (selected != null)
                return selected;
        }

        return null;
    }
    public static @Nullable Block getSelectedBlockOrMainBlock(String categoryName) {
        Category category = CategoryRegistry.getCategory(categoryName);
        if (category.isEmpty()) {
            EzDebug.warn("get empty category with name:" + categoryName);
            return null;
        }

        Block selected = getSelectedBlockIfExist(categoryName);
        if (selected != null)
            return selected;

        Block mainBlock = category.getMainBlock();
        if (mainBlock != null)
            selection.put(categoryName, Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(mainBlock)).toString());
        return mainBlock;
    }

    public static @Nullable Block getSelectBlockIfExistOf(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialStandardizedItem)) {
            EzDebug.warn("stack is not MaterialStandardizedItem");
            return null;
        }

        Category category = MaterialStandardizedItem.getCategory(stack);
        if (category.isEmpty()) {
            EzDebug.warn("category of stack is empty!");
            return null;
        }

        return getSelectedBlockIfExist(category.categoryName);
    }
    public static @Nullable Block getSelectBlockOrMainBlockOf(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialStandardizedItem)) {
            EzDebug.warn("stack is not MaterialStandardizedItem");
            return null;
        }

        Category category = MaterialStandardizedItem.getCategory(stack);
        if (category.isEmpty()) {
            EzDebug.warn("category of stack is empty!");
            return null;
        }

        return getSelectedBlockOrMainBlock(category.categoryName);
    }
}
