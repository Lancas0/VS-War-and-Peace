package com.lancas.vs_wap.content.item.items.docker;

import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.ship.data.IShipSchemeRandomReader;
import com.lancas.vs_wap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.renderer.docker.DockerItemRenderProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.Hashtable;
import java.util.UUID;
import java.util.function.Consumer;

public class RefWithFallbackDocker extends Item implements IDocker {
    private static final int COUNTING_DOWN = 3 * 60 * 20 - 1;//3 minus
    public static Hashtable<Long, BiTuple<ServerLevel, Integer>> countingDown = new Hashtable<>();

    /*@Nullable
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
    }*/

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
            countingDown.remove(showShip.getId());  //stop remove counting down
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
        //applyHoldable(stack, shipBuilder);
        return shipBuilder;
    }

    @Override
    public ItemStack saveShip(ServerLevel level, ServerShip ship, ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        stackNbt.putLong("ship_id", ship.getId());
        ShipPool.getOrCreatePool(level).hideShip(ship, ShipPool.HideType.StaticAndInvisible);

        RRWChunkyShipSchemeData fallbackData = new RRWChunkyShipSchemeData().readShip(level, ship);
        stackNbt.put("fallback_data", fallbackData.saved());

        /*var holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable != null) {
            BlockPos centerInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
            NbtBuilder.modify(stackNbt)
                .putBlockPos("hold_pivot", holdable.holdPivotBpInShip.toBp().subtract(centerInShip))
                .putEnum("hold_forward", holdable.forwardInShip);
            //NbtUtil.putBlockPos(stack, "hold_pivot", holdable.holdPivotBpInShip.toBp().subtract(centerInShip));
            //NbtUtil.putEnum(stack, "hold_forward", holdable.forwardInShip);
        }*/

        countingDown.put(ship.getId(), new BiTuple<>(level, COUNTING_DOWN));

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
    /*@Override
    public @Nullable Vector3ic getLocalPivot(ItemStack stack) {
        NbtBuilder nbtBuilder = NbtBuilder.modify(stack.getOrCreateTag());
        if (nbtBuilder.contains("hold_pivot"))
            return JomlUtil.i(nbtBuilder.getBlockPos("hold_pivot"));
        return null;
    }
    @Override
    public @Nullable Vector3ic getLocalHoldForward(ItemStack stack) {
        NbtBuilder nbtBuilder = NbtBuilder.modify(stack.getOrCreateTag());
        if (nbtBuilder.contains("hold_forward"))
            return JomlUtil.iNormal(nbtBuilder.getEnum("hold_forward", Direction.class));
        return null;
    }*/

    public long getVsShipId(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        return stackNbt.getLong("ship_id");
    }

    //so can be rendered by dockerItemRender
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new DockerItemRenderProperty());
    }
}
