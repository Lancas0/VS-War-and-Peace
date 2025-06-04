package com.lancas.vswap.content;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.infrastructure.config.CClient;
import com.simibubi.create.infrastructure.config.CCommon;
import com.simibubi.create.infrastructure.config.CServer;
import com.simibubi.create.infrastructure.config.CStress;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.function.Supplier;
/*
@Mod.EventBusSubscriber
public class WapConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Integer> dockMaxLength;
    public static final ForgeConfigSpec.ConfigValue<Integer> dockMaxWidth;

    static {
        BUILDER.push("Example Mod Common Configs");

        dockMaxLength = BUILDER.comment("The max length of connected dock").defineInRange("dock_max_length", 16, 1, Integer.MAX_VALUE);
        dockMaxWidth = BUILDER.comment("The max width of connected dock").defineInRange("dock_max_width", 16, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    /.*public static class CommonConfig {
        public final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public final ForgeConfigSpec SPEC;

        public CommonConfig() {
            BUILDER.push("Example Mod Common Configs");

            dockMaxLength = BUILDER.comment("The max length of connected dock").defineInRange("dock_max_length", 16, 1, Integer.MAX_VALUE);
            dockMaxWidth = BUILDER.comment("The max width of connected dock").defineInRange("dock_max_width", 16, 1, Integer.MAX_VALUE);

            BUILDER.pop();
            SPEC = BUILDER.build();
        }

        public final ForgeConfigSpec.ConfigValue<Integer> dockMaxLength;
        public final ForgeConfigSpec.ConfigValue<Integer> dockMaxWidth;
    }

    public static final CommonConfig common = new CommonConfig();*./



    /.*public static class Basic extends ConfigBase {
        public final ConfigInt dockMaxLength = i(16, 1, 1024, "dockMaxLength", "The max length of dock");
        public final ConfigInt dockMaxWidth = i(16, 1, 1024, "dockMaxWidth", "The max width of dock");

        @Override
        public String getName() {
            return "basic";
        }
    }

    private static Basic basic = new Basic();
    public static Basic basic() { return basic; }


    private static <T extends ConfigBase> T registerOneConfig(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        //CONFIGS.put(side, config);
        return config;
    }
    public static void register(ModLoadingContext context) {
        basic = registerOneConfig(Basic::new, ModConfig.Type.COMMON);

        //for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
        //    context.registerConfig(pair.getKey(), pair.getValue().specification);
        context.registerConfig(ModConfig.Type.COMMON, basic.specification);

        //CStress stress = server().kinetics.stressValues;
        //BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
        //BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
    }


    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        /.*for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig()
                .getSpec())
                config.onLoad();*./
        if (basic.specification == event.getConfig().getSpec())
            basic.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (basic.specification == event.getConfig().getSpec())
            basic.onReload();
    }*./

}
*/