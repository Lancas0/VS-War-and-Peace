package com.lancas.vswap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3d;

// An vanilla.disabled config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WapConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    /*private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), WapCommonConfig::validateItemName);


    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;*/

    private static final ForgeConfigSpec.ConfigValue<Integer> DOCK_MAX_LENGTH = BUILDER
        .comment(
            "The max length of connected docks. (default 16)",
            "置船多方块结构的最大长度，默认16"
        )
        .defineInRange("dockMaxLength", 16, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Integer> DOCK_MAX_WIDTH = BUILDER
        .comment(
            "The max width of connected docks. (default 16)",
            "置船多方块结构的最大宽度，默认16"
        )
        .defineInRange("dockMaxWidth", 16, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> FATAL_KE_RATIO = BUILDER
        .comment(
            "When the kinetic energy of projectile is x times of (armourHardness * equivalentArmourDepth), the projectile never ricochets. (default 4.0)",
            "When this value is less or equals to 1.0, the attribute is off",
            "当炮弹动能超过致命阈值，不会发生跳弹"
        )
        .defineInRange("fatalKeRatio", 4.0, 1.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> SHAKE_INTENSITY = BUILDER
        .comment(
            "Shake intense of artillery.(Not impl)",
            "火炮发射时的视角震动强度(目前未实现视角震动)"
        )
        .defineInRange("shake_intensity", 2.0, 0.0, Float.MAX_VALUE);
    private static final ForgeConfigSpec.ConfigValue<Integer> SHAKE_TICKS = BUILDER
        .comment("Shake ticks of artillery.(Not impl)",
            "火炮发射时的视角震动时长，单位为tick(目前未实现视角震动)")
        .defineInRange("shake_ticks", 60, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> STANDARD_PROPELLANT_ENERGY = BUILDER
        .comment("The value of standard propellant energy, with unit of J (default 1E5)",
            "标准火药力的能量，单位为焦，默认100000焦")
        .defineInRange("standard_propellant_energy", 1E5, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> PROJECTILE_RANDOM_DISPLACEMENT = BUILDER
        .comment("During projectile flies, random displacement will be applied to it. As the value increase, the projectile will be less accuracy. (default 0.004)",
            "炮弹飞行时的随机扰动，这个值越大，炮弹飞行越不稳定，默认0.004")
        .defineInRange("projectile_random_displacement", 0.004, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> PROJECTILE_COLLISION_STEP = BUILDER
        .comment("Smaller the step is, the collision will be more precisely, however it consumes performance.(Not used now)",
            "炮弹碰撞精度，越大越精准，但会带来额外的性能开销(目前碰撞算法改动，所以此设置无效)")
        .defineInRange("projectile_collision_step", 0.3, 0.1, 1);

    private static final ForgeConfigSpec.ConfigValue<Double> AIR_DRAG_FACTOR = BUILDER
        .comment("F(airDrag) = 0.5 * AIR_DRAG_FACTOR * v^2 (default 0.1)",
            "此值为空气阻力系数， 空气阻力 = 0.5 * 空气阻力系数 * 速度平方，越大炮弹速度衰减越快，默认0.1")
        .defineInRange("air_drag_factor", 0.1, 0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Integer> FLYING_PROJECTILE_LIFE_SPAN = BUILDER
        .comment("The life span of flying projectile, unit is tick, 20 ticks for 1 sec, and 1200 ticks for 1min. (default 4800, 4min)",
            "飞行中的炮弹能存在的最大时长，单位为tick，默认4800ticks，即四分钟")
        .defineInRange("flying_projectile_life_span", 4800, 20, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Integer> STOPPED_PROJECTILE_LIFE_SPAN = BUILDER
        .comment("The life span of stopped projectile, unit is tick, 20 ticks for 1 sec, and 1200 ticks for 1min. (default 200, 10sec)",
            "停止后的炮弹能存在的最大时长，单位为tick，默认200ticks，即10秒")
        .defineInRange("stopped_projectile_life_span", 200, 20, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> MAX_DESTROY_RADIUS = BUILDER
        .comment("When projectile hits block, it will also try to destroy around blocks. The radius is SPE_DES_SCALAR * spe, and not exceeds max_destroy_radius.(default 3)",
            "当炮弹击中方块，所能产生的损毁半径上限，默认为3")
        .defineInRange("max_destroy_radius", 3, 1, Double.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Integer> MAX_DESTROY_CNT = BUILDER
        .comment("The max count of blocks that a projectile can destroy(default 100)",
            "一个炮弹最大能摧毁多少方块，默认100个")
        .defineInRange("max_destroy_cnt", 100, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> HOT_X = BUILDER
        .comment("HitLoad Debug Value",
            "热重载预留值1")
        .define("hot_x", 0.0);
    private static final ForgeConfigSpec.ConfigValue<Double> HOT_Y = BUILDER
        .comment("HitLoad Debug Value",
            "热重载预留值2")
        .define("hot_y", 0.0);
    private static final ForgeConfigSpec.ConfigValue<Double> HOT_Z = BUILDER
        .comment("HitLoad Debug Value",
            "热重载预留值3")
        .define("hot_z", 0.0);

    private static final ForgeConfigSpec.ConfigValue<Boolean> DEBUG_ON =  BUILDER
        .comment("Should print debug info?",
            "开启debug信息游戏中可见")
        .define("debug_on", false);
    private static final ForgeConfigSpec.ConfigValue<Boolean> DEBUG_STACK_TRACE =  BUILDER
        .comment("Should print debug stack trace info?",
            "在日志输出debug栈信息")
        .define("debug_stack_trace", false);

    private static final ForgeConfigSpec.ConfigValue<Boolean> VS_SAFE_PHYS_THREAD = BUILDER
        .comment("VS phys safe thread",
            "瓦尔基里安全物理线程")
        .define("vs_phys_safe_thread", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int dockMaxLength;
    public static int dockMaxWidth;

    public static double rawFatalPPRatio;

    public static double shakeIntensity;
    public static int shakeTicks;

    public static double standardPropellantEnergy;

    public static double projectileRandomDisplacement;
    public static double projectileCollisionStep;

    public static double airDragFactor;

    public static int flyingProjectileLifeSpan;
    public static int stoppedProjectileLifeSpan;

    public static double maxDestroyRadius;
    public static double maxDestroyCnt;

    public static final Vector3d hotXYZ = new Vector3d();

    public static boolean debug_on;
    public static boolean debug_stack_trace;

    public static boolean vsPhysSafeThread;

    public static boolean isFatalKEOn() {
        return rawFatalPPRatio > 1.0;
    }

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
                .collect(Collectors.toSet());*/

        dockMaxLength = DOCK_MAX_LENGTH.get();
        dockMaxWidth = DOCK_MAX_WIDTH.get();
        rawFatalPPRatio = FATAL_KE_RATIO.get();

        shakeIntensity = SHAKE_INTENSITY.get();
        shakeTicks = SHAKE_TICKS.get();

        standardPropellantEnergy = STANDARD_PROPELLANT_ENERGY.get();

        projectileRandomDisplacement = PROJECTILE_RANDOM_DISPLACEMENT.get();

        projectileCollisionStep = PROJECTILE_COLLISION_STEP.get();

        airDragFactor = AIR_DRAG_FACTOR.get();

        flyingProjectileLifeSpan = FLYING_PROJECTILE_LIFE_SPAN.get();
        stoppedProjectileLifeSpan = STOPPED_PROJECTILE_LIFE_SPAN.get();

        maxDestroyRadius = MAX_DESTROY_RADIUS.get();
        maxDestroyCnt = MAX_DESTROY_CNT.get();

        hotXYZ.set(HOT_X.get(), HOT_Y.get(), HOT_Z.get());

        debug_on = DEBUG_ON.get();
        debug_stack_trace = DEBUG_STACK_TRACE.get();

        vsPhysSafeThread = VS_SAFE_PHYS_THREAD.get();
    }
}
