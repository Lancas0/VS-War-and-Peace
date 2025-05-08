package com.lancas.vs_wap.content.item.items.docker;

import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.ship.data.IShipSchemeRandomReader;
import com.lancas.vs_wap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.renderer.DockerItemRenderProperty;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.UUID;
import java.util.function.Consumer;

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
        return refDocker.saveShip(level, ship, stack);
    }

    public RefWithFallbackDocker(Properties p_41383_) {
        super(p_41383_);
    }

    /*private @Nullable RRWChunkyShipSchemeData getFallbackData(ItemStack stack) {

    }*/

    //todo shrink stack?
    @Override
    public ShipBuilder makeShipBuilder(ServerLevel level, ItemStack stack) {
        CompoundTag dockerNbt = stack.getOrCreateTag();
        if (!dockerNbt.contains("ship_id")/* || !dockerNbt.contains("fallback_data")*/) return null;

        long shipId = stack.getOrCreateTag().getLong("ship_id");
        //ServerShip ship = ShipUtil.getServerShipByID(level, shipId);
        ServerShip showShip = ShipPool.getOrCreatePool(level).showShip(shipId);

        if (showShip != null) {
            return ShipBuilder.modify(level, showShip);
        }

        //fallback : use data
        /*if (!stack.getOrCreateTag().contains("fallback_data")) return null;
        CompoundTag fallbackDataNbt = stack.getOrCreateTag().getCompound("fallback_data");
        RRWChunkyShipSchemeData fallbackData = new RRWChunkyShipSchemeData().load(fallbackDataNbt);

        if (fallbackData == null) return null;*/
        IShipSchemeData fallbackData = getShipData(stack);
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
    public ItemStack saveShip(ServerLevel level, ServerShip ship, ItemStack stack) {
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

    @Override
    public boolean hasShipData(ItemStack stack) {
        if (!(stack.getItem() instanceof RefWithFallbackDocker)) return false;

        CompoundTag stackNbt = stack.getOrCreateTag();
        return stackNbt.contains("ship_id") && stackNbt.contains("fallback_data");
    }

    @Override
    @Nullable
    public IShipSchemeData getShipData(ItemStack stack) {
        CompoundTag dockerNbt = stack.getOrCreateTag();
        if (!dockerNbt.contains("fallback_data")) return null;
        CompoundTag fallbackDataNbt = stack.getOrCreateTag().getCompound("fallback_data");

        return new RRWChunkyShipSchemeData().load(fallbackDataNbt);
    }


    @Override
    public @Nullable UUID getOrCreateDockerUuidIfHasData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        if (!(stack.getItem() instanceof RefWithFallbackDocker)) return null;
        if (!stackNbt.contains("fallback_data")) return null;

        if (stackNbt.contains("uuid")) return stackNbt.getUUID("uuid");
        else {
            UUID newUuid = UUID.randomUUID();
            stackNbt.putUUID("uuid", newUuid);
            return newUuid;
        }
    }

    @Override
    public @Nullable IShipSchemeRandomReader getShipDataReader(ItemStack stack) {
        RRWChunkyShipSchemeData shipData = (RRWChunkyShipSchemeData)getShipData(stack);
        return shipData == null ? null : shipData.getRandomReader();
    }

    //so can be rendered by dockerItemRender
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new DockerItemRenderProperty());
    }
}
