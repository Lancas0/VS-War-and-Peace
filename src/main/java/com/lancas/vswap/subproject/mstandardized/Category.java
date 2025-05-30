package com.lancas.vswap.subproject.mstandardized;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Category {
    public static final String EmptyCategoryName = "empty_category";
    public static final ResourceLocation EmptyIconItemKey = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    public static final Supplier<Item> EmptyIconItemSupplier = () -> Items.DIRT;

    public static Category oneBlockCategory(Block block) {
        ResourceLocation blockKey = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
        String blockID = blockKey.toString();

        return new Category(blockID, List.of(blockID), blockID);
    }
    public static final Category EMPTY = new Category(EmptyCategoryName, List.of(), EmptyIconItemKey.toString());  //todo iconID set a emptyItemIcon

    /*
    "name": "dirt_category",
    "icon": "minecraft:dirt",
    "include": [
      "#minecraft:dirt",
      "minecraft:dirt_path"
    ],
    "exclude": []
     */

    public final String categoryName;  //can be translatable
    //todo concurrent?
    protected Set<String> ids = new HashSet<>();
    public @Nullable Block getMainBlock() {
        return getAllBlock().findFirst().orElse(null);  //getAllBlockItem is a stream so it would be ok.
    }
    public Stream<Block> getAllBlock() {
        return ids.stream()
            .map(x -> {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(x));

                //debug
                if (block == null) {
                    EzDebug.warn("can't find block: " + x);
                } /*else if (!(block.asItem() instanceof BlockItem i)) {
                    EzDebug.warn("get item from block: " + x + " is not blockItem");
                }

                if (block != null && block.asItem() instanceof BlockItem item)
                    return item;*/

                //return null;
                return block;
            })
            .filter(Objects::nonNull);
    }

    protected Category(String inCategoryName, Collection<String> inIDs, String inIconID) {
        categoryName = inCategoryName;

        ids.addAll(inIDs);

        iconKey = inIconID;
        /*if (!ForgeRegistries.ITEMS.containsKey(iconKey)) {
            EzDebug.warn("create category icon fail. will create default icon of DIRT");
            iconKey = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DIRT));
        }*/
    }

    protected String iconKey;  //todo icon maybe a picture or itemStack.
    protected Item cachedIconItem = null;  //todo is it really useful?
    public @NotNull Item getIconItem() {
        if (cachedIconItem != null)
            return cachedIconItem;

        Item iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(iconKey));
        if (iconItem == null) {
            EzDebug.log("fail to get icon of category:" + categoryName);
            iconItem = EmptyIconItemSupplier.get();  //todo default empty icon
        }
        cachedIconItem = iconItem;

        return cachedIconItem;
    }

    public boolean isEmpty() { return ids.isEmpty(); }
    public boolean contains(@NotNull Block block) {
        ResourceLocation blockKey = ForgeRegistries.BLOCKS.getKey(block);
        if (blockKey == null) {
            EzDebug.warn("the block is not in ForgeRegistries:" + block.getName());
            return false;
        }

        return ids.contains(blockKey.toString());
    }

    @Override
    public String toString() {
        return "Category{" +
            "categoryName='" + categoryName + '\'' +
            ", ids=" + String.join(",", ids) +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryName);
    }
}