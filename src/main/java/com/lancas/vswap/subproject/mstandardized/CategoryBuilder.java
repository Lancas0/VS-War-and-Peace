package com.lancas.vswap.subproject.mstandardized;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CategoryBuilder {  //Category as generic param, act like friend class
    /*public static final ResourceKey<CategoryBuilder> KEY = ResourceKey.create(
        CategoryRegistry.KEY,
        new ResourceLocation(ModMain.MODID, "category_builder")
    );*/
    /*public ResourceKey<CategoryBuilder> getKey() {
        return ResourceKey.create(CategoryRegistry.KEY, new ResourceLocation(ModMain.MODID, categoryName));
    }

    public static final Codec<CategoryBuilder> CODEC = RecordCodecBuilder.create(i ->
        i.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.categoryName),
            Codec.STRING.fieldOf("icon").forGetter(x -> x.iconKey.toString()),
            Codec.STRING.listOf().fieldOf("include").forGetter(
                x -> Stream.concat(x.includeTags.stream(), x.includeIDs.stream()).toList()
            ),
            Codec.STRING.listOf().optionalFieldOf("exclude", new ArrayList<>()).forGetter(
                x -> Stream.concat(x.excludeTags.stream(), x.excludeIDs.stream()).toList()
            )
        ).apply(i, (name, icon, include, exclude) -> {
            Set<String> includeTags = new HashSet<>();
            Set<String> includeIDs = new HashSet<>();
            Set<String> excludeTags = new HashSet<>();
            Set<String> excludeIDs = new HashSet<>();

            include.forEach(x -> {
                boolean isTag = x.startsWith("#");
                if (isTag) {
                    includeTags.add(x.substring(1));
                } else {
                    includeIDs.add(x);
                }
            });
            exclude.forEach(x -> {
                boolean isTag = x.startsWith("#");
                if (isTag) {
                    excludeTags.add(x.substring(1));
                } else {
                    excludeIDs.add(x);
                }
            });

            return new CategoryBuilder(name, includeTags, includeIDs, excludeTags, excludeIDs, icon);
        })
    );*/


    public final String categoryName;

    //todo concurrent?
    protected Set<String> includeTags = new HashSet<>();
    protected Set<String> includeIDs = new HashSet<>();
    protected Set<String> excludeTags = new HashSet<>();
    protected Set<String> excludeIDs = new HashSet<>();

    //protected Set<String> builtIncludeIDs = new HashSet<>();
    //todo cache in category blocks after use contains()?

    protected String iconKey;  //todo icon maybe a picture or itemStack.
    protected ItemStack cachedIcon = null;  //todo is it really useful?
    public ItemStack getIcon() {
        if (cachedIcon != null)
            return cachedIcon;

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(iconKey));
        if (item == null) {
            item = Items.DIRT;
        }
        cachedIcon = item.getDefaultInstance();

        return cachedIcon;
    }

    //protected Category(@NotNull String inCategoryName, @NotNull ResourceLocation inIconKey) { categoryName = inCategoryName; iconKey = inIconKey; }
    public CategoryBuilder(String inCategoryName, Collection<String> inIncludeTags, Collection<String> inIncludeIDs, Collection<String> inExcludeTags, Collection<String> inExcludeIDs, String inIconID) {
        categoryName = inCategoryName;

        includeTags.addAll(inIncludeTags);
        includeIDs.addAll(inIncludeIDs);
        excludeTags.addAll(inExcludeTags);
        excludeIDs.addAll(inExcludeIDs);

        iconKey = inIconID;
        /*if (!ForgeRegistries.ITEMS.containsKey(iconKey)) {
            EzDebug.warn("create category icon fail. will create default icon of DIRT");
            iconKey = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DIRT));
        }*/
    }

    protected boolean contains(Block block) {
        ResourceLocation blockKey = ForgeRegistries.BLOCKS.getKey(block);
        if (blockKey == null) {
            EzDebug.warn("the block is not in ForgeRegistries:" + block.getName());
            return false;
        }
        String blockID = blockKey.toString();

        if (excludeIDs.contains(blockID))
            return false;

        if (includeIDs.contains(blockID))
            return true;

        //return true if match any tag
        AtomicBoolean anyTagIncluded = new AtomicBoolean(false);
        AtomicBoolean anyTagExcluded = new AtomicBoolean(false);
        block.defaultBlockState().getTags()
            .map(t -> t.location().toString())
            .forEach(t -> {
                if (anyTagExcluded.get())
                    return;

                if (excludeTags.contains(t))
                    anyTagExcluded.set(true);

                if (includeTags.contains(t))
                    anyTagIncluded.set(true);
            });


        //EzDebug.log("cate builder, blockID:" + blockID + ", return:" + (anyTagIncluded.get() && (!anyTagExcluded.get())));
        return anyTagIncluded.get() && (!anyTagExcluded.get());
    }
    public Category build() {
        if (!valid()) {
            throw new RuntimeException("should not called build because the builder is not valid");
        }
        //return new Category(categoryName, builtIncludeIDs, iconKey);
        List<String> ids = new ArrayList<>();
        BuiltInRegistries.BLOCK.forEach(b -> {
            if (contains(b))
                ids.add(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(b)).toString());
        });
        return new Category(categoryName, ids, iconKey);
    }

    public boolean valid() {
        //return !builtIncludeIDs.isEmpty();
        return
            !includeTags.isEmpty() ||
            !includeIDs.isEmpty()  ||
            !excludeTags.isEmpty() ||
            !excludeIDs.isEmpty();
    }
}
