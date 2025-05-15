package com.lancas.vs_wap.content.item.items.docker;

import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.content.item.items.base.ShipInteractableItem;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.ship.data.IShipSchemeRandomReader;
import com.lancas.vs_wap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.renderer.docker.DockerItemRenderer;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.UUID;
import java.util.function.Consumer;

public class ShipDataDocker extends ShipInteractableItem implements IDocker {
    public ShipDataDocker(Properties p_41383_) {
        super(p_41383_);
    }


    public static ItemStack stackOfVs(ServerLevel level, ServerShip ship) {
        ItemStack stack = WapItems.Docker.SHIP_DATA_DOCKER.asStack();
        ShipDataDocker docker = (ShipDataDocker)stack.getItem();
        return docker.saveShip(level, ship, stack);
    }
    public static ItemStack stackOfSa(ServerLevel level, ISandBoxShip ship) {
        ItemStack stack = WapItems.Docker.SHIP_DATA_DOCKER.asStack();
        //ShipDataDocker docker = (ShipDataDocker)stack.getItem();
        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData();
        int addY = (level.getHeight() + level.getMinBuildHeight()) / 2 + (int)(JomlUtil.lengthY(ship.getBlockCluster().getDataReader().getLocalAABB()) / 2);

        ship.getBlockCluster().getDataReader().allBlocks().forEach(e -> {
            data.setBlockAtLocalBp(JomlUtil.bp(e.getKey()).offset(0, addY, 0), e.getValue(), null);  //todo now can't save be if use sa ship
        });
        stack.getOrCreateTag().put("ship_data", data.saved());
        //todo now can't save att if use sa ship
        return stack;
    }

    /*@Nullable
    public HoldableAttachment applyHoldable(ItemStack stack, ShipBuilder shipBuilder) {
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

    @Override
    public ShipBuilder makeShipBuilder(ServerLevel level, ItemStack stack) {
        //if (!(stack.getItem() instanceof ShipDataDocker)) return null;
        //if (!stack.getOrCreateTag().contains("ship_data")) return null;
        //CompoundTag shipDataNbt = stack.getOrCreateTag().getCompound("ship_data");
        //IShipSchemeData shipData = new RAChunkyShipSchemeData().load(shipDataNbt);
        IShipSchemeData shipData = getShipData(stack);
        if (shipData == null) return null;

        //Matrix4dc shipToWorld = shipBuilder.get().getShipToWorld();
        //applyHoldable(stack, shipBuilder);

        /*return shipBuilder.doIfElse(
            self -> holdable != null,
            self -> {
                Vector3d worldForward = JomlUtil.dWorldNormal(shipToWorld, holdable.forwardInShip);
                Quaterniond rotation = worldForward.rotationTo(headWorldDir, new Quaterniond());
                self.rotate(rotation).moveShipPosToWorldPos(JomlUtil.dCenter(holdable.holdPivotBpInShip.toBp()), pos);
            },
            self -> self.setWorldPos(pos)  //todo do direciton
        ).get();*/
        return ShipPool
            .getOrCreatePool(level)
            .getOrCreateEmptyShipBuilder()
            .overwriteByScheme(shipData);
    }
    @Override
    public ItemStack saveShip(ServerLevel level, ServerShip ship, ItemStack stack) {
        if (stack == null || ship == null) return ItemStack.EMPTY;
        CompoundTag stackNbt = stack.getOrCreateTag();

        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData().readShip(level, ship);
        stackNbt.put("ship_data", data.saved());

        /*var holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable != null) {
            BlockPos centerInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
            NbtBuilder.modify(stackNbt)
                .putBlockPos("hold_pivot", holdable.holdPivotBpInShip.toBp().subtract(centerInShip))
                .putEnum("hold_forward", holdable.forwardInShip);
            //NbtUtil.putBlockPos(stack, "hold_pivot", holdable.holdPivotBpInShip.toBp().subtract(centerInShip));
            //NbtUtil.putEnum(stack, "hold_forward", holdable.forwardInShip);
        }*/
        return stack;
    }

    @Override
    public boolean hasShipData(ItemStack stack) {
        return (stack.getItem() instanceof ShipDataDocker) && (stack.getOrCreateTag().contains("ship_data"));
    }

    @Override
    @Nullable
    public IShipSchemeData getShipData(ItemStack stack) {
        if (!(stack.getItem() instanceof ShipDataDocker)) return null;
        if (!stack.getOrCreateTag().contains("ship_data")) return null;

        CompoundTag shipDataNbt = stack.getOrCreateTag().getCompound("ship_data");
        return new RRWChunkyShipSchemeData().load(shipDataNbt);
    }

    @Override
    public @Nullable UUID getOrCreateDockerUuidIfHasData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        if (!(stack.getItem() instanceof ShipDataDocker)) return null;
        if (!stackNbt.contains("ship_data")) return null;

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


    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!(ship instanceof ServerShip sShip)) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        IShipSchemeData shipData = getShipData(stack);

        if (shipData == null)  //don't have shipData
            saveShip(sLevel, sShip, stack);
        else {
            //place ship loc to world
            Direction normal = ctx.getClickedFace();
            BlockPos locCenter = ctx.getClickedPos().relative(normal);

            Vector3d worldForward =
                ship.getShipToWorld().transformDirection(new Vector3d(0, 0, 1)).normalize();  //todo rotate according to player place dir
            Vector3d worldCenter =
                ship.getShipToWorld().transformPosition(JomlUtil.dCenter(locCenter));

            ServerShip newShip = shipData.createShip(sLevel);
            ShipBuilder shipBuilder = ShipBuilder.modify(sLevel, newShip)
                .rotateForwardTo(worldForward)
                .moveFaceTo(normal.getOpposite(), worldCenter);

            //applyHoldable(stack, shipBuilder);

            EzDebug.warn("new docker world pos:" + newShip.getTransform().getPositionInWorld());
        }
        return InteractionResult.CONSUME;
    }
    @Override
    public InteractionResult onItemNotUseOnShip(ItemStack stack, Level level, Player player, UseOnContext ctx) {
        EzDebug.log("docker not use on ship");

        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;  //only when press shift can make a ship


        IShipSchemeData shipSchemeData = getShipData(stack);
        //EzDebug.log("shipSchemeData is null?" + (shipSchemeData == null));
        if (shipSchemeData == null) {
            EzDebug.warn("fail to get ship scheme data!");
            return InteractionResult.FAIL;
        }

        //EzDebug.log("making ship");

        //todo use makeShipFromStack
        //Direction playerLookDir = player.getDirection();

        BlockPos useOn = ctx.getClickedPos();
        Direction normal = ctx.getClickedFace();

        ServerShip ship = shipSchemeData.createShip(sLevel);
        ShipBuilder shipBuilder = ShipBuilder.modify(sLevel, ship)
            .moveFaceTo(normal.getOpposite(), useOn.relative(normal).getCenter());

        //applyHoldable(stack, shipBuilder);

        //;

        /*SandBoxServerWorld.getOrCreate(sLevel).serverTickSetEvent.addListener(
            //l -> EzDebug.log("ship pos at " + StrUtil.F2(ship.getTransform().getPositionInWorld()))
            l -> {
                EzDebug.log("logging ship block");
                ShipUtil.foreachBlock(shipBuilder.get(), sLevel, (pos, state, be) -> {
                    EzDebug.log("ship has block:" + StrUtil.poslike(pos) + ", " + StrUtil.getBlockName(state));
                });
            }
        );*/

        return InteractionResult.CONSUME;
    }

    //so can be rendered by dockerItemRender
    //todo
    /*@OnlyIn(value = Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        //consumer.accept(new DockerItemRenderProperty());
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return DockerItemRenderer.INSTANCE;
            }
        });
    }*/

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return DockerItemRenderer.INSTANCE;
            }
        });
    }
    /*
    {
  "parent": "item/generated",
  "textures": {
    "layer0": "vs_wap:item/docker"
  }
}
    */
}
