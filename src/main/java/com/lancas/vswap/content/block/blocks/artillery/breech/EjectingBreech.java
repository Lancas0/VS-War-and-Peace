package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.WapSounds;
import com.lancas.vswap.content.block.api.IHoldingShipInteractableBlock;
import com.lancas.vswap.content.block.blocks.artillery.IBarrel;
import com.lancas.vswap.content.block.blocks.artillery.breech.helper.BreechHelper;
import com.lancas.vswap.content.block.blocks.artillery.breech.helper.LoadedMunitionData;
import com.lancas.vswap.content.block.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.block.api.IDockerInteractableBlock;
import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.register.PlayerScreenShakeEvt;
import com.lancas.vswap.sandbox.ballistics.behaviour.BallisticBehaviour;
import com.lancas.vswap.sandbox.ballistics.data.AirDragSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticBarrelContextSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticInitialStateSubData;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.*;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.content.block.blocks.cartridge.primer.IPrimer;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.*;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EjectingBreech extends BlockPlus implements /*IBreech,*/IBE<EjectingBreechBe>, IBarrel, IDockerInteractableBlock, IHoldingShipInteractableBlock/*, IBE<BreechBE>*/ {
    protected static void withBreechBeDo(BlockGetter world, BlockPos pos, Consumer<EjectingBreechBe> action) {
        Optional
            .ofNullable(world.getBlockEntity(pos))
            .map(x -> {
                if (x instanceof EjectingBreechBe be)
                    return be;
                return null;
            })
            .ifPresent(action);
    }

    private static final List<IBlockAdder> providers = List.of(
        new DirectionAdder(false, true, ShapeBuilder.ofCubicRing(0, 0, 0, 2, 16).get()),
        new RedstoneOnOffAdder(true) {
            @Override
            public void onPoweredOnOff(Level level, BlockPos breechBp, BlockState state, boolean isOn) {
                if (!isOn || !(level instanceof ServerLevel sLevel)) return;

                @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, breechBp);

                BlockState breechState = level.getBlockState(breechBp);
                Vector3d worldBreechPos = WorldUtil.getWorldCenter(artilleryShip, breechBp);
                Vector3d worldLaunchDir = WorldUtil.getWorldDirection(artilleryShip, breechState.getValue(DirectionAdder.FACING));

                withBreechBeDo(sLevel, breechBp, be -> {
                    be.fire();
                    /*if (be.loadedMunitionData.isEmpty())
                        return;

                    LoadedMunitionData lastLoaded = be.loadedMunitionData.get(be.loadedMunitionData.size() - 1);
                    lastLoaded.getShip(sLevel);

                    BlockState primerState = lastLoaded.getShip(sLevel).getBlockCluster().getDataReader().getBlockState(IBreech.LOADED_MUNITION_ORIGIN);
                    if (!(primerState.getBlock() instanceof IPrimer primer))
                        return;

                    Dest<Double> speDest = new Dest<>();
                    List<ItemStack> munitionRemains = new ArrayList<>();
                    BlockClusterData projectileBlockData = primer.fire(sLevel, be.loadedMunitionData, speDest, munitionRemains);

                    be.loadedMunitionData.forEach(x -> SandBoxServerWorld.markShipDeleted(sLevel, x.shipUuid()));  //constraint auto remove
                    be.loadedMunitionData.clear();

                    if (speDest.get() > 0) {
                        //play sound
                        sLevel.playSound(null, breechBp, WapSounds.ARTILLERY_FIRE0.get(), SoundSource.BLOCKS);
                        //shakes
                        PlayerScreenShakeEvt.setShakeTicksNoLessThanDefaultTicks();
                    }

                    Vector3d throwDir = worldLaunchDir.negate(new Vector3d());
                    Vector3d throwDeltaMove = throwDir.mul(0.2, new Vector3d());
                    Vector3d spawnPos = worldBreechPos.add(throwDir, new Vector3d());
                    //throw remain items FIXME now will throw even no propellant power, keep it or fix it : keep it, or don't do anything when no propellant power
                    BreechHelper.ejectAllRemainMunition(sLevel, munitionRemains, () -> spawnPos, () -> throwDeltaMove);


                    if (projectileBlockData != null) {
                        RigidbodyData rigidbodyData = new RigidbodyData(
                            new TransformPrimitive(
                                worldBreechPos,
                                JomlUtil.swingYXRotateTo(IBreech.LOADED_MUNITION_FORWARD_D, worldLaunchDir, new Quaterniond()),
                                new Vector3d(1, 1, 1)  //todo use scale
                            )
                        );

                        SandBoxServerShip ship = new SandBoxServerShip(
                            UUID.randomUUID(),
                            rigidbodyData,
                            projectileBlockData
                        );
                        ship.addBehaviour(new BallisticBehaviour(), new BallisticData(
                            new BallisticInitialStateSubData(worldBreechPos, IBreech.LOADED_MUNITION_FORWARD, worldLaunchDir, speDest.get()),
                            new BallisticBarrelContextSubData(),
                            new AirDragSubData()
                        ));
                        SandBoxServerWorld.addShip(sLevel, ship, true);
                    }*/
                });
            }
        },
        (IBlockRemoveCallbackAdder) () -> new Action<BlockChangeContext, Void>() {
            @Override
            public Void pre(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
                IBE.onRemove(ctx.oldState, ctx.level, ctx.pos, ctx.newState);
                return null;
            }
        }
        /*,
        //IBreech.breechInteraction(),
        new InteractableBlockAdder() {
            @Override
            public InteractionResult onInteracted(BlockState breechState, Level level, BlockPos breechBp, Player player, InteractionHand hand, BlockHitResult hit) {
                if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
                if (!(breechState.getBlock() instanceof IBreech iBreech)) {
                    EzDebug.warn("the breech at " + breechBp.toShortString() + " is not a breech");
                    return InteractionResult.PASS;
                }

                // get holding ship and load
                ICanHoldShip icanHoldShip = (ICanHoldShip)player;
                //Dest<Long> holdingShipId = new Dest<>();
                Long holdingShipId = icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand);
                ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId);
                if (holdingShip == null) return InteractionResult.PASS;

                iBreech.loadMunitionShip(sLevel, breechBp, breechState, holdingShip, true);

                //unhold ship and delete
                icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true);
                VSGameUtilsKt.getShipObjectWorld(sLevel).deleteShip(holdingShip);  //should be already removed in loadMunitionShip
                return InteractionResult.PASS;
            }
        },*/
        /*, new IBlockRemoveCallbackAdder() {
            private static final Action<BlockChangeContext, Void> action = new Action<BlockChangeContext, Void>() {
                @Override
                public Void pre(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
                    if (!(ctx.level instanceof ServerLevel level))
                        return null;
                    if (ctx.newState.getBlock() == ctx.oldState.getBlock())
                        return null;

                    if (!(ctx.oldState.getBlock() instanceof IBreech breech)) {
                        EzDebug.warn("can't find breech at " + ctx.pos.toShortString());
                        return null;
                    }
                    breech.unloadShell(level, ctx.pos, ctx.oldState);
                    return null;
                }
            };
            @Override
            public Action<BlockChangeContext, Void> onRemove() { return action; }
        }*/
        //, new RefreshBlockRecordAdder((level, bp, state) -> new BreechRecord(level, bp, 0))
    );
    @Override
    public List<IBlockAdder> getAdders() { return providers; }

    public EjectingBreech(Properties p_49795_) {
        super(p_49795_);
    }


    /*@Override
    public boolean getLoadedMunitionData(Level level, BlockPos breechBp, Dest<Ship> prevMunitionShip, Dest<Boolean> isPrevTriggered, Dest<Direction> prevMunitionShipDir) {
        Dest<IPrimer> primerDest = new Dest<>();
        Dest<BlockPos> primerBpDest = new Dest<>();
        Dest<Ship> primerShipDest = new Dest<>();

        if (!findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest))
            return false;

        BlockState primerState = level.getBlockState(primerBpDest.get());

        prevMunitionShip.set(primerShipDest.get());
        isPrevTriggered.set(primerDest.get().isTriggered(primerState));
        prevMunitionShipDir.set(DirectionAdder.getDirection(primerState));
        return true;
    }*/

    /*@Override
    public boolean canArmLoadDockerNow(ServerLevel level, BlockPos breechBp, ItemStack stack) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("can't get breech at " + breechBp.toShortString());
            return false;
        }

        if (!record.loadedData.isEmpty())  //arm can only load one
            return false;

        if (!(stack.getItem() instanceof Docker)) return false;
        return true;  //todo: further check if it's really a munition
    }*/

    /*@Override
    public boolean loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("fail to load munition because of failure to find breech record at " + breechBp.toShortString());
            return false;
        }

        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);
        return record.loadDockerShip(level, munitionDocker, artilleryShip, breechBp, breechState.getValue(DirectionAdder.FACING));
    }*/

    /*@Override
    public void loadMunitionShip(ServerLevel level, BlockPos breechBp, BlockState breechState, ServerShip vsShip, boolean simulate) {
        //todo sometime(in face always) repeat invoke
        if (!(breechState.getBlock() instanceof IBreech)) {
            EzDebug.warn("the breech at " + breechBp.toShortString() + " is not a breech");
            return;
        }

        var munitionHoldable = vsShip.getAttachment(HoldableAttachment.class);
        if (munitionHoldable == null) return;  //no holdable

        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);

        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("can't find record at " + breechBp.toShortString());
            return;
        }

        boolean loaded = record.loadShip(level, munitionHoldable, artilleryShip, breechBp, DirectionAdder.getDirection(breechState));

        if (!simulate)
            VSGameUtilsKt.getShipObjectWorld(level).deleteShip(vsShip);
    }*/

    /*@Override
    public void unloadShell(ServerLevel level, BlockPos breechBp, BlockState breechState) {
        withBreechBeDo(level, breechBp, be -> {
            ServerShip artilleryShip = be.artilleryShipCache.get();

            Vector3d worldBreechCenter = WorldUtil.getWorldCenter(artilleryShip, breechBp);
            Vector3d worldLaunchDir = WorldUtil.getWorldDirection(artilleryShip, breechState.getValue(DirectionAdder.FACING));

            Vector3d throwDir = worldLaunchDir.negate(new Vector3d());
            Vector3d throwDeltaMove = throwDir.mul(0.2, new Vector3d());
            Vector3d spawnPos = worldBreechCenter.add(throwDir, new Vector3d());

            BreechHelper.ejectAllMunition(level, be.loadedMunitionData, () -> spawnPos, () -> throwDeltaMove);
        });
    }*/


    /*private static boolean findPrimerAround(Level level, BlockPos breechBp, Dest<IPrimer> primerDest, Dest<BlockPos> primerBpDest, Dest<Ship> primerShipDest) {
        Dest<Long> breechShipId = new Dest<>();
        ShipUtil.getBlockInShipId(level, breechBp, breechShipId);

        Vector3d centerInWorld = WorldUtil.getWorldCenter(level, breechBp);
        AABBd breechWorldAABB = JomlUtil.dCenterExtended(centerInWorld, 0.4);

        for (Ship possiblePrimerShip : VSGameUtilsKt.getShipsIntersecting(level, breechWorldAABB)) {
            if (breechShipId.equalsValue(possiblePrimerShip.getId())) continue;  //same ship, skip

            Vector3d breechCenterInOtherShip = possiblePrimerShip.getWorldToShip().transformPosition(centerInWorld, new Vector3d());
            BlockPos breechBpInOtherShip = JomlUtil.bpContaining(breechCenterInOtherShip);
            IPrimer primerInBreech = getPrimer(level, breechBpInOtherShip);
            if (primerInBreech != null) {
                primerDest.set(primerInBreech);
                primerBpDest.set(breechBpInOtherShip);
                primerShipDest.set(possiblePrimerShip);
                return true;
            }

            //not sure whether the breech's direction and the ammo's direction, so I simply test all directions
            //todo maybe without loop is possible
            for (Direction curDir : Direction.values()) {
                BlockPos aroundBp = breechBpInOtherShip.relative(curDir);
                IPrimer curPrimer = getPrimer(level, aroundBp);

                if (curPrimer != null) {
                    primerDest.set(curPrimer);
                    primerBpDest.set(aroundBp);
                    primerShipDest.set(possiblePrimerShip);
                    return true;
                }
            }
        }
        return false;
    }
    private static IPrimer getPrimer(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof IPrimer primer)
            return primer;
        return null;
    }*/

    /*public static void tryFire(ServerLevel level, BlockPos breechPos) {
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(level, breechPos);
        BlockState breechState = level.getBlockState(breechPos);

        Vector3d worldLaunchDir = WorldUtil.getWorldDirection(level, breechPos, breechState.getValue(DirectionAdder.FACING));


        BlockClusterData blockData = new BlockClusterData();
        Dest<Double> propellantEnergyDest = new Dest<>();
        boolean success = IBreech.foreachMunition(level, breechPos, new Vector3i(0, 0, 1), false, propellantEnergyDest, blockData);
        if (!success || propellantEnergyDest.get() <= 0) return;

        //play sound
        level.playSound(null, breechPos, WapSounds.ARTILLERY_FIRE0.get(), SoundSource.BLOCKS);
        //shakes
        PlayerScreenShakeEvt.setShakeTicksNoLessThanDefaultTicks();

        RigidbodyData rigidbodyData =
            new RigidbodyData(new TransformPrimitive(worldBreechPos, new Quaterniond().rotateTo(new Vector3d(0, 0, 1), worldLaunchDir), new Vector3d(1, 1, 1)));
        //rigidbodyData.setNoGravity();  //don't use gravity, it will be in BallisticFlyingContext

        SandBoxServerShip ship = new SandBoxServerShip(
            UUID.randomUUID(),
            rigidbodyData,
            blockData
        );
        ship.addBehaviour(new BallisticBehaviour(), new BallisticData(
            new BallisticInitialStateSubData(worldBreechPos, new Vector3i(0, 0, 1), worldLaunchDir, propellantEnergyDest.get()),
            new BallisticBarrelContextSubData(),
            new AirDragSubData()
        ));
        SandBoxServerWorld.addShip(level, ship, true);
        //todo unload

        Vector3d throwDir = worldLaunchDir.negate(new Vector3d());
        Vector3d throwDeltaMove = throwDir.mul(0.2, new Vector3d());
        Vector3d spawnPos = worldBreechPos.add(throwDir, new Vector3d());

        IBreech.ejectAllMunition(level, breechPos, () -> spawnPos, () -> throwDeltaMove, true);
    }*/

    @Override
    public boolean mayInteract(ItemStack handDocker, Level level, Player player, BlockPos bp, BlockState state) {
        if (state.getBlock() != this) {
            EzDebug.warn("Can't get breech at " + bp.toShortString());
            return false;
        }
        CreateClient.OUTLINER.showCluster("EjectingBreechSelecting", List.of(bp))
            .lineWidth(0.0675f)
            .colored(WapColors.HINT_ORANGE);
        return true;
    }
    @Override
    public @NotNull ItemStack interact(ItemStack handDocker, Level level, Player player, BlockPos bp, BlockState state) {
        if (!(level instanceof ServerLevel sLevel))
            return handDocker;

        AtomicBoolean success = new AtomicBoolean(false);
        withBreechBeDo(sLevel, bp, be -> {
            success.set(be.loadDockerMunition(handDocker));
        });

        return success.get() ? ItemStack.EMPTY : handDocker;
    }

    @Override
    public boolean mayInteract(@NotNull ClientShip holdingShip, ClientLevel level, Player player, BlockPos bp, BlockState state) {
        /*@Nullable Ship artilleryShip = ShipUtil.getShipAt(level, bp);
        if (artilleryShip != null && Objects.equals(artilleryShip.getId(), holdingShip.getId())) {
            //can't load self
            return false;
        }*/

        //todo check if can load
        CreateClient.OUTLINER.showCluster("EjectingBreechHoldingInteraction", List.of(bp))
            .colored(WapColors.HINT_ORANGE)
            .lineWidth(0.0675f);
        return true;
    }

    @Override
    public boolean interact(@NotNull Ship holdingShip, Level level, Player player, BlockPos bp, BlockState state) {
        if (!(level instanceof ServerLevel sLevel) || !(holdingShip instanceof ServerShip sHoldingShip))
            return false;
        /*if (!(state.getBlock() instanceof IBreech iBreech)) {
            EzDebug.warn("the breech at " + bp.toShortString() + " is not a breech");
            return false;
        }*/
        /*@Nullable Ship artilleryShip = ShipUtil.getShipAt(level, bp);
        if (artilleryShip != null && Objects.equals(artilleryShip.getId(), holdingShip.getId())) {
            //can't load self
            return false;
        }*/

        //todo check if can load

        /*HoldableAttachment holdable = sHoldingShip.getAttachment(HoldableAttachment.class);
        if (holdable == null) {
            EzDebug.warn("the holding ship has no holdable!");
            return false;
        }*/

        ICanHoldShip icanHoldShip = (ICanHoldShip)player;
        withBreechBeDo(sLevel, bp, be -> {
            be.loadShipMunition(sHoldingShip);
        });
        //iBreech.loadMunitionShip(sLevel, bp, state, sHoldingShip, true);

        //unhold ship and delete
        icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true);
        VSGameUtilsKt.getShipObjectWorld(sLevel).deleteShip(sHoldingShip);  //should be already removed in loadMunitionShip
        return true;
    }



    @Override
    public Class<EjectingBreechBe> getBlockEntityClass() { return EjectingBreechBe.class; }
    @Override
    public BlockEntityType<? extends EjectingBreechBe> getBlockEntityType() { return WapBlockEntites.EJECTING_BREECH_BE.get(); }

    /*@Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return (level, blockPos, blockState, s) -> ((EjectingBreechBe)s).tick();
    }*/
}
