package com.lancas.vs_wap.content.item.items;


import com.lancas.vs_wap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.valkyrienskies.core.api.ships.ServerShip;

public class GreenPrint extends Item {
    public GreenPrint(Properties p_41383_) {
        super(p_41383_);
    }

    /*@Nullable
    public static RRWChunkyShipSchemeData getSchemeData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (!stackNbt.contains("green_print_data")) return null;
        return new RRWChunkyShipSchemeData().load(stackNbt.getCompound("green_print_data"));
    }
    @NotNull
    public static RRWChunkyShipSchemeData getOrCreateSchemeData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (!stackNbt.contains("green_print_data")) return null;
        return new RRWChunkyShipSchemeData().load(stackNbt.getCompound("green_print_data"));
    }*/
    /*public static void scheduleBlockChange(ItemStack stack, BlockPos localBp, BlockState state, @Nullable CompoundTag beNbt) {
        NbtBuilder nbt = NbtBuilder.modify(stack.getOrCreateTag());
        nbt.putCompound(localBp.toShortString(), NbtBuilder.tagOfBlock(localBp, state, beNbt));
    }
    public static RRWChunkyShipSchemeData getOrCreateFlushedSchemeData(ItemStack stack) {
        NbtBuilder nbt = NbtBuilder.modify(stack.getOrCreateTag());

        RRWChunkyShipSchemeData data;
        if (!nbt.get().contains("scheme_data")) {
            data = new RRWChunkyShipSchemeData();
        } else
            data = new RRWChunkyShipSchemeData().load(nbt.getCompound("scheme_data"));

        var nbtKeyIt = nbt.get().getAllKeys().iterator();
        while (nbtKeyIt.hasNext()) {
            String key = nbtKeyIt.next();
            if (key.equals("scheme_data")) continue;

            CompoundTag blockTag = nbt.get().getCompound(key);
            NbtBuilder.blockOfTagDo(blockTag, data::setBlockAtLocalBp);  //will properly handle air blockState
            nbtKeyIt.remove();
        }

        stack.getOrCreateTag().put("scheme_data", data.saved());
        return data;
    }*/

    public static RRWChunkyShipSchemeData getOrCreateSchemeData(ItemStack stack) {
        NbtBuilder nbt = NbtBuilder.modify(stack.getOrCreateTag());

        RRWChunkyShipSchemeData data;
        if (!nbt.contains("scheme_data")) {
            data = new RRWChunkyShipSchemeData();
            nbt.putCompound("scheme_data", data.saved());
        } else
            data = new RRWChunkyShipSchemeData().load(nbt.getCompound("scheme_data"));
        /*var nbtKeyIt = nbt.get().getAllKeys().iterator();
        while (nbtKeyIt.hasNext()) {
            String key = nbtKeyIt.next();
            if (key.equals("scheme_data")) continue;

            CompoundTag blockTag = nbt.get().getCompound(key);
            NbtBuilder.blockOfTagDo(blockTag, data::setBlockAtLocalBp);  //will properly handle air blockState
            nbtKeyIt.remove();
        }

        stack.getOrCreateTag().put("scheme_data", data.saved());*/
        return data;
    }
    public static ItemStack readShipTo(ItemStack stack, ServerLevel level, ServerShip ship) {
        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData().readShip(level, ship);
        stack.getOrCreateTag().put("scheme_data", data.saved());
        return stack;
    }


}
