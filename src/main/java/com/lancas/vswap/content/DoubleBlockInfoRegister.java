package com.lancas.vswap.content;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lancas.vswap.ModMain;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoubleBlockInfoRegister extends SimpleJsonResourceReloadListener {
    //private static final Logger LOGGER = LoggerFactory.getLogger("SimpleJsonReloadListener|Forge");


    //blockID, infoName
    private final Set<BiTuple<String, String>> registeredBlockInfos = new HashSet<>();
    private final Set<BiTuple<String, String>> registeredTagInfos = new HashSet<>();

    public DoubleBlockInfoRegister() {
        super(new Gson(), "wap_block_info");
        //LOGGER.info("DoubleBlockInfoRegister is created");
    }

    private void clear() {
        for (var blockRegistryData : registeredBlockInfos) {
            String blockID = blockRegistryData.getFirst();
            WapBlockInfos.BlockInfo<?> info = WapBlockInfos.valueOf(blockRegistryData.getSecond());
            if (info == null) {
                throw new IllegalArgumentException("unknown infoName:" + blockRegistryData.getSecond());
            }

            info.removeBlock(blockID);
        }
        registeredBlockInfos.clear();

        for (var tagRegistryData : registeredTagInfos) {
            String tag = tagRegistryData.getFirst();
            WapBlockInfos.BlockInfo<?> info = WapBlockInfos.valueOf(tagRegistryData.getSecond());
            if (info == null) {
                throw new IllegalArgumentException("unknown infoName:" + tagRegistryData.getSecond());
            }

            info.removeTag(tag);
        }
        registeredTagInfos.clear();
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resLoc_JsonEleMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        clear();

        //LOGGER.info("DoubleBlockInfoRegister is applying");
        for (var entry : resLoc_JsonEleMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonElement jsonEle = entry.getValue();

            //LOGGER.info("DoubleBlockInfoRegister get:" + location.toString());
            try {
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
            }
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
                blockInfo = (WapBlockInfos.BlockInfo<Double>) WapBlockInfos.valueOf(infoName);
                if (blockInfo == null)
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                EzDebug.warn("fail to get info, block:" + blockID + ", infoName:" + infoName + ", cause:" + e.toString());
                continue;
            }

            //EzDebug.highlight("add to block:" + blockID + ", " + infoName + ", val:" + infoVal);
            blockInfo.addBlock(blockID, state -> infoVal);
            registeredBlockInfos.add(new BiTuple<>(blockID, infoName));
        }
    }
    private void addTagInfo(ResourceLocation location, JsonObject jsonObj) {
        String tag = jsonObj.get("tag").getAsString();
        for (var infoEntry : jsonObj.entrySet()) {
            //do not treat block as a double field name
            if (infoEntry.getKey().equals("tag"))
                continue;

            String infoName = infoEntry.getKey();
            double infoVal = Double.parseDouble(infoEntry.getValue().getAsString());
            WapBlockInfos.BlockInfo<Double> blockInfo;

            try {
                blockInfo = (WapBlockInfos.BlockInfo<Double>) WapBlockInfos.valueOf(infoName);
                if (blockInfo == null)
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                EzDebug.warn("fail to get info, block:" + tag + ", infoName:" + infoName + ", cause:" + e.toString());
                continue;
            }

            blockInfo.addTag(tag, state -> infoVal);
            registeredBlockInfos.add(new BiTuple<>(tag, infoName));
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
