package com.lancas.vswap.content.info.block;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.mstandardized.Category;
import com.lancas.vswap.subproject.mstandardized.CategoryRegistry;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.BlockStateInfo;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WapBlockInfos {
    private static final Map<String, BlockInfo<?>> id2BlockInfo = new HashMap<>();
    public static void clearCache() {
        //id2BlockInfo.clear();
        id2BlockInfo.values().forEach(i -> i.idOrTagCache.clear());
    }
    //private static final Map<BiTuple<String, String>, Function<BlockState, Object>> blockInfoCache = new Hashtable<>();
    //private static final Map<BiTuple<String, String>, Function<BlockState, Object>> tagInfoCache = new Hashtable<>();


    public static double getValkrienMass(BlockState state) {
        if (state == null || state.isAir()) return 0;

        var valkyrienInfo = BlockStateInfo.INSTANCE.get(state);

        if (valkyrienInfo == null) {
            EzDebug.warn("no valkyrien info of block:" + StrUtil.getBlockName(state) + ", return mass 1000kg");
            return 1000;
        }

        Double mass = valkyrienInfo.getFirst();
        if (mass == null || mass.isInfinite() || mass.isNaN()) {
            EzDebug.warn("invalid mass in valkyrien cache:" + mass + " for block" + StrUtil.getBlockName(state) + ", return 1000kg");
            return 1000;
        }

        return mass;
    }

    private static final Function<String, String> grey = s -> "§7" + s;
    private static <T> Function<T, String> defFormatter() { return v -> v.toString(); };
    private static Function<Double, String> degreeFormatter() { return v -> StrUtil.F0(v) + "°"; }
    private static Function<Double, String> percentFormatter() { return v -> StrUtil.F0(v * 100) + "%"; }
    private static Function<Double, String> unitedFormatter(String unit, boolean canConvertToKJG) {
        return v -> {
            if (v == null)
                return "0" + unit;

            if (Double.isInfinite(v))
                return "∞";// + unit;

            if (canConvertToKJG) {
                if (v >= 1E9)
                    return (v / 1E9) + "G" + unit;
                if (v >= 1E6)
                    return (v / 1E6) + "M" + unit;
                if (v >= 1E3)
                    return (v / 1E3) + "K" + unit;
            }

            return v + unit;
        };
    };
    //use unit J now
    public static final BlockInfo<Double>
        Mass = new BlockInfoRegistry<Double>("mass").registryImplicit(Component.translatable("info.vswap.mass"), WapBlockInfos::getValkrienMass, unitedFormatter("Kg", false).andThen(grey)),

    //default 10KJ/xxx
        //hardness = BlockInfo.createAdvancedExplicit("hardness", state -> 10E3,  unitedFormatter("J/m", true).andThen(grey)),
        //toughness = BlockInfo.createAdvancedExplicit("toughness", state -> 10E3, unitedFormatter("J/m^3", true).andThen(grey)),
        ArmourRhae = new BlockInfoRegistry<Double>("armour_rhae").registryDefinedExplicit(Component.translatable("info.vswap.armour_rhae"), s -> 0.1, v -> StrUtil.F2(v) + "MCM"),
        ArmourAbsorbRatio = new BlockInfoRegistry<Double>("armour_ab_ratio").registryDefinedExplicit(Component.translatable("info.vswap.armour_ab_ratio"), s -> 1.0, StrUtil::F2),
        //drag_factor = new BlockInfoRegistry<Double>("drag_factor").registryDefinedExplicit(Component.translatable("info.vswap.drag_factor"), state -> 1.0,  unitedFormatter("", false).andThen(grey)),
        //ap_area = new BlockInfoRegistry<Double>("ap_area").registryDefinedAdvancedExplicit(Component.literal("info.vswap.ap_area"), state -> 1.0, unitedFormatter("m^2", false).andThen(grey)),

        Normalization = new BlockInfoRegistry<Double>("normalization").registryDefinedExplicit(Component.translatable("info.vswap.normalization"), state -> 0.0, percentFormatter()),
        CriticalDegree = new BlockInfoRegistry<Double>("critical_degree").registryDefinedExplicit(Component.translatable("info.vswap.critical_degree"), state -> 45.0, degreeFormatter()),
        PenetrationMultiplier = new BlockInfoRegistry<Double>("penetration_mul").registryDefinedExplicit(Component.translatable("info.vswap.penetration_mul"), state -> 1.0, StrUtil::F2),
        Spe_DestructionScalar = new BlockInfoRegistry<Double>("spe_destruction_scalar").registryDefinedExplicit(Component.translatable("info.vswap.ke_destruction_scalar"), state -> 1.0, StrUtil::F2),

        StdPropellantEnergy = new BlockInfoRegistry<Double>("std_propellant_energy").registryDefinedExplicit(Component.translatable("info.vswap.std_propellant"), state -> 0.0, StrUtil::F2)

        ;





    /*public static final List<BlockInfo<?>> values = List.of(
        mass,
        hardness, toughness, drag_factor, ap_area, oblique_degree, propellant_power
    );*/
    public @Nullable static BlockInfo<?> infoById(String id) {
        /*return switch (name) {
            case "mass" -> mass;
            case "hardness" -> hardness;
            case "toughness" -> toughness;
            case "drag_factor" -> drag_factor;
            case "ap_area" -> ap_area;
            case "oblique_degree" -> oblique_degree;
            case "propellant_power" -> propellant_power;

            //todo add more cases as soon as adding more info
            default -> null;
        };*/
        return id2BlockInfo.get(id);
    }


    /*public static class ImplicitBlockInfo<T> extends BlockInfo<T> {
        protected ImplicitBlockInfo(String inName, T inDefaultVal, Function<T, String> inFormatter) {
            super(inName, inDefaultVal, inFormatter);
        }

        @Override
        public BlockInfoCache getOrCreate(String blockID, Function<BlockState, T> getter) {
            return BlockInfoCache.getOrCreateImplicit(
                blockID,
                name,
                state -> getter.apply(state),
                val -> formatter.apply((T)val)
            );
        }
    }
    public static class ExplicitBlockInfo<T> extends BlockInfo<T> {
        private ExplicitBlockInfo(String inName, T inDefaultVal, Function<T, String> inFormatter) {
            super(inName, inDefaultVal, inFormatter);
        }

        public BlockInfoCache getOrCreate(String blockID, Function<BlockState, T> getter) {
            return BlockInfoCache.getOrCreateExplicit(
                blockID,
                name,
                state -> getter.apply(state),
                val -> formatter.apply((T)val)
            );
        }
    }*/
    public static class BlockInfoRegistry<T> {
        private final String blockInfoId;
        public BlockInfoRegistry(String id) {
            blockInfoId = id;
        }

        public BlockInfo<T> registry(Supplier<BlockInfo<T>> infoSupplier) {
            BlockInfo<T> info = infoSupplier.get();
            id2BlockInfo.put(blockInfoId, info);
            return info;
        }

        public BlockInfo<T> registryAdvancedExplicit(Component inDisplayName, Function<BlockState, T> inDefaultGetter, Function<T, String> inFormatter) {
            return registry(() -> new BlockInfo<T>(inDisplayName, inDefaultGetter, inFormatter, (hasVal, flag) -> flag.isAdvanced()));
        }
        public BlockInfo<T> registryAlwaysExplicit(Component inDisplayName, Function<BlockState, T> inDefaultGetter, Function<T, String> inFormatter) {
            return registry(() -> new BlockInfo<T>(inDisplayName, inDefaultGetter, inFormatter, (hasVal, flag) -> true));
        }
        public BlockInfo<T> registryImplicit(Component inDisplayName, Function<BlockState, T> inDefaultGetter, Function<T, String> inFormatter) {
            return registry(() -> new BlockInfo<T>(inDisplayName, inDefaultGetter, inFormatter, (hasVal, flag) -> false));
        }
        public BlockInfo<T> registryDefinedExplicit(Component inDisplayName, Function<BlockState, T> inDefaultGetter, Function<T, String> inFormatter) {
            return registry(() -> new BlockInfo<T>(inDisplayName, inDefaultGetter, inFormatter, (hasVal, flag) -> hasVal));
        }
        public BlockInfo<T> registryDefinedAdvancedExplicit(Component inDisplayName, Function<BlockState, T> inDefaultGetter, Function<T, String> inFormatter) {
            return registry(() -> new BlockInfo<T>(inDisplayName, inDefaultGetter, inFormatter, (hasVal, flag) -> hasVal && flag.isAdvanced()));
        }
    }
    public static class BlockInfo<T> {
        protected Map<String, Function<BlockState, T>> idOrTagCache = new HashMap<>();

        protected final Component displayName;
        protected final Function<BlockState, T> defaultGetter;
        public final Function<T, String> formatter;
        protected final BiPredicate<Boolean, TooltipFlag> explicit;

        public MutableComponent getDisplayName() {
            return displayName.copy();
        }
        public MutableComponent getDisplayValue(BlockState state) {
            return displayName.copy().append(": ").append(formatter.apply(valueOrDefaultOf(state)));
        }
        public MutableComponent getDisplayValue(T val) {
            return displayName.copy().append(": ").append(formatter.apply(val));
        }

        private BlockInfo(Component inDisplayName, Function<BlockState, T> inDefaultGetter, Function<T, String> inFormatter, BiPredicate<Boolean, TooltipFlag> inExplicit) {
            displayName = inDisplayName;
            defaultGetter = inDefaultGetter;
            formatter = inFormatter;
            explicit = inExplicit;
        }

        public boolean isExplicit(boolean hasVal, TooltipFlag flag) { return explicit.test(hasVal, flag); }


        public @NotNull T valueOrDefaultOf(BlockState state) {
            T valueOrNull = valueOrNullOf(state);
            return valueOrNull == null ? defaultGetter.apply(state) : valueOrNull;
        }
        public @Nullable T valueOrNullOf(BlockState state) {
            T blockVal = blockValueOrNullOf(state);
            if (blockVal != null)
                return blockVal;

            T tagVal = tagValueOrNullOf(state);
            if (tagVal != null)
                return tagVal;

            //return blockVal == null ? tagValueOrNullOf(state) : blockVal;
            return categoryValueOrNullOf(state);
        }

        private @Nullable T blockValueOrNullOf(BlockState state) {
            ResourceLocation location = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (location == null)
                return null;

            String blockID = location.toString();
            var getter = idOrTagCache.get(blockID);

            if (getter == null)
                return null;

            return getter.apply(state);
        }
        //todo prioperty tag
        private @Nullable T tagValueOrNullOf(BlockState state) {
            return state.getTags()
                .map(t -> {
                    if (t == null)
                        return null;
                    return idOrTagCache.get("#" + t.location().toString());
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(s -> null)
                .apply(state);
        }

        private @Nullable T categoryValueOrNullOf(BlockState state) {
            Category category =  CategoryRegistry.getCategory(state.getBlock());
            return Optional.ofNullable(idOrTagCache.get("*" + category.categoryName))
                .map(f -> f.apply(state))
                .orElse(null);
        }


        public void removeBlock(String blockID) {
            idOrTagCache.remove(blockID);
        }
        public void addBlock(String blockID, Function<BlockState, T> getter) {
            idOrTagCache.put(blockID, getter);
        }

        public void removeTag(TagKey<Block> tag) { idOrTagCache.remove("#" + tag.location().toString()); }
        public void addTag(TagKey<Block> tag, Function<BlockState, T> getter) { idOrTagCache.put("#" + tag.location().toString(), getter); }

        public void addCategory(String categoryName, Function<BlockState, T> getter) {
            if (categoryName.startsWith("*"))
                idOrTagCache.put(categoryName, getter);
            else
                idOrTagCache.put("*" + categoryName, getter);
        }
        public void removeCategory(String categoryName) {
            if (categoryName.startsWith("*"))
                idOrTagCache.remove(categoryName);
            else
                idOrTagCache.remove("*" + categoryName);
        }
    }

    //suppose stack is block item stack
    /*private static <T> Component getInfoTooltip(ItemStack stack, BlockInfo<T> info) {
        BlockState state = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
        T infoVal = info.valueOrDefaultOf(state);
        /.*StringBuilder textBuilder = new StringBuilder()
            .append(info.displayName)
            .append(": ")
            .append(info.formatter == null ? infoVal.toString() : info.formatter.apply(infoVal));*./
        MutableComponent component = info.displayName.copy()
            .append(": ")
            .append(info.formatter == null ? infoVal.toString() : info.formatter.apply(infoVal));

        return component;
    }*/

    @SubscribeEvent
    public static void registerTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return;

        BlockState defState = blockItem.getBlock().defaultBlockState();

        for (var value : id2BlockInfo.values()) {
            boolean hasVal = value.valueOrNullOf(defState) != null;
            if (!value.isExplicit(hasVal, event.getFlags())) continue;

            //event.getToolTip().add(getInfoTooltip(stack, value));
            event.getToolTip().add(value.getDisplayValue(defState));
        }
        //event.getToolTip().add(Component.literal("MASS:" + BlockStateInfo.INSTANCE.get(defState).getFirst()));
    }

    public static void register() {}
}
