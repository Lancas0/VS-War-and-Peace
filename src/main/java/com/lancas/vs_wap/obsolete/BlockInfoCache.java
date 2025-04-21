package com.lancas.vs_wap.obsolete;

/*
import com.lancas.einherjar.foundation.BiTuple;
import com.lancas.einherjar.subproject.blockplusapi.blockplus.BlockPlus;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

public class BlockInfoCache {
    //todo concurrent?
    //key is <blockId, infoName>
    private static final Map<BiTuple<String, String>, Function<BlockState, Object>> infoCache = new Hashtable<>();

    private BlockInfoCache() {}

    public abstract <T> T getInfoValue(BlockState state);

    public static BlockInfoCache getOrCreate(String blockID, String name, Function<BlockState, Object> attributeGetter, @Nullable Function<Object, String> formatter, boolean explicit) {
        BiTuple<String, String> key = new BiTuple<>(blockID, name);

        return infoCache.computeIfAbsent(
            key,
            k -> new BlockInfoAdderImpl(k.getSecond(), attributeGetter, formatter, explicit)
        );
    }
    public static boolean removeBlockInfo(String blockID, String infoName) {
        return infoCache.remove(new BiTuple<>(blockID, infoName)) != null;
    }
    public static boolean removeBlockInfo(BiTuple<String, String> infoKey) {
        return infoCache.remove(infoKey) != null;
    }
    public static BlockInfoCache getOrCreateExplicit(Class<? extends BlockPlus> blockType, String name, Function<BlockState, Object> attributeGetter, @Nullable Function<Object, String> formatter) {
        return getOrCreate(blockType, name, attributeGetter, formatter, true);
    }
    public static BlockInfoCache getOrCreateImplicit(Class<? extends BlockPlus> blockType, String name, Function<BlockState, Object> attributeGetter, @Nullable Function<Object, String> formatter) {
        return getOrCreate(blockType, name, attributeGetter, formatter, false);
    }


    public static <T> T infoValueOf(BlockState state, String name) {
        if (!(state.getBlock() instanceof BlockPlus blockPlus)) {
            return null;
        }

        Class<? extends BlockPlus> blockType = blockPlus.getClass();
        BlockInfoCache adder = infoCache.get(new BiTuple<Class<? extends BlockPlus>, String>(blockType, name));
        if (adder == null)
            return null;
        return adder.getInfoValue(state);
    }
}
*/