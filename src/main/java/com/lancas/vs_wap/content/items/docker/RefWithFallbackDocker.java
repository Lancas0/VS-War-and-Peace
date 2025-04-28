package com.lancas.vs_wap.content.items.docker;

import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

public class RefWithFallbackDocker extends Item implements IDocker {
    @Nullable
    public static HoldableAttachment applyHoldable(ItemStack stack, ShipBuilder shipBuilder) {
        if (!stack.getOrCreateTag().contains("hold_pivot")) {
            EzDebug.log("ship has no holdable");
            return null;
        }

        BlockPos centerInShip = JomlUtil.bpContaining(shipBuilder.calUpdatedShipPos());
        BlockPos offset = NbtUtil.getBlockPos(stack, "hold_pivot");
        Direction dir = NbtUtil.getEnum(stack, "hold_forward", Direction.class);

        if (offset == null || dir == null) {
            EzDebug.log("offset:" + offset + ", dir:" + dir);
            return null;
        }

        EzDebug.log("bp:" + centerInShip.offset(offset) + ", dir:" + dir);
        return HoldableAttachment.apply(
            shipBuilder.get(),
            centerInShip.offset(offset),
            dir
        );
    }

    public static ItemStack stackOf(ServerLevel level, ServerShip ship) {
        ItemStack stack = WapItems.Docker.REF_WITH_FALLBACK_DOCKER.asStack();
        RefWithFallbackDocker refDocker = (RefWithFallbackDocker)stack.getItem();
        return refDocker.saveShipToStack(level, ship, stack);
    }

    public RefWithFallbackDocker(Properties p_41383_) {
        super(p_41383_);
    }

    //todo shrink stack?
    @Override
    public ShipBuilder makeShipBuilderFromStack(ServerLevel level, ItemStack stack) {
        if (!stack.getOrCreateTag().contains("ship_id")) return null;

        long shipId = stack.getOrCreateTag().getLong("ship_id");
        //ServerShip ship = ShipUtil.getServerShipByID(level, shipId);
        ServerShip showShip = ShipPool.getOrCreatePool(level).showShip(shipId);

        if (showShip != null) {
            return ShipBuilder.modify(level, showShip);
        }

        //fallback : use data
        if (!stack.getOrCreateTag().contains("fallback_data")) return null;
        CompoundTag fallbackDataNbt = stack.getOrCreateTag().getCompound("fallback_data");
        RRWChunkyShipSchemeData fallbackData = new RRWChunkyShipSchemeData().load(fallbackDataNbt);

        if (fallbackData == null) return null;

        ShipBuilder shipBuilder = ShipPool
            .getOrCreatePool(level)        //todo should I use pool?
            .getOrCreateEmptyShipBuilder()
            .overwriteByScheme(fallbackData);

        //Matrix4dc shipToWorld = shipBuilder.get().getShipToWorld();
        applyHoldable(stack, shipBuilder);
        return shipBuilder;
    }

    @Override
    public ItemStack saveShipToStack(ServerLevel level, ServerShip ship, ItemStack stack) {
        stack.getOrCreateTag().putLong("ship_id", ship.getId());
        ShipPool.getOrCreatePool(level).hideShip(ship, ShipPool.HideType.StaticAndInvisible);

        RRWChunkyShipSchemeData fallbackData = new RRWChunkyShipSchemeData().readShip(level, ship);
        stack.getOrCreateTag().put("fallback_data", fallbackData.saved());

        var holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable != null) {
            BlockPos centerInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
            NbtUtil.putBlockPos(stack, "hold_pivot", holdable.holdPivotBpInShip.toBp().subtract(centerInShip));
            NbtUtil.putEnum(stack, "hold_forward", holdable.forwardInShip);
        }

        return stack;
    }
}
