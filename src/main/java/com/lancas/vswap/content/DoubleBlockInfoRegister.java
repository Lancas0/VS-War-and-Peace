package com.lancas.vswap.content;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.GsonUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoubleBlockInfoRegister extends SimpleJsonResourceReloadListener {
    //private static final Logger LOGGER = LoggerFactory.getLogger("SimpleJsonReloadListener|Forge");


    //blockID, infoName
    //private final Set<BiTuple<String, String>> registeredBlockInfos = new HashSet<>();
    //private final Set<BiTuple<String, String>> registeredTagInfos = new HashSet<>();

    public DoubleBlockInfoRegister() {
        super(new Gson(), "wap_block_info");
        //LOGGER.info("DoubleBlockInfoRegister is created");
    }

    private void clear() {
        WapBlockInfos.clearCache();
        /*
        for (var blockRegistryData : registeredBlockInfos) {
            String blockID = blockRegistryData.getFirst();
            WapBlockInfos.BlockInfo<?> info = WapBlockInfos.infoById(blockRegistryData.getSecond());
            if (info == null) {
                EzDebug.warn("unknown infoName:" + blockRegistryData.getSecond());
                continue;
            }

            info.removeBlock(blockID);
        }
        registeredBlockInfos.clear();

        for (var tagRegistryData : registeredTagInfos) {
            String tag = tagRegistryData.getFirst();
            WapBlockInfos.BlockInfo<?> info = WapBlockInfos.infoById(tagRegistryData.getSecond());
            if (info == null) {
                EzDebug.warn("unknown infoName:" + tagRegistryData.getSecond());
                continue;
            }

            info.removeTag(tag);
        }
        registeredTagInfos.clear();*/
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resLoc_JsonEleMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        clear();

        //LOGGER.info("DoubleBlockInfoRegister is applying");
        for (var entry : resLoc_JsonEleMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonElement jsonEle = entry.getValue();

            GsonUtil.objOfArrayEvenNested(jsonEle)
                .forEach(jsonObj -> {
                    addBlockOrTagInfo(location, jsonObj.getAsJsonObject());
                });

            //LOGGER.info("DoubleBlockInfoRegister get:" + location.toString());
            /*try {
                if (jsonEle.isJsonArray()) {
                    for (JsonElement jsonObj : jsonEle.getAsJsonArray()) {
                        if (jsonObj.isJsonObject()) {
                            addBlockOrTagInfo(location, jsonObj.getAsJsonObject());
                        } else {
                            //not allow nested json array
                            throw new IllegalArgumentException();
                        }
                    }

                    continue;
                }

                if (jsonEle.isJsonObject()) {
                    addBlockOrTagInfo(location, jsonEle.getAsJsonObject());
                    continue;
                }

                //not json array, not json obj
                throw new IllegalArgumentException();
            } catch (Exception e) {
                EzDebug.warn("fail to register block info:" + e.toString());
            }*/
        }
    }
    private void addBlockOrTagInfo(ResourceLocation location, JsonObject jsonObj) {
        if (jsonObj.has("block")) {
            addBlockInfo(location, jsonObj);
            return;
        }

        if (jsonObj.has("tag")) {
            addTagInfo(location, jsonObj);
            return;
        }

        if (jsonObj.has("category")) {
            addCategoryInfo(location, jsonObj);
            return;
        }

        StringBuilder allKeys = new StringBuilder();
        for (String key : jsonObj.keySet()) {
            allKeys.append(key).append(", ");
        }
        EzDebug.warn("fail to register at: " + location.toString() + ", json has no field block or tag, allKeys:" + allKeys);
    }
    private void addBlockInfo(ResourceLocation location, JsonObject jsonObj) {
        String blockID = jsonObj.get("block").getAsString();
        for (var infoEntry : jsonObj.entrySet()) {
            //do not treat block as a double field name
            if (infoEntry.getKey().equals("block"))
                continue;

            String infoName = infoEntry.getKey();
            double infoVal = infoEntry.getValue().getAsDouble();
            WapBlockInfos.BlockInfo<Double> blockInfo;

            try {
                blockInfo = (WapBlockInfos.BlockInfo<Double>) WapBlockInfos.infoById(infoName);
                if (blockInfo == null)
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                EzDebug.warn("fail to get info, block:" + blockID + ", infoName:" + infoName + ", cause:" + e.toString());
                continue;
            }

            //EzDebug.highlight("add to block:" + blockID + ", " + infoName + ", val:" + infoVal);
            blockInfo.addBlock(blockID, state -> infoVal);
            //registeredBlockInfos.add(new BiTuple<>(blockID, infoName));
        }
    }
    private void addTagInfo(ResourceLocation location, JsonObject jsonObj) {
        String tagStr = jsonObj.get("tag").getAsString();
        tagStr = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;

        ResourceLocation tagLoc = ResourceLocation.tryParse(tagStr);
        if (tagLoc == null) {
            EzDebug.warn("fail to get res location:" + tagStr);
            return;
        }

        TagKey<Block> tag = TagKey.create(Registries.BLOCK, tagLoc);

        for (var infoEntry : jsonObj.entrySet()) {
            //do not treat block as a double field name
            if (infoEntry.getKey().equals("tag"))
                continue;

            String infoName = infoEntry.getKey();
            double infoVal = Double.parseDouble(infoEntry.getValue().getAsString());
            WapBlockInfos.BlockInfo<Double> blockInfo;

            try {
                blockInfo = (WapBlockInfos.BlockInfo<Double>) WapBlockInfos.infoById(infoName);
                if (blockInfo == null)
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                EzDebug.warn("fail to get info, block:" + tag + ", infoName:" + infoName + ", cause:" + e.toString());
                continue;
            }

            blockInfo.addTag(tag, state -> infoVal);
            //registeredBlockInfos.add(new BiTuple<>(tag, infoName));
        }
    }
    private void addCategoryInfo(ResourceLocation location, JsonObject jsonObj) {
        String categoryName = jsonObj.get("category").getAsString();
        categoryName = categoryName.startsWith("*") ? categoryName.substring(1) : categoryName;

        for (var infoEntry : jsonObj.entrySet()) {
            //do not treat block as a double field name
            if (infoEntry.getKey().equals("category"))
                continue;

            String infoName = infoEntry.getKey();
            double infoVal = Double.parseDouble(infoEntry.getValue().getAsString());
            WapBlockInfos.BlockInfo<Double> blockInfo;

            try {
                blockInfo = (WapBlockInfos.BlockInfo<Double>) WapBlockInfos.infoById(infoName);
                if (blockInfo == null)
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                EzDebug.warn("fail to get info, category:" + categoryName + ", infoName:" + infoName + ", cause:" + e.toString());
                continue;
            }

            blockInfo.addCategory(categoryName, state -> infoVal);
            //registeredBlockInfos.add(new BiTuple<>(tag, infoName));
        }
    }
    /*private double smartReadDouble(JsonObject jsonObj, String name) {
        if (!jsonObj.has(name))
            throw new IllegalArgumentException("json Obj has no field:" + name);

        Double.parseDouble()
        jsonObj.getAsString(name);
    }*/
    /*@SubscribeEvent(priority = EventPriority.HIGHEST) // 事件处理优先级最高
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new DoubleBlockInfoRegister());
    }*/
}
