package com.lancas.vswap.subproject.mstandardized;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.util.GsonUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CategoryRegistry extends SimpleJsonResourceReloadListener  {
    //public static final ResourceKey<Registry<CategoryBuilder>> KEY =
    //    ResourceKey.createRegistryKey(new ResourceLocation(ModMain.MODID, "ms_category"));

    public static final HashMap<String, CategoryBuilder> categoryBuilders = new HashMap<>();

    /*public static class Provider extends DatapackBuiltinEntriesProvider {
        private static RegistrySetBuilder registrySetBuilder() {
            RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
            for (CategoryBuilder builder : categoryBuilders) {
                registrySetBuilder.add(KEY, bootstrap -> {
                    bootstrap.register(builder.getKey(), builder);
                });
            }
            return registrySetBuilder;
        }

        public Provider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, registrySetBuilder(), Set.of(ModMain.MODID));
        }
    }*/


    //protected boolean initialized = false;
    //sometime it cause ConcurrentModificationException(when i use HashMap), so now I use ConcurrentHashMap
    protected static ConcurrentHashMap<String, Category> runtimeCategories = new ConcurrentHashMap<>();  //outside is readonly so no worry about concurrent
    protected static ConcurrentHashMap<Block, String> blockCategoryCache = new ConcurrentHashMap<>();
    //protected HashMap<Block, Category> blockCategoryCache = new HashMap<>();

    /*protected void initialize() {
        categoryBuilders.stream()
            .filter(CategoryBuilder::valid)
            .forEach(x -> {
                runtimeCategories.put(x.categoryName, x.build());
                EzDebug.log("has cate builder:" + x.categoryName);
            });
    }*/
    public CategoryRegistry() {
        super(new Gson(), "ms_category");
    }

    @NotNull public static Category getCategory(String categoryName) {
        if (runtimeCategories.isEmpty())
            buildAll();

        Category existed = runtimeCategories.get(categoryName);
        if (existed != null) {
            return existed;
        }

        return buildRuntimeCategory(categoryName);
    }
    @NotNull  //todo handle when block is air
    public static Category getCategory(Block block) {
        if (runtimeCategories.isEmpty())
            buildAll();

        //String blockID = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString();
        String cachedCategoryName = blockCategoryCache.get(block);
        if (cachedCategoryName != null) {  //has cache
            Category cached = runtimeCategories.get(cachedCategoryName);
            if (cached == null) {
                EzDebug.warn("block " + block.getName() + " has cached category " + cachedCategoryName + ", but can't find the category");
                return Category.EMPTY;
            }
            return cached;
        }

        //no cached, try find one and cache it.
        Category existedRuntime = runtimeCategories.values().stream()
            .filter(c -> c.contains(block))
            .findFirst()
            .orElse(null);

        if (existedRuntime != null) {
            blockCategoryCache.put(block, existedRuntime.categoryName);
            return existedRuntime;
        }

        return buildRuntimeCategory(block);
    }

    @NotNull
    private static Category buildRuntimeCategory(String categoryName) {
        /*CategoryBuilder builder = categoryBuilders.get(categoryName);
        if (builder != null) {
            Category category = builder.build();
            runtimeCategories.put(categoryName, category);
            return category;
        }*/

        //existed builder is null, the categoryName should be a blockID, will make a single-block category
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(categoryName));
        if (block == null) {
            EzDebug.warn("can't get category by name:" + categoryName);
            return Category.EMPTY;
        }

        Category category = Category.oneBlockCategory(block);
        runtimeCategories.put(categoryName, category);
        return category;
    }
    @NotNull
    private static Category buildRuntimeCategory(Block block) {
        if (block == null) {
            EzDebug.warn("block is null!");
            return Category.EMPTY;
        }

        Category category = Category.oneBlockCategory(block);
        runtimeCategories.put(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).toString(), category);
        return category;
    }
    /*@NotNull
    private static Category buildRuntimeCategory(Block block) {
        Category built = categoryBuilders.values().stream()
            .filter(x -> x.contains(block))
            .map(CategoryBuilder::build)
            .findFirst()
            .orElse(Category.oneBlockCategory(block));

        runtimeCategories.put(built.categoryName, built);
        blockCategoryCache.put(block, built.categoryName);
        return built;
    }*/
    private static void buildAll() {
        if (!runtimeCategories.isEmpty()) {
            EzDebug.warn("have build already!");
            return;
        }

        ForgeRegistries.BLOCKS.forEach(b -> {
            for (var builder : categoryBuilders.values()) {
                if (builder.contains(b)) {
                    Category category = runtimeCategories.computeIfAbsent(builder.categoryName, k -> new Category(k, builder.localizationKey, builder.iconKey));
                    category.add(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(b)).toString());
                }
            }
        });
    }




    private void clear() {
        categoryBuilders.clear();
        runtimeCategories.clear();
        blockCategoryCache.clear();
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resLoc_JsonEleMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        clear();

        //LOGGER.info("DoubleBlockInfoRegister is applying");
        //List<CategoryBuilder> validCategoryBuilders = new ArrayList<>();

        for (var entry : resLoc_JsonEleMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonElement jsonEle = entry.getValue();

            //LOGGER.info("DoubleBlockInfoRegister get:" + location.toString());
            GsonUtil.objOfArrayEvenNested(jsonEle)
                .forEach(x -> {
                    Dest<String> categoryName = new Dest<>();
                    Dest<CategoryBuilder> builderDest = new Dest<>();

                    if (readCategory(location, x, categoryName, builderDest)) {
                        if (!builderDest.get().valid()) {
                            EzDebug.warn("category [" + categoryName.get() + "] is invalid and won't be recorded.");
                            return;
                        }

                        CategoryBuilder prevBuilder = categoryBuilders.get(categoryName.get());
                        if (prevBuilder != null)
                            prevBuilder.append(builderDest.get());
                        else
                            categoryBuilders.put(categoryName.get(), builderDest.get());
                    }
                });
        }

        /*for (CategoryBuilder builder : validCategoryBuilders) {
            runtimeCategories.put(builder.categoryName, builder.build());
        }*/
    }
    private boolean readCategory(ResourceLocation location, JsonObject jsonObj, Dest<String> categoryNameDest, Dest<CategoryBuilder> builderDest) {
        Set<String> include = getStringSetByFieldOfJsonObj(jsonObj, "include");
        Set<String> exclude = getStringSetByFieldOfJsonObj(jsonObj, "exclude");

        Dest<String> localizationKey = new Dest<>();

        @NotNull ResourceLocation iconKey;
        //@NotNull String categoryName;

        try {
            String categoryName = jsonObj.get("name").getAsString();
            categoryNameDest.set(categoryName);

            if (jsonObj.has("localization")) {
                String locKey = jsonObj.get("localization").getAsString();
                localizationKey.set(locKey);
            } else {
                localizationKey.set(categoryName);
            }


            if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(categoryName))) {
                EzDebug.warn("category name can't be same to a block's id. will not record this. categoryName:" + categoryName);
                return false;
            }
            if (categoryName.equals(Category.EmptyCategoryName)) {
                EzDebug.warn("category name can't be:" + categoryName);
                return false;
            }

            /*if (runtimeCategories.containsKey(categoryName)) {
                EzDebug.warn("existed category name: " + categoryName + " will override the previous category if it's not empty");
            }*/

        } catch (Exception e) {
            EzDebug.warn("fail to get category name");
            return false;
        }

        try {
            iconKey = new ResourceLocation(jsonObj.get("icon").getAsString());
        } catch (Exception e) {
            EzDebug.warn("fail to get icon of category[" + categoryNameDest.get() + "], will use default icon");
            //iconKey = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DIRT));
            iconKey = Category.EmptyIconItemKey;
        }

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

        builderDest.set(new CategoryBuilder(
            categoryNameDest.get(), localizationKey.get(), includeTags, includeIDs, excludeTags, excludeIDs, iconKey.toString()
        ));
        return true;
    }
    private @NotNull Set<String> getStringSetByFieldOfJsonObj(JsonObject jsonObj, String fieldName) {
        Set<String> set = new HashSet<>();

        if (!jsonObj.has(fieldName)) {
            EzDebug.warn("the field with name[" + fieldName + "] don't exist. It's ok but recommended to declare it with value of empty array. make sure that field name in json are spelled right.");
            return set;
        }

        JsonElement field = jsonObj.get(fieldName);
        if (field.isJsonArray()) {
            JsonArray strings = jsonObj.getAsJsonArray(fieldName);
            strings.asList().stream()
                .map(s -> {
                    try {
                        return s.getAsString();
                    } catch (Exception e) {
                        EzDebug.warn("fail to read a string value of set:" + fieldName);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(set::add);
        } else {
            try {
                set.add(Objects.requireNonNull(field.getAsString()));
            } catch (Exception e) {
                EzDebug.warn("fail to read string value of field:" + fieldName);
            }
        }/* else {
            EzDebug.warn("unexpected type of field:" + fieldName + ", isArray?:" + jsonObj.isJsonArray() + ", isObj?:" + jsonObj.isJsonObject() + ", isPrim?:" + jsonObj.isJsonPrimitive());
        }*/

        return set;
    }
}

/*
public class CategoryRegistry extends SimpleJsonResourceReloadListener {
    protected static HashMap<String, Category> allCategories = new HashMap<>();  //outside is readonly so no worry about concurrent
    protected static HashMap<Block, Category> blockCategoryCache = new HashMap<>();




    public CategoryRegistry() { super(new Gson(), "ms_category"); }


}
*/