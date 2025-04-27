package com.lancas.vs_wap.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EzDebug {
    private static boolean NOT_DEBUG = true;
    private static boolean alwaysDebug;

    private EzDebug(boolean inAlwaysDebug) { alwaysDebug = inAlwaysDebug; }
    public static final EzDebug DEFAULT = new EzDebug(false);
    public static final EzDebug ALWAYS_DEBUG = new EzDebug(true);

    public static EzDebug log(String str) {
        if (NOT_DEBUG) return DEFAULT;

        Minecraft mcInst = Minecraft.getInstance();
        boolean isClient = (mcInst.player != null);
        if (isClient) {
            mcInst.player.sendSystemMessage(Component.literal(str));
        } else {
            //todo temp
        }

        System.out.println((isClient ? "[Client]" : "[Server]") + str);
        return DEFAULT;
    }

    public static <T> EzDebug logs(Iterable<T> c, @Nullable Function<T, String> strGetter) {
        if (NOT_DEBUG) return DEFAULT;

        if (c == null) {
            EzDebug.log("the collection is null.");
            return DEFAULT;
        }

        StringBuilder sb = new StringBuilder("Logs:\n");
        for (T t : c) {
            if (t == null) continue;  //todo maybe not skip
            if (strGetter == null)
                sb.append(t.toString()).append(", ");
            else
                sb.append(strGetter.apply(t)).append(", ");
        }
        log(sb.toString());
        return DEFAULT;
    }
    public static <K, V> EzDebug logs(Map<K, V> m, @Nullable BiFunction<K, V, String> strGetter) {
        if (NOT_DEBUG) return DEFAULT;

        if (m == null) {
            EzDebug.log("the map is null.");
            return DEFAULT;
        }

        StringBuilder sb = new StringBuilder("Logs:\n");
        m.forEach((k, v) -> {
            if (strGetter == null)
                sb.append("Key:" + k + ", Value:" + v).append("\n");
            else
                sb.append(strGetter.apply(k, v)).append("\n");
        });
        log(sb.toString());
        return DEFAULT;
    }

    public static EzDebug highlight(String str) {
        if (NOT_DEBUG) return DEFAULT;

        Minecraft mcInst = Minecraft.getInstance();
        if (mcInst != null && mcInst.player != null) {
            mcInst.player.sendSystemMessage(Component.literal("§a" + str));
        }

        System.out.println(str.startsWith("[Highlight]") ? str : "[Highlight]" + str);
        return DEFAULT;
    }
    public static EzDebug light(String str) {
        if (NOT_DEBUG) return DEFAULT;

        Minecraft mcInst = Minecraft.getInstance();
        if (mcInst != null && mcInst.player != null) {
            mcInst.player.sendSystemMessage(Component.literal("§7" + str));
        }

        System.out.println(str.startsWith("[Light]") ? str : "[Light]" + str);
        return DEFAULT;
    }

    public static EzDebug warn(String str) {
        if (NOT_DEBUG) return DEFAULT;

        Minecraft mcInst = Minecraft.getInstance();
        if (mcInst != null && mcInst.player != null) {
            mcInst.player.sendSystemMessage(Component.literal("§6" + str));
        }

        System.out.println(str.startsWith("[Warn]") ? str : "[Warn]" + str);
        return DEFAULT;
    }
    public static EzDebug error(String str) {
        if (NOT_DEBUG) return DEFAULT;

        Minecraft mcInst = Minecraft.getInstance();
        if (mcInst != null && mcInst.player != null) {
            mcInst.player.sendSystemMessage(Component.literal("§4" + str));
        }

        System.out.println(str.startsWith("[Error]") ? str : "[Error]" + str);
        return DEFAULT;
    }
    public static EzDebug fatal(String str) {
        if (NOT_DEBUG) return DEFAULT;

        Minecraft mcInst = Minecraft.getInstance();
        if (mcInst != null && mcInst.player != null) {
            mcInst.player.sendSystemMessage(Component.literal("§4" + str));
        }

        System.out.println(str.startsWith("[Fatal]") ? str : "[Fatal]" + str);
        return DEFAULT;
    }



}
