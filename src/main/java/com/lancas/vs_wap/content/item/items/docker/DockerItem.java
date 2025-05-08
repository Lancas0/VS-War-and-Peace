package com.lancas.vs_wap.content.item.items.docker;

/*
import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.content.items.base.ShipInteractableItem;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.data.SectionShipSchemeData;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

public class DockerItem extends ShipInteractableItem {
    public static final String ID = "docker";
    public static final String DATA_TAG_KEY = "ship_data";
    public static final String HOLD_PIVOT_BP_OFFSET_TAG = "hold_pivot_bp_offset";
    public static final String HOLD_DIR_TAG = "hold_dir_bp";

    public DockerItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static void recordShipTo(ServerLevel level, ItemStack stack, ServerShip ship) {
        if (stack == null || ship == null) return;

        SectionShipSchemeData data = new SectionShipSchemeData().readShip(level, ship);
        stack.getOrCreateTag().put(DATA_TAG_KEY, data.saved());

        var holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable != null) {
            BlockPos centerInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
            NbtUtil.putBlockPos(stack, HOLD_PIVOT_BP_OFFSET_TAG, holdable.holdPivotBpInShip.toBp().subtract(centerInShip));
            NbtUtil.putEnum(stack, HOLD_DIR_TAG, holdable.forwardInShip);
        }
    }
    public static boolean hasShipData(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(DATA_TAG_KEY);
    }
    public static IShipSchemeData getShipSchemeData(ItemStack stack) {
        if (!hasShipData(stack)) return null;
        return new SectionShipSchemeData().load(stack.getTag().getCompound(DATA_TAG_KEY));
    }
    @Nullable
    public static HoldableAttachment applyHoldable(ItemStack stack, ShipBuilder shipBuilder) {
        if (!stack.getOrCreateTag().contains(HOLD_PIVOT_BP_OFFSET_TAG)) {
            EzDebug.log("ship has no holdable");
            return null;
        }

        BlockPos centerInShip = JomlUtil.bpContaining(shipBuilder.calUpdatedShipPos());
        BlockPos offset = NbtUtil.getBlockPos(stack, HOLD_PIVOT_BP_OFFSET_TAG);
        Direction dir = NbtUtil.getEnum(stack, HOLD_DIR_TAG, Direction.class);

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
    public static void clearShipData(ItemStack stack) {
        if (!hasShipData(stack)) return;
        stack.getOrCreateTag().remove(DATA_TAG_KEY);
    }


    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!(ship instanceof ServerShip sShip)) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        if (!hasShipData(stack))
            recordShipTo(sLevel, stack, sShip);
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

    public static ItemStack stackOfShip(ServerLevel level, @NotNull ServerShip ship) {
        ItemStack baseStack = WapItems.DOCKER.asStack();
        recordShipTo(level, baseStack, ship);
        return baseStack;
    }
    public static ServerShip makeShipFromStack(ServerLevel level, @NotNull ItemStack stack, Vector3dc pos, Vector3dc headWorldDir) {  //todo set direction
        if (!(stack.getItem() instanceof DockerItem)) {
            EzDebug.warn("item is not docker");
            return null;
        }

        IShipSchemeData shipSchemeData = getShipSchemeData(stack);
        if (shipSchemeData == null) {
            EzDebug.warn("shipSchemeData is null");
            return null;
        }

        ServerShip ship = shipSchemeData.createShip(level);
        ShipBuilder shipBuilder = ShipBuilder.modify(level, ship);
        return makeShipByBuilder(stack, shipBuilder, pos, headWorldDir);
    }
    public static ServerShip makeShipFromStackWithPool(ServerLevel level, @NotNull ItemStack stack, Vector3dc pos, Vector3dc headWorldDir) {
        if (!(stack.getItem() instanceof DockerItem)) return null;

        IShipSchemeData shipSchemeData = getShipSchemeData(stack);
        if (shipSchemeData == null) return null;

        ShipBuilder shipBuilder = ShipPool
            .getOrCreatePool(level)
            .getOrCreateEmptyShipBuilder();

        return makeShipByBuilder(stack, shipBuilder, pos, headWorldDir);
    }
    private static ServerShip makeShipByBuilder(@NotNull ItemStack stack, ShipBuilder builder, Vector3dc pos, Vector3dc headWorldDir) {
        IShipSchemeData shipSchemeData = getShipSchemeData(stack);
        if (shipSchemeData == null) return null;

        builder.overwriteByScheme(shipSchemeData);

        Matrix4dc shipToWorld = builder.get().getShipToWorld();
        var holdable = applyHoldable(stack, builder);

        EzDebug.log("pos:" + StrUtil.F2(pos) + ", headDir:" + StrUtil.F2(headWorldDir));

        return builder.doIfElse(
            self -> holdable != null,
            self -> {
                Vector3d worldForward = JomlUtil.dWorldNormal(shipToWorld, holdable.forwardInShip);
                Quaterniond rotation = worldForward.rotationTo(headWorldDir, new Quaterniond());
                self.rotate(rotation).moveShipPosToWorldPos(JomlUtil.dCenter(holdable.holdPivotBpInShip.toBp()), pos);
            },
            self -> self.setWorldPos(pos)  //todo do direciton
        ).get();
    }

    @Override
    public InteractionResult onItemNotUseOnShip(ItemStack stack, Level level, Player player, UseOnContext ctx) {
        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        //only when press shift can make a ship

        IShipSchemeData shipSchemeData = getShipSchemeData(stack);
        if (shipSchemeData == null) return InteractionResult.FAIL;

        //todo use makeShipFromStack
        Direction playerLookDir = player.getDirection();

        BlockPos useOn = ctx.getClickedPos();
        Direction normal = ctx.getClickedFace();

        //ShipBuilder shipBuilder = new ShipBuilder(useOn, sLevel, shipSchemeData)
        //    .moveFaceTo(normal.getOpposite(), useOn.relative(normal).getCenter());

        //applyHoldable(stack, shipBuilder);

        //EzDebug.log("ship worldPos:" + shipBuilder.get().getTransform().getPositionInWorld());
        //HoldableAttachment.rotateDirectionToUp()

        ServerShip ship = shipSchemeData.createShip(sLevel);
        ShipBuilder shipBuilder = ShipBuilder.modify(sLevel, ship)
            .moveFaceTo(normal.getOpposite(), useOn.relative(normal).getCenter());

        applyHoldable(stack, shipBuilder);
        /.*
        ((ShipDataCommon)ship).setTransform(
            new ShipTransformImpl(
                JomlUtil.d(player.position().add(0, 3, 0)),
                ship.getTransform().getPositionInShip(),
                new Quaterniond(),
                new Vector3d(1, 1, 1)
            )
        );
        *./
        //ship.saveAttachment(DebugPosAtt.class, new DebugPosAtt());

        return InteractionResult.CONSUME;
    }
}
*/