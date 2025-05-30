package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.lancas.vswap.content.block.blocks.artillery.IBarrel;
import com.lancas.vswap.content.block.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.sandbox.ballistics.behaviour.BallisticBehaviour;
import com.lancas.vswap.sandbox.ballistics.data.AirDragSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticBarrelContextSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticInitialStateSubData;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.RedstoneOnOffAdder;
import com.lancas.vswap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.UUID;

public class EjectingBreech extends BlockPlus implements IBreech, IBarrel/*, IBE<BreechBE>*/ {
    private static final List<IBlockAdder> providers = List.of(
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
                /*Dest<IPrimer> primerDest = new Dest<>();
                Dest<BlockPos> primerBpDest = new Dest<>();
                Dest<Ship> primerShipDest = new Dest<>();

                if (findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest)) {
                    fireOrEject((ServerLevel)level, breechBp, (ServerShip)primerShipDest.get(), primerDest.get(), primerBpDest.get());
                    return;
                }*/
                tryFire((ServerLevel) level, breechBp);
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
                Dest<Long> holdingShipId = new Dest<>();
                icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand, holdingShipId);
                ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId.get());
                if (holdingShip == null) return InteractionResult.PASS;

                iBreech.loadMunitionShip(sLevel, breechBp, breechState, holdingShip, true);

                //unhold ship and delete
                icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
                VSGameUtilsKt.getShipObjectWorld(sLevel).deleteShip(holdingShip);  //should be already removed in loadMunitionShip
                return InteractionResult.PASS;
            }
        },
        new RefreshBlockRecordAdder((level, bp, state) -> new BreechRecord(level, bp, 0))
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

    @Override
    public boolean canLoadDockerNow(Level level, BlockPos breechBp, ItemStack stack) {
        if (!(stack.getItem() instanceof Docker)) return false;
        //if (docker.getLocalPivot(stack) == null || docker.getLocalHoldForward(stack) == null) return false;
        //if (DockerItem.)
        return true;  //todo: further check if it's really a munition
    }

    @Override
    public void loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("fail to load munition because of failure to find breech record at " + breechBp.toShortString());
            return;
        }

        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);
        record.loadDockerShip(level, munitionDocker, artilleryShip, breechBp, breechState.getValue(DirectionAdder.FACING));
        /*@Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);

        Vector3dc worldBreechPos = WorldUtil.getWorldCenter(level, breechBp);
        Vector3dc worldLaunchDir = WorldUtil.getWorldDirection(level, breechBp, breechState.getValue(DirectionAdder.FACING));

        //todo don;t make ship
        //ServerShip newMunition = DockerItem.makeShipFromStackWithPool(level, munitionDocker, placePos, placeDir);
        ShipBuilder munitionBuilder = IDocker.makeShipBuilderFromStack(level, munitionDocker);
        if (munitionBuilder == null) {
            EzDebug.warn("can't get ship from docker item");
            return;
        }
        ServerShip newMunition = munitionBuilder.get();

        //todo pre check if have holdable
        //todo don't use pool?
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

        IDocker.setShipTransformByHoldable(munitionBuilder, holdable, worldBreechPos, worldLaunchDir);

        Direction breechDirInWorldOrShip = level.getBlockState(breechBp).getValue(DirectionAdder.FACING);
        //todo lock more effective, todo not foreach
        ServerShip finalNewMunition = newMunition;
        ShipBuilder.modify(level, finalNewMunition).foreachBlock((curBp, state, be) -> {
            if (state.getBlock() instanceof PrimerBlock primer) {
                PrimerBlock.createConstraints(level, curBp, artilleryShip, finalNewMunition, breechBp, breechDirInWorldOrShip, holdable);
            }
        });*/


    }

    @Override
    public void loadMunitionShip(ServerLevel level, BlockPos breechBp, BlockState breechState, ServerShip vsShip, boolean simulate) {
        //todo sometime(in face always) repeat invoke
        if (!(breechState.getBlock() instanceof IBreech)) {
            EzDebug.warn("the breech at " + breechBp.toShortString() + " is not a breech");
            return;
        }

        /*ICanHoldShip icanHoldShip = (ICanHoldShip)player;
        Dest<Long> holdingShipId = new Dest<>();
        icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand, holdingShipId);

        ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId.get());*/

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
        /*if (loaded) {
            icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
            VSGameUtilsKt.getShipObjectWorld(sLevel).deleteShip(holdingShip);
        }*/
    }

    @Override
    public void unloadShell(ServerLevel level, BlockPos breechBp, BlockState breechState) {
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(level, breechBp);  //todo use ship
        Vector3d worldLaunchDir = WorldUtil.getWorldDirection(level, breechBp, breechState.getValue(DirectionAdder.FACING));

        Vector3d throwDir = worldLaunchDir.negate(new Vector3d());
        Vector3d throwDeltaMove = throwDir.mul(0.2, new Vector3d());
        Vector3d spawnPos = worldBreechPos.add(throwDir, new Vector3d());

        IBreech.ejectAllMunition(level, breechBp, () -> spawnPos, () -> throwDeltaMove, true);

        /*Vector3d breechWorldPos = WorldUtil.getWorldCenter(level, breechBp);
        ShipBuilder.modify(level, shellShip)
            .moveFaceTo(shellDirInShip, breechWorldPos)
            .setLocalVelocity(JomlUtil.dNormal(shellDirInShip, -20));*/
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

    /*private static void fireOrEject(ServerLevel level, BlockPos breechPos, ServerShip primerShip, IPrimer primer, BlockPos primerPos) {
        //trigger the primer, or
        //throw primer ship with dir oppsite to the primer's dir
        /.*if (!(primer instanceof IDirectionalBlock directional)) {
            EzDebug.error("the primer is not directional:" + primer.getClass().getName());
            return;
        }*./

        BlockState primerState = level.getBlockState(primerPos);
        Direction primerDir = primerState.getValue(DirectionAdder.FACING);//directional.getDirection(primerState);
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(level, breechPos);
        Vector3d worldLaunchDir = JomlUtil.dWorldNormal(primerShip.getShipToWorld(), primerDir);
        IBreech iBreech = WorldUtil.getBlockInterface(level, breechPos, null);

        EzDebug.highlight("is triggered:" + primer.isTriggered(primerState));

        if (primer.isTriggered(primerState)) {
            //ejectShell(level, primerShip, primerDir, primerPos);
            //Vector3d ejectDir = JomlUtil.dWorldNormal(primerShip.getShipToWorld(), primerDir).negate();
            iBreech.unloadShell(level, primerShip, primerDir, breechPos);
        } else {
            BlockClusterData blockData = new BlockClusterData();

            Dest<Double> propellantEnergyDest = new Dest<>();
            Dest<Vector3i> projectileStartDest = new Dest<>();
            MunitionShipHandler.foreachPropellant(
                JomlUtil.i(primerPos),
                JomlUtil.iNormal(primerDir),
                posInShip -> level.getBlockState(JomlUtil.bp(posInShip))/*shipSchemeRA.getBlockStateByLocalBp(JomlUtil.bp(recordPos))*./,
                (posInShip, prevPropellantState) -> {
                    if (prevPropellantState.getBlock() instanceof IPropellant propellant)
                        level.setBlockAndUpdate(JomlUtil.bp(posInShip), propellant.getEmptyState(prevPropellantState));
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
            //设置弹头初始位置在炮闩前面一格，放在检测不到炮管
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
            level.setBlockAndUpdate(primerPos, primer.getTriggeredState(primerState));
            //todo remove constraints?


            /.*@Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechPos);
            //long artilleryShipOrGroundId = ShipUtil.idOfShipOrGround(level, artilleryShip);

            //double power = primer.trigger(level, primerPos, primerState);
            //Vector3i projectileStartPos = new Vector3i();

            ShellTriggerHandler triggerHandler = ShellTriggerHandler.ofArtilleryOnShipOrGround(level, primerPos, artilleryShip);

            Dest<Double> totalEnergyDest = new Dest<>();
            Dest<ProjectileWrapper> projectileDest = new Dest<>();
            if (!triggerHandler.tryTrigger(totalEnergyDest, projectileDest)) {
                EzDebug.error("fail to trigger");
                return;
            }

            BallisticsServerMgr.addBallistics(level, projectileDest.get(), primerShip, artilleryShip, totalEnergyDest.get());*./
            /.*EzDebug.Log("total power is " + power + ", >1E-20?:" + (power > 1E-20));
            if (power > 1E-20) {
                EzDebug.Log("try add reaction");
                Vector3d worldPrimerBackward = primerShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dOpposite(primerDir));
                EzDebug.Log("primer ship is loaded ship: " + (primerShip instanceof LoadedServerShip));
                CartridgeReactionController.appply(primerShip, worldPrimerBackward, power);
            }*./
        }
    }*/


    public static void tryFire(ServerLevel level, BlockPos breechPos) {
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(level, breechPos);
        BlockState breechState = level.getBlockState(breechPos);

        Vector3d worldLaunchDir = WorldUtil.getWorldDirection(level, breechPos, breechState.getValue(DirectionAdder.FACING));


        BlockClusterData blockData = new BlockClusterData();
        Dest<Double> propellantEnergyDest = new Dest<>();
        boolean success = IBreech.foreachMunition(level, breechPos, new Vector3i(0, 0, 1), false, propellantEnergyDest, blockData);
        if (!success) return;

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

        IBreech.ejectAllMunition(level, breechPos, () -> spawnPos, () -> throwDeltaMove, true);
        /*IBreech.BreechRecord record = BlockRecordRWMgr.getRecord(level, breechPos);
        if (record == null) {
            EzDebug.warn("can't find breech record at " + breechPos.toShortString());
            return;
        }

        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        record.loadedShipData.stream().map(x -> saWorld.getShip(x.getFirst().getFirst())).filter(Objects::nonNull).forEach(s -> {
            if (s.getBlockCluster().getDataReader().getBlockCnt() <= 0) return;

            ItemStack shellStack = ShipDataDocker.stackOfSa(level, s);

            ItemEntity itemE = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), shellStack);
            itemE.setDeltaMovement(throwDeltaMove.x, throwDeltaMove.y, throwDeltaMove.z);
            level.addFreshEntity(itemE);
        });

        IBreech.clearLoadedMunition(level, breechPos);*/
    }

    //public static final ShapeBuilder UP_SHAPE = ;
        /*.appendBox(0, 0, 0, 16, 16, 2)
        .appendBox(0, 0, 13, 16, 16, 16)
        .appendBox(0, 0, 2, 3, 16, 13)
        .appendBox(13, 0, 2, 16, 16, 13);*/
        //new ShapeBuilder(Shapes.block()).remove(ShapeBuilder.createPrism(Direction.UP, 10).move(0, 0, 1));
        //ShapeBuilder.createCubicRing(0, 0, 0, 3, 16)
        //    .remove(ShapeBuilder.prism(Direction.UP, 10).move(0, 0, -1));
            //.remove(ShapeBuilder.centerBlock(10).move(0, -1, 0));
        /*Shapes.join(
            ShapeUtil.cubicRing(0, 0, 0, 2, 6),
            ShapeUtil.cubicRing(0, 6, 0, 3, 10),
            BooleanOp.OR
        );*/
    /*@Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(FACING);
        return UP_SHAPE.getRotated(dir);
    }*/

    /*
    @Override
    public Class<BreechBE> getBlockEntityClass() { return BreechBE.class; }
    @Override
    public BlockEntityType<? extends BreechBE> getBlockEntityType() { return WapBlockEntites.BREECH_BE.get(); }
    */
    /*@Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return ((level, blockPos, state, be) -> ((BreechBE)be).tick());
    }*/



    /*@Override
    public Set<BlockPos> findBarrelWithBreechPoses(Level level, BlockPos breechPos, BlockState breechState) {

    }*/
}
