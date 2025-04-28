package com.lancas.vs_wap.content.items.docker;

import com.lancas.vs_wap.content.items.base.ShipInteractableItem;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

public class ShipDataDocker extends ShipInteractableItem implements IDocker {
    public ShipDataDocker(Properties p_41383_) {
        super(p_41383_);
    }

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

    @Override
    public ShipBuilder makeShipBuilderFromStack(ServerLevel level, ItemStack stack) {
        //if (!(stack.getItem() instanceof ShipDataDocker)) return null;
        //if (!stack.getOrCreateTag().contains("ship_data")) return null;
        //CompoundTag shipDataNbt = stack.getOrCreateTag().getCompound("ship_data");
        //IShipSchemeData shipData = new RAChunkyShipSchemeData().load(shipDataNbt);
        RRWChunkyShipSchemeData shipData = getShipSchemeData(stack);
        if (shipData == null) return null;

        ShipBuilder shipBuilder = ShipPool
            .getOrCreatePool(level)
            .getOrCreateEmptyShipBuilder()
            .overwriteByScheme(shipData);

        //Matrix4dc shipToWorld = shipBuilder.get().getShipToWorld();
        applyHoldable(stack, shipBuilder);

        /*return shipBuilder.doIfElse(
            self -> holdable != null,
            self -> {
                Vector3d worldForward = JomlUtil.dWorldNormal(shipToWorld, holdable.forwardInShip);
                Quaterniond rotation = worldForward.rotationTo(headWorldDir, new Quaterniond());
                self.rotate(rotation).moveShipPosToWorldPos(JomlUtil.dCenter(holdable.holdPivotBpInShip.toBp()), pos);
            },
            self -> self.setWorldPos(pos)  //todo do direciton
        ).get();*/
        return shipBuilder;
    }

    public RRWChunkyShipSchemeData getShipSchemeData(ItemStack stack) {
        if (!(stack.getItem() instanceof ShipDataDocker)) return null;
        if (!stack.getOrCreateTag().contains("ship_data")) return null;

        CompoundTag shipDataNbt = stack.getOrCreateTag().getCompound("ship_data");
        return new RRWChunkyShipSchemeData().load(shipDataNbt);
    }

    @Override
    public ItemStack saveShipToStack(ServerLevel level, ServerShip ship, ItemStack stack) {
        if (stack == null || ship == null) return ItemStack.EMPTY;

        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData().readShip(level, ship);
        stack.getOrCreateTag().put("ship_data", data.saved());

        var holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable != null) {
            BlockPos centerInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
            NbtUtil.putBlockPos(stack, "hold_pivot", holdable.holdPivotBpInShip.toBp().subtract(centerInShip));
            NbtUtil.putEnum(stack, "hold_forward", holdable.forwardInShip);
        }
        return stack;
    }



    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!(ship instanceof ServerShip sShip)) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        if (!stack.getOrCreateTag().contains("ship_data"))
            saveShipToStack(sLevel, sShip, stack);
        else {
            //place ship loc to world
            Direction normal = ctx.getClickedFace();
            BlockPos locCenter = ctx.getClickedPos().relative(normal);

            Vector3d worldForward =
                ship.getShipToWorld().transformDirection(new Vector3d(0, 0, 1)).normalize();  //todo rotate according to player place dir
            Vector3d worldCenter =
                ship.getShipToWorld().transformPosition(JomlUtil.dCenter(locCenter));

            ServerShip newShip = getShipSchemeData(stack).createShip(sLevel);
            ShipBuilder shipBuilder = ShipBuilder.modify(sLevel, newShip)
                .rotateForwardTo(worldForward)
                .moveFaceTo(normal.getOpposite(), worldCenter);

            applyHoldable(stack, shipBuilder);
        }
        return InteractionResult.CONSUME;
    }
    /*@Override
    public InteractionResult onItemNotUseOnShip(ItemStack stack, Level level, Player player, UseOnContext ctx) {
        EzDebug.log("docker not use on ship");

        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;  //only when press shift can make a ship


        IShipSchemeData shipSchemeData = getShipSchemeData(stack);
        EzDebug.log("shipSchemeData is null?" + (shipSchemeData == null));
        if (shipSchemeData == null) {
            EzDebug.warn("fail to get ship scheme data!");
            return InteractionResult.FAIL;
        }

        EzDebug.log("making ship");

        //todo use makeShipFromStack
        //Direction playerLookDir = player.getDirection();

        BlockPos useOn = ctx.getClickedPos();
        Direction normal = ctx.getClickedFace();

        ServerShip ship = shipSchemeData.createShip(sLevel);
        ShipBuilder shipBuilder = ShipBuilder.modify(sLevel, ship)
            .moveFaceTo(normal.getOpposite(), useOn.relative(normal).getCenter());

        applyHoldable(stack, shipBuilder);

        ShipUtil.foreachBlock(shipBuilder.get(), sLevel, (pos, state, be) -> {
            EzDebug.log("ship has block:" + StrUtil.poslike(pos) + ", " + StrUtil.getBlockName(state));
        });

        return InteractionResult.CONSUME;
    }*/
}
