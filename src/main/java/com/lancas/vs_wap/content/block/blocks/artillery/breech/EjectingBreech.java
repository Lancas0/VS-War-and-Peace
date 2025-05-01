package com.lancas.vs_wap.content.block.blocks.artillery.breech;

import com.lancas.vs_wap.content.block.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vs_wap.content.block.blocks.cartridge.PrimerBlock;
import com.lancas.vs_wap.content.items.docker.DockerItem;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.ballistics.BallisticsServerMgr;
import com.lancas.vs_wap.ship.ballistics.handler.ShellTriggerHandler;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.RedstoneOnOffAdder;
import com.lancas.vs_wap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShapeBuilder;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

public class EjectingBreech extends BlockPlus implements IBreech/*, IBE<BreechBE>*/ {
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
                Dest<IPrimer> primerDest = new Dest<>();
                Dest<BlockPos> primerBpDest = new Dest<>();
                Dest<Ship> primerShipDest = new Dest<>();

                if (findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest)) {
                    handlePrimer((ServerLevel)level, breechBp, (ServerShip)primerShipDest.get(), primerDest.get(), primerBpDest.get());
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
        IBreech.breechInteraction(),
        new RefreshBlockRecordAdder(() -> new BreechRecord(40))
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return providers; }

    public EjectingBreech(Properties p_49795_) {
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
    public void loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker) {
        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechBp);

        Vector3dc placePos = WorldUtil.getWorldCenter(level, breechBp);
        Vector3dc placeDir = WorldUtil.getWorldDirection(level, breechBp, breechState.getValue(DirectionAdder.FACING));

        ServerShip newMunition = DockerItem.makeShipFromStackWithPool(level, munitionDocker, placePos, placeDir);
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
        ShipBuilder.modify(level, finalNewMunition).foreachBlock((curBp, state, be) -> {
            if (state.getBlock() instanceof PrimerBlock primer) {
                PrimerBlock.createConstraints(level, curBp, artilleryShip, finalNewMunition, breechBp, breechDirInWorldOrShip, holdable);
            }
        });
    }

    @Override
    public void unloadShell(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp) {
        Vector3d breechWorldPos = WorldUtil.getWorldCenter(level, breechBp);

        ShipBuilder.modify(level, shellShip)
            .moveFaceTo(shellDirInShip, breechWorldPos)
            .setLocalVelocity(JomlUtil.dNormal(shellDirInShip, -20));
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

    private static void handlePrimer(ServerLevel level, BlockPos breechPos, ServerShip primerShip, IPrimer primer, BlockPos primerPos) {
        //trigger the primer, or
        //throw primer ship with dir oppsite to the primer's dir
        /*if (!(primer instanceof IDirectionalBlock directional)) {
            EzDebug.error("the primer is not directional:" + primer.getClass().getName());
            return;
        }*/

        BlockState primerState = level.getBlockState(primerPos);
        Direction primerDir = primerState.getValue(DirectionAdder.FACING);//directional.getDirection(primerState);
        //Vector3d worldBreechPos = ShipUtil.getWorldCenter(level, breechPos);
        IBreech iBreech = WorldUtil.getBlockInterface(level, breechPos, null);

        EzDebug.highlight("is triggered:" + primer.isTriggered(primerState));

        if (primer.isTriggered(primerState)) {
            //ejectShell(level, primerShip, primerDir, primerPos);
            Vector3d ejectDir = JomlUtil.dWorldNormal(primerShip.getShipToWorld(), primerDir).negate();
            iBreech.unloadShell(level, primerShip, primerDir, breechPos);
        } else {
            @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, breechPos);
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

            BallisticsServerMgr.addBallistics(level, projectileDest.get(), primerShip, artilleryShip, totalEnergyDest.get());
            /*EzDebug.Log("total power is " + power + ", >1E-20?:" + (power > 1E-20));
            if (power > 1E-20) {
                EzDebug.Log("try add reaction");
                Vector3d worldPrimerBackward = primerShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dOpposite(primerDir));
                EzDebug.Log("primer ship is loaded ship: " + (primerShip instanceof LoadedServerShip));
                CartridgeReactionController.appply(primerShip, worldPrimerBackward, power);
            }*/
        }
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
