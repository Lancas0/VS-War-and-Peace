package com.lancas.vs_wap.content.block.blocks.artillery.breech;

/*
import com.lancas.vs_wap.content.block.blocks.artillery.IBarrel;
import com.lancas.vs_wap.content.block.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vs_wap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.content.block.blocks.cartridge.PrimerBlock;
import com.lancas.vs_wap.content.block.blocks.cartridge.propellant.IPropellant;
import com.lancas.vs_wap.content.item.items.docker.ShipDataDocker;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.handler.MunitionShipHandler;
import com.lancas.vs_wap.sandbox.ballistics.behaviour.BallisticBehaviour;
import com.lancas.vs_wap.sandbox.ballistics.data.AirDragSubData;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticBarrelContextSubData;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticInitialStateSubData;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxExpireTicker;
import com.lancas.vs_wap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.ExpireTickerData;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RapidBreech extends BlockPlus implements IBreech, IBarrel {
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(RapidBreech.class,
            () -> List.of(
                new DirectionAdder(false, true, ShapeBuilder.ofCubicRing(0, 0, 0, 2, 16).get()),
                //IBreech.breechInteraction(),  //not interactable, or todo: fire immedate interact
                /.*new InteractableBlockAdder() {
                    @Override
                    public InteractionResult onInteracted(BlockState breechState, Level level, BlockPos breechBp, Player player, InteractionHand hand, BlockHitResult hit) {
                        //todo sometime(in face always) repeat invoke
                        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;

                        ICanHoldShip icanHoldShip = (ICanHoldShip)player;
                        Dest<Long> holdingShipId = new Dest<>();
                        icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand, holdingShipId);

                        ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId.get());
                        if (holdingShip == null) return InteractionResult.PASS;  //not holding ship
                        var munitionHoldable = holdingShip.getAttachment(HoldableAttachment.class);
                        if (munitionHoldable == null) return InteractionResult.PASS;  //no holdable

                        AtomicBoolean shouldLoad = new AtomicBoolean(false);
                        AtomicReference<BlockPos> primerBp = new AtomicReference<>(null);
                        AtomicReference<Direction> primerDir = new AtomicReference<>(null);
                        //todo not foreach ship
                        ShipBuilder.modify(sLevel, holdingShip).foreachBlock((posInShip, stateInShip, blockEntity) -> {
                            if (shouldLoad.get()) return;

                            if (stateInShip.getBlock() instanceof PrimerBlock primer) {  //todo not support other primer now
                                if (!primer.isTriggered(stateInShip)) {
                                    shouldLoad.set(true);
                                    primerBp.set(posInShip);
                                    primerDir.set(stateInShip.getValue(DirectionAdder.FACING));
                                }
                            }
                        });
                        if (!shouldLoad.get()) return InteractionResult.PASS;

                        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, breechBp);

                        Direction breechDir = DirectionAdder.getDirection(breechState);  //todo use IBreech to get breech dir?
                        if (breechDir == null) {
                            EzDebug.fatal("can not get direction of breech");
                            return InteractionResult.FAIL;
                        }

                        icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
                        VSGameUtilsKt.getShipObjectWorld(sLevel).deleteShip(holdingShip);  //remove the ship as if it's loaded into breech

                        Vector3dc worldBreechPos = WorldUtil.getWorldCenter(level, breechBp);
                        Vector3dc worldLaunchDir = WorldUtil.getWorldDirection(level, breechBp, breechState.getValue(DirectionAdder.FACING));

                        BlockClusterData blockData = new BlockClusterData();

                        Dest<Double> propellantEnergyDest = new Dest<>();
                        Dest<Vector3i> projectileStartDest = new Dest<>();
                        MunitionShipHandler.foreachPropellant(
                            JomlUtil.i(primerBp.get()),
                            JomlUtil.iNormal(primerDir.get()),
                            posInShip -> level.getBlockState(JomlUtil.bp(posInShip)),
                            null,  //no need to set empty (todo spawn empty shell docker or something)
                            propellantEnergyDest,
                            projectileStartDest
                        );
                        //获取弹头部分，并分配到blockData
                        MunitionShipHandler.foreachFromProjectileStart(
                            projectileStartDest.get(),
                            JomlUtil.iNormal(primerDir.get()),
                            p -> level.getBlockState(JomlUtil.bp(p)),
                            (recordPos, state) -> {
                                blockData.setBlock(
                                    recordPos.sub(projectileStartDest.get(), new Vector3i()),  //以弹头开始点为初始点
                                    state
                                );
                            },
                            null
                        );

                        //since no rotated is no rotated, the worldNoRotated is equal to local
                        Vector3dc worldMunitionDirNoRotated = JomlUtil.dNormal(primerDir.get());

                        //todo scale the projectile by breech scale
                        RigidbodyData rigidbodyData = new RigidbodyData(new TransformPrimitive(worldBreechPos, new Quaterniond().rotateTo(worldMunitionDirNoRotated, worldLaunchDir), new Vector3d(1, 1, 1)));
                        SandBoxServerShip ship = new SandBoxServerShip(
                            UUID.randomUUID(),
                            rigidbodyData,
                            blockData
                        );
                        ship.addBehaviour(new BallisticBehaviour(), new BallisticData(
                            new BallisticInitialStateSubData(worldBreechPos, JomlUtil.iNormal(primerDir.get()), worldLaunchDir, propellantEnergyDest.get()),
                            new BallisticBarrelContextSubData(),
                            new AirDragSubData()
                        ));
                        SandBoxServerWorld.addShipAndSyncClient(sLevel, ship);

                        return InteractionResult.PASS;
                    }
                },*./
                new RefreshBlockRecordAdder((bp, state) -> new IBreech.BreechRecord(0))
            )
        );
    }
    public RapidBreech(Properties p_49795_) { super(p_49795_); }


    @Override
    public boolean getLoadedMunitionData(Level level, BlockPos breechBp, Dest<Ship> munitionShip, Dest<Boolean> isTriggered, Dest<Direction> munitionDirInShip) {
        return false;  //there don't exist loaded munition for rapid breech
    }
    @Override
    public boolean isDockerLoadable(Level level, BlockPos breechBp, ItemStack stack) {
        return stack.getItem() instanceof ShipDataDocker;  //todo
    }
    /.*@Override
    public void loadMunition(Level level, BlockPos breechBp, BlockState breechState, ItemStack stack) { //todo remain itemStack?
        //directly fire
        var shipScheme = DockerItem.getShipSchemeData(stack);
        if (shipScheme == null) {
            EzDebug.warn("the item has no shipSchemeData!");
            return;
        }

        //shipScheme.foreachBlock();
    }*./
    @Override
    public void unloadShell(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp) {
        Ship artilleryShip = ShipUtil.getShipAt(level, breechBp);

        ItemStack shellStack = ShipDataDocker.stackOfVs(level, shellShip);
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
    @Override
    public void loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionStack) {
        if (!(munitionStack.getItem() instanceof ShipDataDocker docker)) return;

        //directly fire
        var shipSchemeReader = docker.getShipDataReader(munitionStack);
        if (shipSchemeReader == null) {
            EzDebug.warn("the item has no shipSchemeData!");
            return;
        }

        Vector3dc worldBreechPos = WorldUtil.getWorldCenter(level, breechBp);
        Vector3dc worldLaunchDir = WorldUtil.getWorldDirection(level, breechBp, breechState.getValue(DirectionAdder.FACING));
        //SandBoxTransformData transformData = new SandBoxTransformData().setPos(worldBreechPos).setRotation(new Quaterniond());  //todo use factory avoid destroycal data setting?
        BlockClusterData projectileBlockData = new BlockClusterData();
        BlockClusterData casingBlockData = new BlockClusterData();

        //我估计遍历两次和记录一次后再遍历一次差不多
        Vector3ic[] primerRecordPos = new Vector3ic[] { null };
        Vector3ic[] munitionLocalDir = new Vector3ic[] { null };

        shipSchemeReader.foreachBlockInLocal((recordPos, state) -> {
            if (state.getBlock() instanceof IPrimer) {
                Direction primerDir = state.getValue(DirectionAdder.FACING);

                primerRecordPos[0] = JomlUtil.i(recordPos);
                munitionLocalDir[0] = JomlUtil.iNormal(primerDir);
                casingBlockData.setBlock(new Vector3i(), state);
                //EzDebug.log("primer record pos:" + StrUtil.poslike(recordPos) + ", dir:" + primerDir);
            }

            //EzDebug.log("have block:" + StrUtil.poslike(recordPos) + ", state:" + StrUtil.getBlockName(state));
        });
        if (primerRecordPos[0] == null) {  //没找到底火
            EzDebug.warn("fail to find primer");
            return;  //don't create ship
        }

        //获取发射药总能量
        //Vector3i curRecordPos = new Vector3i(primerRecordPos[0]).add(munitionLocalDir[0]);
        Dest<Double> propellantEnergyDest = new Dest<>();
        Dest<Vector3i> projectileStartDest = new Dest<>();
        MunitionShipHandler.foreachPropellant(
            primerRecordPos[0],
            munitionLocalDir[0],
            recordPos -> shipSchemeReader.getBlockStateByLocalBp(JomlUtil.bp(recordPos)),
            (recordPos, state) -> {
                //EzDebug.log("get propellant state:" + StrUtil.getBlockName(state));
                if (state.getBlock() instanceof IPropellant propellant)
                    casingBlockData.setBlock(recordPos.sub(primerRecordPos[0], new Vector3i()), propellant.getEmptyState(state));
            },  //no need to set empty (todo spawn empty shell docker or something)
            propellantEnergyDest,
            projectileStartDest
        );
        //获取弹头部分，并分配到blockData
        MunitionShipHandler.foreachFromProjectileStart(
            projectileStartDest.get(),
            munitionLocalDir[0],
            p -> shipSchemeReader.getBlockStateByLocalBp(JomlUtil.bp(p)),
            (recordPos, state) -> {
                projectileBlockData.setBlock(
                    recordPos.sub(projectileStartDest.get(), new Vector3i()),  //以弹头开始点为初始点
                    state
                );
            },
            null
        );

        //since no rotated is no rotated, the worldNoRotated is equal to local
        Vector3dc worldMunitionDirNoRotated = JomlUtil.d(munitionLocalDir[0]);
        Quaterniond shellInitialRotation = new Quaterniond().rotateTo(worldMunitionDirNoRotated, worldLaunchDir);

        //todo scale the projectile by breech scale
        RigidbodyData rigidbodyData = new RigidbodyData(new TransformPrimitive(worldBreechPos, shellInitialRotation, new Vector3d(1, 1, 1)));
        SandBoxServerShip ship = new SandBoxServerShip(
            UUID.randomUUID(),
            rigidbodyData,
            projectileBlockData
        );
        ship.addBehaviour(new BallisticBehaviour(), new BallisticData(
            new BallisticInitialStateSubData(worldBreechPos, munitionLocalDir[0], worldLaunchDir, propellantEnergyDest.get()),
            new BallisticBarrelContextSubData(),
            new AirDragSubData()
        ));
        SandBoxServerWorld.addShipAndSyncClient(level, ship);

        //todo scale the casing by breech scale
        RigidbodyData casingRigidbody =
            new RigidbodyData(new TransformPrimitive(worldBreechPos, shellInitialRotation, new Vector3d(1, 1, 1)));
        casingRigidbody.setEarthGravity();
        casingRigidbody.setVelocity(worldLaunchDir.normalize(-RandUtil.nextG(8, 2.5), new Vector3d()));
        casingRigidbody.setOmega(RandUtil.onRandSphere(2, 5));

        SandBoxServerShip casingShip = new SandBoxServerShip(
            UUID.randomUUID(),
            casingRigidbody,
            casingBlockData
        );
        casingShip.addBehaviour(new SandBoxExpireTicker(), new ExpireTickerData(40));
        SandBoxServerWorld.addShipAndSyncClient(level, casingShip);
    }
}
*/