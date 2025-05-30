package com.lancas.vswap.obsolete.blocks;

import com.lancas.vswap.content.block.blocks.artillery.IBarrel;
import com.lancas.vswap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vswap.content.block.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vswap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vswap.content.block.blocks.cartridge.PrimerBlock;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.sandbox.ballistics.behaviour.BallisticBehaviour;
import com.lancas.vswap.sandbox.ballistics.data.AirDragSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticBarrelContextSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticInitialStateSubData;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.ship.feature.pool.ShipPool;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.RedstoneOnOffAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DroppingBreech extends BlockPlus implements IBreech, IBarrel/*, IBE<BreechBE>*/ {
    private static final List<IBlockAdder> adders = List.of(
        new DirectionAdder(false, true, ShapeBuilder.ofCubicRing(0, 0, 0, 2, 16).get()),
        new RedstoneOnOffAdder(true) {
            @Override
            public void onPoweredOnOff(Level level, BlockPos breechBp, BlockState state, boolean isOn) {
                if (!isOn || level.isClientSide) return;

                tryFire((ServerLevel)level, breechBp);

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
                /*Dest<IPrimer> primerDest = new Dest<>();
                Dest<BlockPos> primerBpDest = new Dest<>();
                Dest<Ship> primerShipDest = new Dest<>();*/
                /*if (findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest)) {
                    fire((ServerLevel)level, breechBp, (ServerShip)primerShipDest.get(), primerDest.get(), primerBpDest.get());
                    return;
                }*/
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
        IBreech.breechInteraction(),
        new RefreshBlockRecordAdder((level, bp, state) -> new BreechRecord(level, bp, 40))
    );
    @Override
    public List<IBlockAdder> getAdders() { return adders; }

    public DroppingBreech(Properties p_49795_) {
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
    public boolean canLoadDockerNow(Level level, BlockPos breechBp, ItemStack stack) {
        if (!(stack.getItem() instanceof Docker)) return false;
        return true;  //todo: further check if it's really a munition
    }

    @Override
    public void loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker) {
        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);

        Vector3dc worldBreechPos = WorldUtil.getWorldCenter(level, breechBp);
        Vector3dc worldLaunchDir = WorldUtil.getWorldDirection(level, breechBp, breechState.getValue(DirectionAdder.FACING));

        //ShipBuilder munitionBuilder = Docker.makeShipBuilderFromStack(level, munitionDocker);//DockerItem.makeShipFromStackWithPool(level, munitionDocker, placePos, placeDir);
        ShipBuilder munitionBuilder = Docker.makeVsShipBuilder(level, munitionDocker, true, true);
        if (munitionBuilder == null) {
            EzDebug.warn("fail get munition ship from stack");
            return;
        }
        ServerShip newMunition = munitionBuilder.get();
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

        Docker.setShipTransformByHoldable(munitionBuilder, holdable, worldBreechPos, worldLaunchDir);

        Direction breechDirInWorldOrShip = level.getBlockState(breechBp).getValue(DirectionAdder.FACING);
        //todo lock more effective, todo not foreach
        ServerShip finalNewMunition = newMunition;
        ShipBuilder.modify(level, finalNewMunition).foreachBlock((curBp, state, be) -> {
            if (state.getBlock() instanceof PrimerBlock primer) {
                PrimerBlock.createConstraints(level, curBp, artilleryShip, finalNewMunition, breechBp, breechDirInWorldOrShip, holdable);
            }
        });
    }

    @Override
    public void loadMunitionShip(ServerLevel level, BlockPos breechBp, BlockState breechState, ServerShip vsShip, boolean simulate) {
        EzDebug.fatal("should never be called");
    }

    @Override
    public void unloadShell(ServerLevel level, BlockPos breechBp, BlockState breechState) { }

    /*@Override
    public void unloadShell(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp) {
        Ship artilleryShip = ShipUtil.getShipAt(level, breechBp);

        ItemStack shellStack = ShipDataDocker.stackOfVs(level, shellShip);//DockerItem.stackOfShip(level, shellShip);
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
    }*/


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

    public static void tryFire(ServerLevel level, BlockPos breechPos/*, ServerShip primerShip, IPrimer primer, BlockPos primerPos*/) {
        //BlockState primerState = level.getBlockState(primerPos);
        //Direction primerDir = primerState.getValue(DirectionAdder.FACING);//directional.getDirection(primerState);
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(level, breechPos);
        //Vector3d worldLaunchDir = JomlUtil.dWorldNormal(primerShip.getShipToWorld(), primerDir);
        BlockState breechState = level.getBlockState(breechPos);
        //IBreech iBreech = WorldUtil.getBlockInterface(level, breechPos, null);

        //EzDebug.highlight("is triggered:" + primer.isTriggered(primerState));

        /*if (primer.isTriggered(primerState)) {
            //should unload right after fire, but for safe:
            //iBreech.unloadShell(level, primerShip, primerDir, breechPos);  //todo unload
            return;
        }*/

        Vector3d worldLaunchDir = WorldUtil.getWorldDirection(level, breechPos, breechState.getValue(DirectionAdder.FACING));


        BlockClusterData blockData = new BlockClusterData();
        Dest<Double> propellantEnergyDest = new Dest<>();
        boolean success = IBreech.foreachMunition(level, breechPos, new Vector3i(0, 0, 1), false, propellantEnergyDest, blockData);
        if (!success) {
            IBreech.clearLoadedMunition(level, breechPos);
            return;
        }
        //IBreech.clearLoadedMunition(level, breechPos);

        RigidbodyData rigidbodyData = new RigidbodyData(new TransformPrimitive(worldBreechPos, new Quaterniond().rotateTo(new Vector3d(0, 0, 1), worldLaunchDir), new Vector3d(1, 1, 1)));
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
        SandBoxServerWorld.addShipAndSyncClient(level, ship);
        //todo unload

        Vector3d throwDir = worldLaunchDir.negate(new Vector3d());
        Vector3d throwDeltaMove = throwDir.mul(0.2, new Vector3d());
        Vector3d spawnPos = worldBreechPos.add(throwDir, new Vector3d());

        IBreech.BreechRecord record = BlockRecordRWMgr.getRecord(level, breechPos);
        if (record == null) {
            EzDebug.warn("can't find breech record at " + breechPos.toShortString());
            return;
        }

        //ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechPos);
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        record.loadedShipData.stream().map(x -> saWorld.getShip(x.getFirst().getFirst())).filter(Objects::nonNull).forEach(s -> {
            if (s.getBlockCluster().getDataReader().getBlockCnt() <= 0) return;

            ItemStack shellStack = Docker.stackOfSa(level, s);//DockerItem.stackOfShip(level, shellShip);
            //ShipPool.getOrCreatePool(level).returnShipAndSetEmpty(shellShip, ShipPool.ResetAndSet.farawayAndNoConstraint);

            /*BlockEntity belowEntity = level.getBlockEntity(breechPos.below());
            if (belowEntity == null && artilleryShip != null)
                belowEntity = level.getBlockEntity(JomlUtil.worldBp(artilleryShip.getShipToWorld(), breechPos.below()));

            Vector3dc worldBelowPos = WorldUtil.getWorldCenter(level, breechPos.below());

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
            }*/

            ItemEntity itemE = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), shellStack);
            itemE.setDeltaMovement(throwDeltaMove.x, throwDeltaMove.y, throwDeltaMove.z);
            level.addFreshEntity(itemE);
        });

        IBreech.clearLoadedMunition(level, breechPos);


        /*Dest<Vector3i> projectileStartDest = new Dest<>();
        MunitionShipHandler.foreachPropellant(
            JomlUtil.i(primerPos),
            JomlUtil.iNormal(primerDir),
            posInShip -> level.getBlockState(JomlUtil.bp(posInShip))/.*shipSchemeRA.getBlockStateByLocalBp(JomlUtil.bp(recordPos))*./,
            (posInShip, prevPropellantState) -> {
                if (prevPropellantState.getBlock() instanceof IPropellant propellant)
                    level.setBlockAndUpdate(JomlUtil.bp(posInShip), propellant.getEmptyState(prevPropellantState));
                else
                    EzDebug.warn("can't get empty state of " + StrUtil.getBlockName(prevPropellantState));
            },
            propellantEnergyDest,
            projectileStartDest
        );
        //获取弹头部分，并分配到blockData
        MunitionShipHandler.foreachFromProjectileStart(
            projectileStartDest.get(),
            JomlUtil.iNormal(primerDir),
            p -> level.getBlockState(JomlUtil.bp(p)),//shipSchemeRA.getBlockStateByLocalBp(JomlUtil.bp(p)),
            (posInShip, state) -> {
                blockData.setBlock(
                    posInShip.sub(projectileStartDest.get(), new Vector3i()),  //以弹头开始点为初始点
                    state
                );
            },
            //clear projectile head
            (posInShip, state) -> level.setBlockAndUpdate(JomlUtil.bp(posInShip), Blocks.AIR.defaultBlockState())
        );


        //since no rotated is no rotated, the worldNoRotated is equal to local
        Vector3dc worldMunitionDirNoRotated = JomlUtil.dNormal(primerDir);

        //todo scale the projectile by breech scale
        //设置弹头初始位置在炮闩前面一格，防止检测不到炮管
        RigidbodyData rigidbodyData = new RigidbodyData(new TransformPrimitive(worldBreechPos.add(worldLaunchDir, new Vector3d()), new Quaterniond().rotateTo(worldMunitionDirNoRotated, worldLaunchDir), new Vector3d(1, 1, 1)));
        SandBoxServerShip ship = new SandBoxServerShip(
            UUID.randomUUID(),
            rigidbodyData,
            blockData
        );
        ship.addBehaviour(new BallisticBehaviour(), new BallisticData(
            new BallisticInitialStateSubData(worldBreechPos, JomlUtil.iNormal(primerDir), worldLaunchDir, propellantEnergyDest.get()),
            new BallisticBarrelContextSubData(),
            new AirDragSubData()
        ));
        SandBoxServerWorld.addShipAndSyncClient(level, ship);

        //set primer triggered
        primer.setAsTriggered(level, primerPos, primerState);

        iBreech.unloadShell(level, primerShip, primerDir, breechPos);*/

        /*@Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechPos);
        ShellTriggerHandler triggerHandler = ShellTriggerHandler.ofArtilleryOnShipOrGround(level, primerPos, artilleryShip);

        Dest<Double> totalEnergyDest = new Dest<>();
        Dest<ProjectileWrapper> projectileDest = new Dest<>();
        if (!triggerHandler.tryTrigger(totalEnergyDest, projectileDest)) {
            EzDebug.error("fail to trigger");
            return;
        }

        //breechBE.addBallistics(projectileDest.get(), primerShip, artilleryShip, totalEnergyDest.get());
        BallisticsServerMgr.addBallistics(level, projectileDest.get(), primerShip, artilleryShip, totalEnergyDest.get());
        iBreech.unloadShell(level, primerShip, primerDir, breechPos);*/

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
