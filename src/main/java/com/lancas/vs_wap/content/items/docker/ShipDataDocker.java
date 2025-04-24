package com.lancas.vs_wap.content.items.docker;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.ship.data.SectionShipSchemeData;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.valkyrienskies.core.api.ships.ServerShip;

public class ShipDataDocker extends Item implements IDocker {
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
        if (!(stack.getItem() instanceof DockerItem)) return null;

        IShipSchemeData shipSchemeData = SectionShipSchemeData.fromCompound(stack.getOrCreateTag().getCompound("ship_data"));
        if (shipSchemeData == null) return null;

        ShipBuilder shipBuilder = ShipPool
            .getOrCreatePool(level)
            .getOrCreateEmptyShipBuilder()
            .overwriteByScheme(shipSchemeData);

        Matrix4dc shipToWorld = shipBuilder.get().getShipToWorld();
        var holdable = applyHoldable(stack, shipBuilder);

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

    @Override
    public ItemStack saveShipToStack(ServerLevel level, ServerShip ship, ItemStack stack) {
        if (stack == null || ship == null) return ItemStack.EMPTY;

        SectionShipSchemeData data = new SectionShipSchemeData().readShip(level, ship);
        stack.getOrCreateTag().put("ship_data", data.toCompound());

        var holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable != null) {
            BlockPos centerInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
            NbtUtil.putBlockPos(stack, "hold_pivot", holdable.holdPivotBpInShip.toBp().subtract(centerInShip));
            NbtUtil.putEnum(stack, "hold_forward", holdable.forwardInShip);
        }
        return stack;
    }
}
