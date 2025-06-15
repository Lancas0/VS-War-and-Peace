package com.lancas.vswap.content.item.items.docker;

import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.content.block.api.IDockerInteractableBlock;
import com.lancas.vswap.content.block.blocks.industry.shredder.IShredderableItem;
import com.lancas.vswap.content.saved.refship.RefShipMgr;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.mixinfriend.LevelRenderTaskReceiver;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.ship.data.IShipSchemeData;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.ship.feature.hide.VsHideAndSeek;
import com.lancas.vswap.ship.feature.hide.hideseek.HideIntoUnderVoid;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.ship.feature.pool.ShipPool;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.ItemPredictPlacementAdder;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.util.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.outliner.Outline;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


public class Docker extends Item implements IShredderableItem {
    protected static final String SHIP_DATA_KEY = "ship_data";
    protected static final String SHIP_REF_KEY = "ship_ref";
    protected static final String SHIP_AABB_KEY = "ship_aabb";
    protected static final String SHIP_SCALE_KEY = "ship_scale";

    @Override
    public List<ItemStack> getProducts(ItemStack stack) {
        RRWChunkyShipSchemeData schemeData = Docker.getShipData(stack);
        if (schemeData == null || schemeData.isEmpty())
            return List.of();

        List<ItemStack> products = new ArrayList<>();
        double scale = schemeData.getScale().x();  //todo xyzScale?
        int amount = MaterialStandardizedItem.getCntByScale(scale);
        schemeData.foreachBlockInLocal((bp, state) -> {
            products.add(MaterialStandardizedItem.fromBlock(state.getBlock(), amount));
        });
        return products;
    }

    public enum PlaceMode {
        VsShip, Blocks
    }

    public Docker(Properties p_41383_) {
        super(p_41383_);
    }

    public static ItemStack saveShipData(ItemStack stack, ServerLevel level, ServerShip ship, boolean ref, boolean autoHiding) {
        if (ship == null || ship.getShipAABB() == null) {
            EzDebug.warn("save a null ship, or ship has null aabb");
            return ItemStack.EMPTY;
        }

        CompoundTag stackNbt = stack.getOrCreateTag();

        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData().readShip(level, ship);
        NbtBuilder.modify(stackNbt)
            .putCompound(SHIP_DATA_KEY, data.saved())
            .putAABBi(SHIP_AABB_KEY, ship.getShipAABB())
            .putVector3d(SHIP_SCALE_KEY, ship.getTransform().getShipToWorldScaling());

        if (ref) {
            stackNbt.putLong(SHIP_REF_KEY, ship.getId());
            //countingDown.put(ship.getId(), new BiTuple<>(level, COUNTING_DOWN));
            RefShipMgr.addCountingRef(level, ship.getId());

            if (autoHiding) {
                VsHideAndSeek.hide(level, ship, new HideIntoUnderVoid());
                //ShipPool.getOrCreatePool(level).hideShip(ship, ShipPool.HideType.StaticAndInvisible);
            }
        }

        return stack;
    }


    public static @Nullable ServerShip getReferencedShip(ServerLevel level, ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (stackNbt.contains(SHIP_REF_KEY, CompoundTag.TAG_LONG)) {
            return ShipUtil.getServerShipByID(level, stackNbt.getLong(SHIP_REF_KEY));
        }
        return null;
    }
    public static @Nullable Long getReferencedShipId(ServerLevel level, ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (stackNbt.contains(SHIP_REF_KEY, CompoundTag.TAG_LONG)) {
            return stackNbt.getLong(SHIP_REF_KEY);
        }
        return null;
    }
    public static @Nullable Ship getReferencedShip(Level level, ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (stackNbt.contains(SHIP_REF_KEY)) {
            return ShipUtil.getShipByID(level, stackNbt.getLong(SHIP_REF_KEY));
        }
        return null;
    }

    //todo simulate stack only contains AABB
    //recommend that byRefIfPossible is true
    //recommend that use this method, rather than use getShipData().make....
    public static @Nullable ShipBuilder makeVsShipBuilder(ServerLevel level, ItemStack stack, boolean tryRef, boolean autoUnHide) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        if (tryRef) {
            if (stackNbt.contains(SHIP_REF_KEY)) {
                long shipId = stackNbt.getLong(SHIP_REF_KEY);
                //remove ref after first use: avoid duplicate ref
                stackNbt.remove(SHIP_REF_KEY);

                ServerShip refShip = ShipUtil.getServerShipByID(level, shipId);
                if (refShip != null) {
                    //countingDown.remove(refShip.getId());  //stop remove counting down
                    RefShipMgr.releaseRef(level, refShip.getId());

                    if (autoUnHide) {
                        //ShipPool.getOrCreatePool(level).showShip(refShip.getId());
                        VsHideAndSeek.getOrCreate(level).seek(refShip);
                    }

                    return ShipBuilder.modify(level, refShip);
                }
            }
        }

        RRWChunkyShipSchemeData shipData = getShipData(stack);
        if (shipData == null || shipData.isEmpty()) return null;

        return ShipPool
            .getOrCreatePool(level)
            .getOrCreateEmptyShipBuilder()
            .overwriteByScheme(shipData);
    }
    public static @Nullable ServerShip makeVsShip(ServerLevel level, ItemStack stack, boolean tryRef, boolean autoUnHide) {
        ShipBuilder builder = makeVsShipBuilder(level, stack, tryRef, autoUnHide);
        return builder == null ? null : builder.get();
    }

    public static @Nullable RRWChunkyShipSchemeData getShipData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        if (!(stack.getItem() instanceof Docker)) return null;
        if (!stackNbt.contains(SHIP_DATA_KEY)) return null;

        CompoundTag shipDataNbt = stackNbt.getCompound("ship_data");
        return new RRWChunkyShipSchemeData().load(shipDataNbt);
    }

    /*public static @Nullable AABBi getShipAABBContainsCoordinate(ItemStack stack) {
        AtomicReference<AABBi> aabb = new AtomicReference<>(null);
        NbtBuilder.modify(stack.getOrCreateTag())
            .readDoIfExist(SHIP_AABB_KEY, aabb::set, NbtBuilder::getAABBi);
        return aabb.get();
    }*/
    public static @Nullable AABBi getShipAABBContainsShape(ItemStack stack) {
        AtomicReference<AABBi> aabb = new AtomicReference<>(null);
        NbtBuilder.modify(stack.getOrCreateTag())
            .readDoIfExist(SHIP_AABB_KEY, aabb::set, NbtBuilder::getAABBi);
        return aabb.get();
    }

    public static @Nullable Vector3d getScale(ItemStack stack) {
        AtomicReference<Vector3d> scale = new AtomicReference<>(null);
        NbtBuilder.modify(stack.getOrCreateTag())
            .readDoIfExist(SHIP_SCALE_KEY, scale::set, NbtBuilder::getNewVector3d);
        return scale.get();
    }



    //todo simulate stack
    public static ItemStack defaultStack() {
        return WapItems.DOCKER.asStack();
    }
    /*public static ItemStack stackOfVsCopy(ServerLevel level, ServerShip ship) {
        ItemStack stack = defaultStack();
        Docker docker = (Docker)stack.getItem();
        return docker.saveShip(level, ship, stack);
    }
    public static ItemStack stackOfVsRef(ServerLevel level, ServerShip ship) {
        ItemStack stack = defaultStack();
        Docker docker = (Docker)stack.getItem();
        return docker.saveShip(level, ship, stack);
    }*/
    public static ItemStack stackOfVs(ServerLevel level, ServerShip ship, boolean ref, boolean autoHiding) {
        ItemStack stack = defaultStack();
        return saveShipData(stack, level, ship, ref, autoHiding);
    }
    public static ItemStack stackOfSa(ServerLevel level, ISandBoxShip ship) {
        /*ItemStack stack = WapItems.DOCKER.asStack();
        CompoundTag stackNbt = stack.getOrCreateTag();

        AABBi aabb = new AABBi();
        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData();
        int addY = (level.getHeight() + level.getMinBuildHeight()) / 2 + (int)(JomlUtil.lengthY(ship.getBlockCluster().getDataReader().getLocalAABB()) / 2);

        ship.getBlockCluster().getDataReader().allBlocks().forEach(e -> {
            data.setBlockAtLocalBp(JomlUtil.bp(e.getKey()).offset(0, addY, 0), e.getValue(), null);  //todo temporily can't save be if use sa ship
            aabb.union(e.getKey());
        });

        NbtBuilder.modify(stackNbt)
            .putCompound(SHIP_DATA_KEY, data.saved())
            .putAABBi(SHIP_AABB_KEY, aabb);

        //todo now can't save att if use sa ship
        return stack;*/
        return stackOfSaBlockData(level, ship.getBlockCluster().getDataReader());
    }
    public static ItemStack stackOfSaBlockData(ServerLevel level, IBlockClusterDataReader bcDataReader) {
        ItemStack stack = WapItems.DOCKER.asStack();
        CompoundTag stackNbt = stack.getOrCreateTag();

        AABBi aabb = new AABBi();
        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData();
        int addY = (level.getHeight() + level.getMinBuildHeight()) / 2 + (int)(JomlUtil.lengthY(bcDataReader.getLocalAABB()) / 2);

        bcDataReader.allBlocks().forEach(e -> {
            data.setBlockAtLocalBp(JomlUtil.bp(e.getKey()).offset(0, addY, 0), e.getValue(), null);  //todo temporily can't save be if use sa ship
            aabb.union(e.getKey());
        });

        NbtBuilder.modify(stackNbt)
            .putCompound(SHIP_DATA_KEY, data.saved())
            .putAABBi(SHIP_AABB_KEY, aabb);

        //todo now can't save att if use sa ship
        return stack;
    }
    public static ItemStack stackOfSaBlockData(int addY, IBlockClusterDataReader bcDataReader) {
        ItemStack stack = WapItems.DOCKER.asStack();
        CompoundTag stackNbt = stack.getOrCreateTag();

        AABBi aabb = new AABBi();
        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData();

        bcDataReader.allBlocks().forEach(e -> {
            data.setBlockAtLocalBp(JomlUtil.bp(e.getKey()).offset(0, addY, 0), e.getValue(), null);  //todo temporily can't save be if use sa ship
            aabb.union(e.getKey());
        });

        NbtBuilder.modify(stackNbt)
            .putCompound(SHIP_DATA_KEY, data.saved())
            .putAABBi(SHIP_AABB_KEY, aabb);

        //todo now can't save att if use sa ship
        return stack;
    }

    //todo remove
    public static ItemStack stackOfData(RRWChunkyShipSchemeData data) {
        ItemStack stack = defaultStack();
        stack.getOrCreateTag().put(SHIP_DATA_KEY, data.saved());
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


    /*@Override
    public boolean hasShipData(ItemStack stack) {
        return (stack.getItem() instanceof Docker) && (stack.getOrCreateTag().contains("ship_data"));
    }


    @Override
    public @Nullable UUID getOrCreateDockerUuidIfHasData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        if (!(stack.getItem() instanceof Docker)) return null;
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
    */

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

    /*@Override
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
    }*/
    /*@Override
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

        /.*SandBoxServerWorld.getOrCreate(sLevel).serverTickSetEvent.addListener(
            //l -> EzDebug.log("ship pos at " + StrUtil.F2(ship.getTransform().getPositionInWorld()))
            l -> {
                EzDebug.log("logging ship block");
                ShipUtil.foreachBlock(shipBuilder.get(), sLevel, (pos, state, be) -> {
                    EzDebug.log("ship has block:" + StrUtil.poslike(pos) + ", " + StrUtil.getBlockName(state));
                });
            }
        );*./

        return InteractionResult.CONSUME;
    }*/

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
    public static ShipBuilder setShipTransformByHoldable(ShipBuilder builder, HoldableAttachment holdable, Vector3dc moveThePivotTo, Vector3dc rotateTheForwardTo) {
        Matrix4dc shipToWorld = builder.get().getShipToWorld();

        return builder.doIfElse(
            self -> holdable != null,
            self -> {
                Vector3d worldForward = JomlUtil.dWorldNormal(shipToWorld, holdable.forwardInShip);
                Quaterniond rotation = worldForward.rotationTo(rotateTheForwardTo, new Quaterniond());
                self.rotate(rotation).moveShipPosToWorldPos(JomlUtil.dCenter(holdable.holdPivotBpInShip.toBp()), moveThePivotTo);
            },
            self -> self.setWorldPos(moveThePivotTo)  //todo do direciton
        );
    }



    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return DockerItemRenderer.INSTANCE;
            }
        });
    }


    public static void setMode(ItemStack stack, PlaceMode mode) {
        NbtBuilder.ofStack(stack)
            .putEnum("mode", mode);
    }
    public @NotNull static PlaceMode getMode(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (stackNbt.contains("mode"))
            return NbtBuilder.ofStack(stack).getEnum("mode", PlaceMode.class);
        else {
            //put default VsShip
            NbtBuilder.ofStack(stack).putEnum("mode", PlaceMode.VsShip);
            return PlaceMode.VsShip;
        }
    }

    //todo can change mod place ship / place block(only when scale is 1(or very close to 1))

    @OnlyIn(Dist.CLIENT)
    private static int renderTaskLifeCountdown = 0;
    @OnlyIn(Dist.CLIENT)
    private static final LevelRenderTaskReceiver.LevelRenderTask blockPreviewTask = new LevelRenderTaskReceiver.LevelRenderTask() {
        @Override
        public void render(MultiBufferSource bufSrc, PoseStack poseStack, float partialTicks, Camera camera, GameRenderer gameRenderer) {
            poseStack.pushPose();
            //move camera space to world space
            poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

            cachedShipSchemeData.foreachBlockInLocal((bp, state) -> {
                BlockPos worldBp = bp.offset(localToWorldBlockOffset.x, localToWorldBlockOffset.y, localToWorldBlockOffset.z);
                //EzDebug.log("render at " + worldBp.toShortString());

                //move to current block pos
                poseStack.translate(worldBp.getX(), worldBp.getY(), worldBp.getZ());
                //todo batched

                BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                Level level = Minecraft.getInstance().level;
                if (level == null) return;

                dispatcher.getModelRenderer().tesselateBlock(
                    level,
                    dispatcher.getBlockModel(state),
                    state,
                    worldBp,
                    poseStack,
                    bufSrc.getBuffer(RenderType.solid()),  //todo change render type by state
                    false, // 不检查相邻方块遮挡
                    RandomSource.create(),
                    state.getSeed(BlockPos.ZERO),
                    OverlayTexture.NO_OVERLAY
                );

                poseStack.translate(-worldBp.getX(), -worldBp.getY(), -worldBp.getZ());
            });

            poseStack.popPose();
        }
        @Override
        public boolean isAlive() { return cachedShipSchemeData != null && !cachedShipSchemeData.isEmpty() && --renderTaskLifeCountdown > 0; }
    };
    @OnlyIn(Dist.CLIENT)
    private static final Vector3i localToWorldBlockOffset = new Vector3i();
    @OnlyIn(Dist.CLIENT)
    private static RRWChunkyShipSchemeData cachedShipSchemeData = null;
    @OnlyIn(Dist.CLIENT)
    private static ItemStack cachedItemStack = null;
    @OnlyIn(Dist.CLIENT)
    private static final LazyTicks schemeDataUpdateLazy = new LazyTicks(10);

    private static final ItemPredictPlacementAdder
        predictPlacementAdder = new ItemPredictPlacementAdder(List.of(ItemPredictPlacementAdder.PlacementStage.Predict, ItemPredictPlacementAdder.PlacementStage.PreUseOn)) {
        @Override
        public InteractionResult predictPlacement(ItemStack stack, Level level, Player player, BlockPlaceContext ctx, PlacementStage stage) {
            BlockPos placeAgainstBp = ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite());
            BlockState placeAgainstState = level.getBlockState(placeAgainstBp);

            if (player.getClass() == LocalPlayer.class || player.getClass() == ServerPlayer.class) {
                //only check real player
                if (level.getBlockState(placeAgainstBp).isAir())
                    return InteractionResult.PASS;
            }

            PlaceMode mode = getMode(stack);

            if (stage == PlacementStage.Predict) {
                if (!level.isClientSide)
                    return InteractionResult.PASS;

                if (cachedShipSchemeData == null || cachedShipSchemeData.isEmpty())
                    return InteractionResult.PASS;

                //todo preview rendered ship outline
                //todo when mode is Blocks?
                if (placeAgainstState.getBlock() instanceof IDockerInteractableBlock dib) {
                    if (dib.mayInteract(stack, level, player, placeAgainstBp, placeAgainstState)) {
                        return InteractionResult.PASS;
                    }
                }

                return switch (mode) {
                    case VsShip -> previewShipOutline(level, ctx);
                    case Blocks -> previewBlocks(level, ctx);
                };
            } else {
                if (!(level instanceof ServerLevel sLevel))
                    return InteractionResult.PASS;

                if (placeAgainstState.getBlock() instanceof IDockerInteractableBlock dib) {
                    ItemStack afterStack = dib.interact(stack, level, player, placeAgainstBp, placeAgainstState);
                    if (!afterStack.equals(stack, true)) { //if get dif stack
                        player.setItemInHand(ctx.getHand(), afterStack);
                        return InteractionResult.SUCCESS;
                    }
                }

                InteractionResult result = switch (mode) {
                    case VsShip -> spawnShip(sLevel, stack, ctx);
                    case Blocks -> placeBlocks(sLevel, stack, ctx);//InteractionResult.PASS;
                };
                if (result.consumesAction())
                    stack.shrink(1);
                return result;
            }
        }
    };
    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity player, int ix, boolean selecting) {
        super.inventoryTick(stack, level, player, ix, selecting);
        if (!level.isClientSide)
            return;
        if (!selecting)
            return;

        /*if (schemeDataUpdateLazy.shouldWork()) {
            cachedShipSchemeData = (RRWChunkyShipSchemeData)getShipData(stack);
        }*/
        if (cachedItemStack != stack) {
            cachedShipSchemeData = (RRWChunkyShipSchemeData)getShipData(stack);
            cachedItemStack = stack;
            lastPreviewBlock = null;
        }

        predictPlacementAdder.inventoryTick(stack, level, player, ix, selecting);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        if (ctx.getLevel() instanceof ServerLevel level && ctx.getPlayer() instanceof ServerPlayer player) {  //read ship must by player
            if (player.isCreative()) {  //only creative player can read ship
                ServerShip ship = ShipUtil.getServerShipAt(level, ctx.getClickedPos());
                if (ship != null) {
                    InteractionResult readShipResult = readShip(level, ship, player, ctx.getItemInHand(), ctx);
                    if (readShipResult.consumesAction())
                        return readShipResult;
                }
            }
        }

        return predictPlacementAdder.useOn().pre(ctx, InteractionResult.PASS, Dest.__());
    }
    private InteractionResult readShip(ServerLevel level, ServerShip ship, Player player, ItemStack stack, UseOnContext ctx) {
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        IShipSchemeData shipData = getShipData(stack);

        if (shipData == null || shipData.isEmpty()) {
            saveShipData(stack, level, ship, false, false);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @OnlyIn(Dist.CLIENT)
    private static InteractionResult previewShipOutline(Level level, BlockPlaceContext ctx) {
        AABBd scaledAABB = JomlUtil.scaleFromLower(cachedShipSchemeData.getLocalAabbContainsShape(), cachedShipSchemeData.getScale(), new AABBd());
        //JomlUtil.extentFromLower(scaledExtended, new Vector3d(1, 1, 1));  //should extend because localAABB is union of lower block coordinates

        Direction clickedFace = ctx.getClickedFace();
        Vector3dc clickedPos = JomlUtil.d(ctx.getClickLocation());

        Vector3d from = JomlUtil.dFaceCenter(scaledAABB, clickedFace.getOpposite());
        Vector3d to = WorldUtil.getWorldPos(level, clickedPos, new Vector3d());

        AABBd worldAABB = scaledAABB.translate(new Vector3d(to).sub(from));
        CreateClient.OUTLINER.showAABB("ship_data_docker_place_outline", JomlUtil.aabb(worldAABB));
        return InteractionResult.PASS;
    }
    private static BlockPos lastPreviewBlock = null;
    private static Outline.OutlineParams lastBlocksValidationOutline = null;
    @OnlyIn(Dist.CLIENT)
    private static InteractionResult previewBlocks(Level level, BlockPlaceContext ctx) {
        if (!ctx.canPlace())
            return InteractionResult.PASS;

        //can preview block then scale must be 1, no need for scale
        AABBic localAABB = cachedShipSchemeData.getLocalAabbContainsShape();
        Direction clickedFace = ctx.getClickedFace();
        //Vector3dc clickedPos = JomlUtil.d(ctx.getClickLocation());
        //Vector3d from = JomlUtil.dFaceCenter(localAABB, clickedFace.getOpposite());
        BlockPos from = JomlUtil.bpContaining(JomlUtil.dFaceCenter(localAABB, clickedFace.getOpposite()));
        BlockPos to = WorldUtil.getWorldBp(level, JomlUtil.d(ctx.getClickLocation()).add(JomlUtil.dNormal(clickedFace, 0.5)));//WorldUtil.getWorldPos(level, clickedPos, new Vector3d());
        //Vector3d diff = to.subtract(from);//to.sub(from, new Vector3d());
        BlockPos diff = to.subtract(from);
        localToWorldBlockOffset.set(diff.getX(), diff.getY(), diff.getZ()/*(int)Math.floor(diff.x), (int)Math.floor(diff.y), (int)Math.floor(diff.z)*/);

        //here render blocks
        LevelRenderTaskReceiver renderer = (LevelRenderTaskReceiver)(Minecraft.getInstance().levelRenderer);
        //only in client dist so it should be safe
        renderTaskLifeCountdown = 20;  //reset life count down
        renderer.acceptIfAbsent("docker_preview_blocks", blockPreviewTask);


        if (!Objects.equals(lastPreviewBlock, ctx.getClickedPos())) {
            AABBi worldAABB = localAABB.translate(localToWorldBlockOffset.x, localToWorldBlockOffset.y, localToWorldBlockOffset.z, new AABBi());
            lastBlocksValidationOutline = null;

            lastPreviewBlock = ctx.getClickedPos();
            BlockPos collisionBp = cachedShipSchemeData.allLocalBps()
                .map(loc -> loc.offset(localToWorldBlockOffset.x, localToWorldBlockOffset.y, localToWorldBlockOffset.z))  //local to world
                .filter(wd -> !level.getBlockState(wd).isAir())
                .findFirst()
                .orElse(null);

            if (collisionBp != null)
                EzDebug.warn("collisionBp:" + collisionBp + ", collisionState:" + level.getBlockState(collisionBp));

            int color = collisionBp == null ? WapColors.SHOWCASE_BLUE : WapColors.DARK_RED;
            lastBlocksValidationOutline = CreateClient.OUTLINER
                .showAABB("preview_block_validation_aabb", JomlUtil.aabb(worldAABB))
                .colored(color)
                .lineWidth(0.0675f);
        } else {
            //lastPreviewBlock is same, simply use lastOutlineParams
            CreateClient.OUTLINER.keep("preview_block_validation_aabb");
        }

        return InteractionResult.PASS;
    }
    private static InteractionResult placeBlocks(ServerLevel level, ItemStack stack, BlockPlaceContext ctx) {
        RRWChunkyShipSchemeData schemeData = Docker.getShipData(stack);
        if (schemeData == null || schemeData.isEmpty())
            return InteractionResult.PASS;

        AABBic localAABB = schemeData.getLocalAabbContainsShape();
        if (!localAABB.isValid()) {
            EzDebug.warn("schemeData is not empty but get invalid localAABB:" + localAABB);
            return InteractionResult.PASS;
        }
        Direction clickedFace = ctx.getClickedFace();
        BlockPos from = JomlUtil.bpContaining(JomlUtil.dFaceCenter(localAABB, clickedFace.getOpposite()));
        BlockPos to = WorldUtil.getWorldBp(level, JomlUtil.d(ctx.getClickLocation()).add(JomlUtil.dNormal(clickedFace, 0.5)));//WorldUtil.getWorldPos(level, clickedPos, new Vector3d());
        BlockPos localToWorld = to.subtract(from);

        BlockPos collisionBp = schemeData.allLocalBps()
            .map(loc -> loc.offset(localToWorld))  //local to world
            .filter(wd -> !level.getBlockState(wd).isAir())
            .findFirst()
            .orElse(null);

        if (collisionBp != null)
            return InteractionResult.PASS;  //collide block, directly return

        //can place
        schemeData.placeAsBlocks(level, localToWorld);
        return InteractionResult.SUCCESS;
    }
    private static InteractionResult spawnShip(ServerLevel level, ItemStack stack, BlockPlaceContext ctx) {
        if (!(stack.getItem() instanceof Docker)) {
            EzDebug.warn("try spawn ship with not docker item, item:" + stack.getItem());
            return InteractionResult.PASS;
        }

        /*RRWChunkyShipSchemeData schemeData = getShipData(stack);
        if (schemeData == null || schemeData.isEmpty())
            return InteractionResult.PASS;

        Direction clickedFace = ctx.getClickedFace();
        Vector3dc clickedPos = JomlUtil.d(ctx.getClickLocation());
        Vector3d to = WorldUtil.getWorldPos(level, clickedPos, new Vector3d());

        ShipBuilder builder = new ShipBuilder(BlockPos.ZERO, level, schemeData.getScale().x(), false);  //todo 3d scale?
        builder.overwriteByScheme(schemeData)
            .moveFaceTo(clickedFace.getOpposite(), to);*/
        Direction clickedFace = ctx.getClickedFace();
        Vector3dc clickedPos = JomlUtil.d(ctx.getClickLocation());
        Vector3d to = WorldUtil.getWorldPos(level, clickedPos, new Vector3d());

        ShipBuilder builder = Docker.makeVsShipBuilder(level, stack, true, true);
        if (builder == null)
            return InteractionResult.PASS;

        builder.moveFaceTo(clickedFace.getOpposite(), to);
        return InteractionResult.SUCCESS;
    }

}
