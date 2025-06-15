package com.lancas.vswap.debug;

import com.lancas.vswap.WapConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class EzDebug {
    public static final Logger logger = Logger.getLogger("EzDebug");

    //private static final boolean NOT_DEBUG = false;
    //private static boolean alwaysDebug;
    //private static final boolean PRINT_STACK_TRACE = false;

    //private EzDebug(boolean inAlwaysDebug) { alwaysDebug = inAlwaysDebug; }
    public static final EzDebug DEFAULT = new EzDebug();
    //public static final EzDebug ALWAYS_DEBUG = new EzDebug();

    private static final Hashtable<Object, String> scheduleLog = new Hashtable<>();
    public static EzDebug schedule(Object key, String log) {
        scheduleLog.put(key, log);
        return DEFAULT;
    }
    @SubscribeEvent
    public static void tickLog(TickEvent event) {
        scheduleLog.values().forEach(EzDebug::log);
        scheduleLog.clear();
    }

    private static void advancedLog(Level level, String msg) {
        if (WapConfig.debug_stack_trace) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            logger.log(level, msg + "\n" + Arrays.stream(stackTrace).map(Objects::toString).collect(Collectors.joining("\n")));
        } else {
            logger.log(level, msg);
        }
    }

    public static EzDebug log(String str) {
        if (!WapConfig.debug_on) return DEFAULT;

        Minecraft mc = Minecraft.getInstance();
        boolean isClient = (mc.player != null);
        if (isClient) {
            mc.player.sendSystemMessage(Component.literal(str));
        } else {
            //todo temp
        }

        advancedLog(Level.INFO, (isClient ? "[Client]" : "[Server]") + str);
        return DEFAULT;
    }

    public static <T> EzDebug logs(Iterable<T> c, @Nullable Function<T, String> strGetter) {
        if (!WapConfig.debug_on) return DEFAULT;

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
        if (!WapConfig.debug_on) return DEFAULT;

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
        if (!WapConfig.debug_on) return DEFAULT;

        Minecraft mcInst = Minecraft.getInstance();
        if (mcInst != null && mcInst.player != null) {
            mcInst.player.sendSystemMessage(Component.literal("§a" + str));
        }

        //System.out.println(str.startsWith("[Highlight]") ? str : "[Highlight]" + str);
        advancedLog(Level.FINEST, str.startsWith("[Highlight]") ? str : "[Highlight]" + str);
        return DEFAULT;
    }
    public static EzDebug light(String str) {
        if (!WapConfig.debug_on) return DEFAULT;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal("§7" + str));
        }

        advancedLog(Level.FINER, str.startsWith("[Light]") ? str : "[Light]" + str);
        return DEFAULT;
    }

    public static EzDebug warn(String str) {
        if (!WapConfig.debug_on) return DEFAULT;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal("§6" + str));
        }

        advancedLog(Level.WARNING, str.startsWith("[Warn]") ? str : "[Warn]" + str);
        return DEFAULT;
    }
    public static EzDebug error(String str) {
        if (!WapConfig.debug_on) return DEFAULT;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal("§4" + str));
        }

        advancedLog(Level.WARNING, str.startsWith("[Error]") ? str : "[Error]" + str);
        return DEFAULT;
    }
    public static EzDebug fatal(String str) {
        if (!WapConfig.debug_on) return DEFAULT;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal("§4" + str));
        }

        advancedLog(Level.WARNING, str.startsWith("[Fatal]") ? str : "[Fatal]" + str);
        return DEFAULT;
    }

    public static EzDebug notImpl(String methodName) { return error("the method [" + methodName + "] is not impl!"); }



}
