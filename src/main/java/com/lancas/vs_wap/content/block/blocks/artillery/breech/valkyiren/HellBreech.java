package com.lancas.vs_wap.content.block.blocks.artillery.breech.valkyiren;

import com.lancas.vs_wap.content.block.blocks.artillery.IBarrel;
import com.lancas.vs_wap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vs_wap.content.block.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vs_wap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.content.block.blocks.cartridge.PrimerBlock;
import com.lancas.vs_wap.content.item.items.docker.IDocker;
import com.lancas.vs_wap.content.saved.BlockRecordRWMgr;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.register.ServerShipEvent;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.ballistics.BallisticsServerMgr;
import com.lancas.vs_wap.ship.ballistics.handler.ShellTriggerHandler;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.RedstoneOnOffAdder;
import com.lancas.vs_wap.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HellBreech extends BlockPlus implements IBreech, IBarrel {
    private static final List<IBlockAdder> adders = List.of(
        new DirectionAdder(false, true, ShapeBuilder.ofCubicRing(0, 0, 0, 2, 16).get()),
        new RedstoneOnOffAdder(true) {
            @Override
            public void onPoweredOnOff(Level level, BlockPos breechBp, BlockState state, boolean isOn) {
                if (!isOn || level.isClientSide) return;

                //ServerShip onShip = ShipUtil.getServerShipAt((ServerLevel)level, breechBp);

                ///get the breech bounds in world and breech center in world for get intersected ships
            /*AABBd worldAABBInBreech;
            Vector3d worldBreechCenter;
            if (onShip == null) {
                worldAABBInBreech = JomlUtil.dBoundBlock(breechBp);
                worldBreechCenter = JomlUtil.dCenter(breechBp);
            } else {
                Matrix4dc shipToWorld = onShip.getShipToWorld();
                worldAABBInBreech = BallisticsUtil.quickTransformAABB(
                    shipToWorld,
                    JomlUtil.dBoundBlock(breechBp)
                );
                worldBreechCenter = shipToWorld.transformPosition(JomlUtil.dCenter(breechBp));
            }*/

                ///find primer around and try to trigger it
                Dest<IPrimer> primerDest = new Dest<>();
                Dest<BlockPos> primerBpDest = new Dest<>();
                Dest<ServerShip> primerShipDest = new Dest<>();

                if (findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest)) {
                    fire((ServerLevel) level, breechBp, primerShipDest.get(), primerDest.get(), primerBpDest.get());
                    return;
                }

            /*for (LoadedShip findShip : VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getIntersecting(worldAABBInBreech)) {
                if (onShip != null && findShip.getId() == onShip.getId()) continue;  //skip onShip

                Matrix4dc worldToFindShip = findShip.getWorldToShip();
                Vector3d breechCenterInShip = worldToFindShip.transformPosition(worldBreechCenter, new Vector3d());

                BlockPos bpInBreech = BlockPos.containing(breechCenterInShip.x, breechCenterInShip.y, breechCenterInShip.z);

                Dest<IPrimer> primerDest = new Dest<>();
                Dest<BlockPos> primerBpDest = new Dest<>();
                findPrimerAround(level, bpInBreech, primerDest, primerBpDest);

                if (primerDest.hasValue() && primerBpDest.hasValue()) {
                    EzDebug.log("realdy call primer handling");
                    handlePrimer((ServerLevel)level, breechBp, (ServerShip)findShip, primerDest.get(), primerBpDest.get());
                    return;
                }
            }*/
            }
        },
        new InteractableBlockAdder() {
            @Override
            public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                //todo sometime(in face always) repeat invoke
                if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;

                ICanHoldShip icanHoldShip = (ICanHoldShip)player;
                Dest<Long> holdingShipId = new Dest<>();
                icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand, holdingShipId);

                ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId.get());
                if (holdingShip == null) return InteractionResult.PASS;  //not holding ship

                if (!(state.getBlock() instanceof IBreech iBreech)) {
                    EzDebug.warn("the block " + StrUtil.getBlockName(state) + " should is iBreech");
                    return InteractionResult.FAIL;
                }

                iBreech.loadMunitionShip(sLevel, pos, state, holdingShip, false);

                icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
                return InteractionResult.PASS;
            }
        },
        new RefreshBlockRecordAdder((level, bp, state) -> new IBreech.BreechRecord(level, bp, 40))
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return adders; }

    public HellBreech(BlockBehaviour.Properties p_49795_) {
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

    @Override
    public boolean canLoadDockerNow(Level level, BlockPos breechBp, ItemStack stack) {  //todo put cold down in it
        //hell breech can't load sep
        if (findPrimerAround(level, breechBp, null, null, null))
            return false;
        if (!(stack.getItem() instanceof IDocker)) return false;
        return true;  //todo: further check if it's really a munition
    }

    @Override
    public void loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker) {
        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);

        Vector3dc placePos = WorldUtil.getWorldCenter(level, breechBp);
        Vector3dc placeDir = WorldUtil.getWorldDirection(level, breechBp, breechState.getValue(DirectionAdder.FACING));

        //ServerShip newMunition = IDocker.makeShipBuilderFromStack(level, munitionDocker/*, placePos, placeDir*/);
        //todo use pool?
        ShipBuilder munitionBuilder = IDocker.makeShipBuilderFromStack(level, munitionDocker);
        if (munitionBuilder == null) {
            EzDebug.warn("can't get munition ship from docker item");
            return;
        }
        ServerShip newMunition = munitionBuilder.get();  //todo initialize the pos and rot

        //todo pre check if have holdable
        var holdable = newMunition.getAttachment(HoldableAttachment.class);
        if (holdable == null) {
            ShipPool.getOrCreatePool(level).returnShipAndSetEmpty(newMunition, ShipPool.ResetAndSet.farawayAndNoConstraint);
            newMunition = null;
        }

        if (newMunition == null) {
            EzDebug.warn("fail to load munition ship");
            return;
        } else {
            EzDebug.highlight("successfully make ship and place at:" + newMunition.getTransform().getPositionInWorld());
        }

        Direction breechDirInWorldOrShip = level.getBlockState(breechBp).getValue(DirectionAdder.FACING);
        //todo lock more effective, todo not foreach
        ServerShip finalNewMunition = newMunition;
        ShipBuilder.modify(level, finalNewMunition)
            .moveShipPosToWorldPos(JomlUtil.dCenter(holdable.getPivotBpInShip()), WorldUtil.getWorldCenter(artilleryShip, breechBp))
            .foreachBlock((curBp, state, be) -> {
                if (state.getBlock() instanceof PrimerBlock primer) {
                    EzDebug.log("primer creating constraints");
                    PrimerBlock.createConstraints(level, curBp, artilleryShip, finalNewMunition, breechBp, breechDirInWorldOrShip, holdable);
                }
            });
    }

    @Override
    public void loadMunitionShip(ServerLevel level, BlockPos breechBp, BlockState breechState, ServerShip vsShip, boolean simulate) {
        var munitionHoldable = vsShip.getAttachment(HoldableAttachment.class);
        if (munitionHoldable == null) return;  //no holdable

        AtomicBoolean shouldLoad = new AtomicBoolean(false);
        AtomicReference<BlockPos> primerBp = new AtomicReference<>(null);
        //todo not foreach ship
        ShipBuilder.modify(level, vsShip).foreachBlock((posInShip, stateInShip, blockEntity) -> {
            if (shouldLoad.get()) return;

            if (stateInShip.getBlock() instanceof PrimerBlock primer) {  //todo not support other primer now
                if (!primer.isTriggered(stateInShip)) {
                    shouldLoad.set(true);
                    primerBp.set(posInShip);
                }
            }
        });
        if (!shouldLoad.get()) return;

        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);

        Direction breechDir = DirectionAdder.getDirection(breechState);  //todo use IBreech to get breech dir?
        if (breechDir == null) {
            EzDebug.fatal("can not get direction of breech");
            return;
        }

        PrimerBlock.createConstraints(level, primerBp.get(), artilleryShip, vsShip, breechBp, breechDir, munitionHoldable);
    }

    @Override
    public void unloadShell(ServerLevel level, BlockPos breechBp, BlockState breechState) {
        Dest<IPrimer> primerDest = new Dest<>();
        Dest<BlockPos> primerBpDest = new Dest<>();
        Dest<ServerShip> primerShipDest = new Dest<>();

        BreechRecord breechRecord = BlockRecordRWMgr.getRecord(level, breechBp);
        if (breechRecord == null) {
            EzDebug.warn("can't get breech record at " + breechBp.toShortString());
            return;
        }

        //todo avoid find primer every time
        if (findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest)) {
            breechRecord.setLoadColdDown(5);

            PrimerBlock.removeConstraint(level, primerBpDest.get());

            BlockState primerState = level.getBlockState(primerBpDest.get());
            Direction primerDir = primerState.getValue(DirectionAdder.FACING);//directional.getDirection(primerState);

            //Vector3d worldLaunchDir = WorldUtil.getWorldDirection(level, )

            ServerShipEvent.delayedShipEvents.add(() -> unloadShellImpl(level, primerShipDest.get(), primerDir, breechBp));
        }
    }

    //@Override
    private void unloadShellImpl(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp) {
        Vector3d breechWorldPos = WorldUtil.getWorldCenter(level, breechBp);

        ShipBuilder.modify(level, shellShip)
            .moveFaceTo(shellDirInShip, breechWorldPos)
            .setLocalVelocity(JomlUtil.dNormal(shellDirInShip, -15));
    }


    private static boolean findPrimerAround(Level level, BlockPos breechBp, @Nullable Dest<IPrimer> primerDest, @Nullable Dest<BlockPos> primerBpDest, @Nullable Dest<ServerShip> primerShipDest) {
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
                Dest.setIfExistDest(primerDest, primerInBreech);
                Dest.setIfExistDest(primerBpDest, breechBpInOtherShip);
                Dest.setIfExistDest(primerShipDest, (ServerShip)possiblePrimerShip);
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
                    primerShipDest.set((ServerShip)possiblePrimerShip);
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
    }

    private static void fire(ServerLevel level, BlockPos breechPos, ServerShip primerShip, IPrimer primer, BlockPos primerPos) {
        BlockState primerState = level.getBlockState(primerPos);
        Direction primerDir = primerState.getValue(DirectionAdder.FACING);//directional.getDirection(primerState);
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(level, breechPos);
        Vector3d worldLaunchDir = JomlUtil.dWorldNormal(primerShip.getShipToWorld(), primerDir);
        HellBreech iBreech = WorldUtil.getBlockInterface(level, breechPos, null);

        if (iBreech == null) {
            EzDebug.warn("can't get HellBreech at " + breechPos.toShortString());
            return;
        }

        EzDebug.highlight("is triggered:" + primer.isTriggered(primerState));

        if (primer.isTriggered(primerState)) {
            //should unload right after fire, but for safe:
            iBreech.unloadShellImpl(level, primerShip, primerDir, breechPos);
            return;
        }

        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechPos);
        ShellTriggerHandler triggerHandler = ShellTriggerHandler.ofArtilleryOnShipOrGround(level, primerPos, artilleryShip);

        Dest<Double> totalEnergyDest = new Dest<>();
        Dest<ProjectileWrapper> projectileDest = new Dest<>();
        if (!triggerHandler.tryTrigger(totalEnergyDest, projectileDest)) {
            EzDebug.error("fail to trigger");
            return;
        }

        //breechBE.addBallistics(projectileDest.get(), primerShip, artilleryShip, totalEnergyDest.get());
        BallisticsServerMgr.addBallistics(level, projectileDest.get(), primerShip, artilleryShip, totalEnergyDest.get());

        PrimerBlock.removeConstraint(level, primerPos);
        iBreech.unloadShellImpl(level, primerShip, primerDir, breechPos);
    }

    /*@Override
    public Class<BreechBE> getBlockEntityClass() { return BreechBE.class; }
    @Override
    public BlockEntityType<? extends BreechBE> getBlockEntityType() { return WapBlockEntites.BREECH_BE.get(); }
     */
    /*@Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return ((level, blockPos, state, be) -> ((BreechBE)be).tick());
    }*/
}
