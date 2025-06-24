package com.lancas.vswap;

/*
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

// An vanilla.disabled config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = VsWap.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WapClientConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    /.*private static final ForgeConfigSpec.ConfigValue<Float> SHAKE_INTENSE = BUILDER
        .comment("Shake intense of ")
        .defineInRange("dockMaxLength", 16, 1, Integer.MAX_VALUE);*./

    private static final ForgeConfigSpec.ConfigValue<Integer> DOCK_MAX_LENGTH = BUILDER
        .comment("The max length of connected docks.")
        .defineInRange("dockMaxLength", 16, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Integer> DOCK_MAX_WIDTH = BUILDER
        .comment("The max width of connected docks.")
        .defineInRange("dockMaxWidth", 16, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> FATAL_KE_RATIO = BUILDER
        .comment(
            "When the kinetic energy of projectile is x times of (armourHardness * equivalentArmourDepth), the projectile never rcochets.",
            "When this value is less or equals to 1.0, the attribute is off"
        )
        .defineInRange("fatalKeRatio", 4.0, 1.0, Double.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int dockMaxLength;
    public static int dockMaxWidth;

    public static double rawFatalPPRatio;

    public static boolean isFatalKEOn() { return rawFatalPPRatio > 1.0; }

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        /*logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                .collect(Collectors.toSet());*./

        dockMaxLength = DOCK_MAX_LENGTH.get();
        dockMaxWidth = DOCK_MAX_WIDTH.get();
        rawFatalPPRatio = FATAL_KE_RATIO.get();


    }
}
*/