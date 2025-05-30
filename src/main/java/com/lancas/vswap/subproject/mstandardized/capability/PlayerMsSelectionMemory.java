package com.lancas.vswap.subproject.mstandardized.capability;

/*
import com.lancas.vswap.ModMain;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.subproject.mstandardized.Category;
import com.lancas.vswap.subproject.mstandardized.CategoryRegistry;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ResourcesUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Objects;

import static com.lancas.vswap.ModMain.MODID;


public class PlayerMsSelectionMemory {
    public static PlayerMsSelectionMemory getMemory(Player player) {
        return player.getCapability(PlayerMsSelectionMemoryProvider.CAPABILITY)
            .orElseThrow(() -> new RuntimeException("fail to get player ms selection memory"));
    }


    public final Hashtable<String, String> selection = new Hashtable<>();

    public void setSelection(String categoryName, Block block) { setSelection(categoryName, ResourcesUtil.blockId(block)); }
    public void setSelection(String categoryName, String blockId) {
        if (!CategoryRegistry.getCategory(categoryName).isEmpty())
            selection.put(categoryName, blockId);
        else
            EzDebug.warn("category with name " + categoryName + " is empty");
    }
    public @Nullable Block getSelectedBlockIfExist(String categoryName) {
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
    public @Nullable Block getSelectedBlockOrMainBlock(String categoryName) {
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

    public @Nullable Block getSelectBlockIfExistOf(ItemStack stack) {
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
    public @Nullable Block getSelectBlockOrMainBlockOf(ItemStack stack) {
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
*/