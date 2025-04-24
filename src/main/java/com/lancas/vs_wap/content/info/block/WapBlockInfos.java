package com.lancas.vs_wap.content.info.block;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.valkyrienskies.mod.common.BlockStateInfo;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WapBlockInfos {
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
        mass = BlockInfo.createImplicit("mass", WapBlockInfos::getValkrienMass, unitedFormatter("Kg", false).andThen(grey)),

    //default 10KJ/xxx
        hardness = BlockInfo.createAdvancedExplicit("hardness", state -> 10E3,  unitedFormatter("J/m", true).andThen(grey)),
        toughness = BlockInfo.createAdvancedExplicit("toughness", state -> 10E3, unitedFormatter("J/m^3", true).andThen(grey)),

        drag_factor = BlockInfo.createAdvancedExplicit("drag factor", state -> 1.0,  unitedFormatter("", false).andThen(grey)),
        ap_area = BlockInfo.createDefinedAdvancedExplicit("penetrate area", state -> 1.0, unitedFormatter("m^2", false).andThen(grey)),

        oblique_degree = BlockInfo.createDefinedAdvancedExplicit("oblique angle", state -> 0.0, unitedFormatter("°", false).andThen(grey)),
        propellant_power = BlockInfo.createDefinedAdvancedExplicit("propellant power", state -> 0.0, unitedFormatter("J", true).andThen(grey));


    public static final List<BlockInfo<?>> values = List.of(
        mass,
        hardness, toughness, drag_factor, ap_area, oblique_degree, propellant_power
    );
    public static BlockInfo<?> valueOf(String name) {
        return switch (name) {
            case "mass" -> mass;
            case "hardness" -> hardness;
            case "toughness" -> toughness;
            case "drag_factor" -> drag_factor;
            case "ap_area" -> ap_area;
            case "oblique_degree" -> oblique_degree;
            case "propellant_power" -> propellant_power;

            //todo add more cases as soon as adding more info
            default -> null;
        };
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
    public static class BlockInfo<T> {
        private static final Map<BiTuple<String, String>, Function<BlockState, Object>> blockInfoCache = new Hashtable<>();
        private static final Map<BiTuple<String, String>, Function<BlockState, Object>> tagInfoCache = new Hashtable();

        protected final String name;
        protected final Function<BlockState, T> defaultGetter;
        protected final Function<T, String> formatter;
        protected final BiPredicate<Boolean, TooltipFlag> explicit;

        private BlockInfo(String inName, Function<BlockState, T> inDefaultGetter, Function<T, String> inFormatter, BiPredicate<Boolean, TooltipFlag> inExplicit) {
            name = inName;
            defaultGetter = inDefaultGetter;
            formatter = inFormatter;
            explicit = inExplicit;
        }
        protected static <TT> BlockInfo<TT> createAdvancedExplicit(String inName, Function<BlockState, TT> inDefaultGetter, Function<TT, String> inFormatter) {
            return new BlockInfo<TT>(inName, inDefaultGetter, inFormatter, (hasVal, flag) -> flag.isAdvanced());
        }
        protected static <TT> BlockInfo<TT> createAlwaysExplicit(String inName, Function<BlockState, TT> inDefaultGetter, Function<TT, String> inFormatter) {
            return new BlockInfo<TT>(inName, inDefaultGetter, inFormatter, (hasVal, flag) -> true);
        }
        protected static <TT> BlockInfo<TT> createImplicit(String inName, Function<BlockState, TT> inDefaultGetter, Function<TT, String> inFormatter) {
            return new BlockInfo<TT>(inName, inDefaultGetter, inFormatter, (hasVal, flag) -> false);
        }
        protected static <TT> BlockInfo<TT> createDefinedExplicit(String inName, Function<BlockState, TT> inDefaultGetter, Function<TT, String> inFormatter) {
            return new BlockInfo<TT>(inName, inDefaultGetter, inFormatter, (hasVal, flag) -> hasVal);
        }
        protected static <TT> BlockInfo<TT> createDefinedAdvancedExplicit(String inName, Function<BlockState, TT> inDefaultGetter, Function<TT, String> inFormatter) {
            return new BlockInfo<TT>(inName, inDefaultGetter, inFormatter, (hasVal, flag) -> hasVal && flag.isAdvanced());
        }

        public boolean isExplicit(boolean hasVal, TooltipFlag flag) { return explicit.test(hasVal, flag); }


        public T valueOrDefaultOf(BlockState state) {
            T valueOrNull = valueOrNullOf(state);
            return valueOrNull == null ? defaultGetter.apply(state) : valueOrNull;
        }
        public T valueOrNullOf(BlockState state) {
            T blockVal = blockValueOrNullOf(state);
            return blockVal == null ? tagValueOrNullOf(state) : blockVal;
        }

        private T blockValueOrNullOf(BlockState state) {
            ResourceLocation location = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (location == null)
                return null;

            String blockID = location.toString();
            var getter = blockInfoCache.get(new BiTuple<>(blockID, name));

            if (getter == null)
                return null;

            Object val = getter.apply(state);
            if (val == null)
                return null;
            return (T)val;
        }
        //todo prioperty tag
        private T tagValueOrNullOf(BlockState state) {
            final Object[] value = new Object[] { null };

            state.getTags().forEach(
                tag -> {
                    if (tag == null || value[0] != null) return;

                    var getter = tagInfoCache.get(new BiTuple<>(tag.location().toString(), name));
                    if (getter != null)
                        value[0] = getter.apply(state);
                }
            );

            return (T)value[0];
        }

        public void removeBlock(String blockID) {
            blockInfoCache.remove(new BiTuple<>(blockID, name));
        }
        /*public static void removeBlock(String blockID, BlockInfo<?> info) {
            info.removeBlock(blockID);
        }*/

        public void addBlock(String blockID, Function<BlockState, T> getter) {
            blockInfoCache.put(new BiTuple<>(blockID, name), getter::apply);
        }
        /*public static <TT> void addBlock(String blockID, BlockInfo<TT> info, Function<BlockState, TT> getter) {
            info.addBlock(blockID, getter);
        }*/

        public void removeTag(String tag) {
            tagInfoCache.remove(new BiTuple<>(tag, name));
        }
        /*public static void removeTag(String tag, BlockInfo<?> info) {
            info.removeTag(tag);
        }*/

        public void addTag(String tag, Function<BlockState, T> getter) {
            tagInfoCache.put(new BiTuple<>(tag, name), getter::apply);
        }
    }

    //suppose stack is block item stack
    private static <T> Component getInfoTooltip(ItemStack stack, BlockInfo<T> info) {
        BlockState state = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
        T infoVal = info.valueOrDefaultOf(state);
        StringBuilder textBuilder = new StringBuilder()
            .append(info.name)
            .append(": ")
            .append(info.formatter == null ? infoVal.toString() : info.formatter.apply(infoVal));

        return Component.literal(textBuilder.toString());
    }

    @SubscribeEvent
    public static void registerTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return;

        BlockState defState = blockItem.getBlock().defaultBlockState();

        for (var value : values) {
            boolean hasVal = value.valueOrNullOf(defState) != null;
            if (!value.isExplicit(hasVal, event.getFlags())) continue;

            event.getToolTip().add(getInfoTooltip(stack, value));
        }
        //event.getToolTip().add(Component.literal("MASS:" + BlockStateInfo.INSTANCE.get(defState).getFirst()));
    }
}
