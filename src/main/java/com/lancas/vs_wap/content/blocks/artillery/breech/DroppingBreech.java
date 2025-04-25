package com.lancas.vs_wap.content.blocks.artillery.breech;

import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vs_wap.content.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.content.items.docker.DockerItem;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.ballistics.BallisticsServerMgr;
import com.lancas.vs_wap.ship.ballistics.handler.ShellTriggerHandler;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.RedstonePoweredAdder;
import com.lancas.vs_wap.util.*;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

public class DroppingBreech extends BlockPlus implements IBreech/*, IBE<BreechBE>*/ {
    private static final List<IBlockAdder> adders = List.of(
        new DirectionAdder(false, true, ShapeBuilder.ofCubicRing(0, 0, 0, 2, 16).get()),
        new RedstonePoweredAdder((level, breechBp, state, hasSignal) -> {
            if (!hasSignal || level.isClientSide) return;

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
            Dest<Ship> primerShipDest = new Dest<>();

            if (findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest)) {
                fire((ServerLevel)level, breechBp, (ServerShip)primerShipDest.get(), primerDest.get(), primerBpDest.get());
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
        }, true),
        IBreech.breechInteraction(),
        new RefreshBlockRecordAdder(() -> new BreechRecord(40))
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return adders; }

    public DroppingBreech(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
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
    }

    @Override
    public boolean isDockerLoadable(Level level, BlockPos breechBp, ItemStack stack) {
        if (!(stack.getItem() instanceof DockerItem)) return false;
        //if (DockerItem.)
        return true;  //todo: further check if it's really a munition
    }

    @Override
    public void loadMunition(Level level, BlockPos breechBp, BlockState state, Dest<Vector3d> placePos, Dest<Vector3d> placeDir) {
        placePos.set(WorldUtil.getWorldCenter(level, breechBp));
        placeDir.set(WorldUtil.getWorldDirection(level, breechBp, state.getValue(DirectionAdder.FACING)));
    }

    @Override
    public void unloadShell(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp) {
        Ship artilleryShip = ShipUtil.getShipAt(level, breechBp);

        ItemStack shellStack = DockerItem.stackOfShip(level, shellShip);
        ShipPool.getOrCreatePool(level).returnShipAndSetEmpty(shellShip, ShipPool.ResetAndSet.farawayAndNoConstraint);

        BlockEntity belowEntity = level.getBlockEntity(breechBp.below());
        if (belowEntity == null && artilleryShip != null)
            belowEntity = level.getBlockEntity(JomlUtil.worldBp(artilleryShip.getShipToWorld(), breechBp.below()));

        Vector3dc worldBelowPos = WorldUtil.getWorldCenter(level, breechBp.below());

        if (belowEntity != null) {
            var cap = belowEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
            cap.ifPresent(handler -> {
                ItemStack remainStack = ItemHandlerHelper.insertItem(handler, shellStack.copy(), true);
                if (remainStack.isEmpty()) {  //actually insert
                    ItemHandlerHelper.insertItem(handler, shellStack, false);
                    return;
                }
                //should spawn item entity
                ItemEntity itemE = new ItemEntity(level, worldBelowPos.x(), worldBelowPos.y(), worldBelowPos.z(), shellStack);
                itemE.setDeltaMovement(0, -0.1, 0);
                level.addFreshEntity(itemE);
            });

            return;
        }

        ItemEntity itemE = new ItemEntity(level, worldBelowPos.x(), worldBelowPos.y(), worldBelowPos.z(), shellStack);
        itemE.setDeltaMovement(0, -0.1, 0);
        level.addFreshEntity(itemE);
    }


    private static boolean findPrimerAround(Level level, BlockPos breechBp, Dest<IPrimer> primerDest, Dest<BlockPos> primerBpDest, Dest<Ship> primerShipDest) {
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
    }

    private static void fire(ServerLevel level, BlockPos breechPos, ServerShip primerShip, IPrimer primer, BlockPos primerPos) {
        BlockState primerState = level.getBlockState(primerPos);
        Direction primerDir = primerState.getValue(DirectionAdder.FACING);//directional.getDirection(primerState);
        //Vector3d worldBreechPos = ShipUtil.getWorldCenter(level, breechPos);
        IBreech iBreech = WorldUtil.getBlockInterface(level, breechPos, null);

        EzDebug.highlight("is triggered:" + primer.isTriggered(primerState));

        if (primer.isTriggered(primerState)) {
            //should unload right after fire, but for safe:
            iBreech.unloadShell(level, primerShip, primerDir, breechPos);
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
        iBreech.unloadShell(level, primerShip, primerDir, breechPos);
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
