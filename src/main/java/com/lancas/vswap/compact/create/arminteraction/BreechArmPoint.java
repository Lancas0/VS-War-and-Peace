package com.lancas.vswap.compact.create.arminteraction;

import com.lancas.vswap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

public class BreechArmPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public BreechArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
        //artilleryShip = LazyShip.ofBlockPos(pos);
    }

    //private final LazyShip artilleryShip;

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return stack;
        if (!(level instanceof ServerLevel sLevel)) return stack;  //the ship deleting must be in server
        //todo the interface provider a method, getting if it can be loaded with arm
        BlockState breechState = level.getBlockState(pos);
        if (!(breechState.getBlock() instanceof IBreech breech)) {
            EzDebug.warn("Breech Arm Point is not on a breech");
            return stack;
        }
        if (!(stack.getItem() instanceof Docker)) {
            EzDebug.warn("the item is not docker");
            return stack;
        }

        /*if (!(sLevel.getBlockEntity(pos) instanceof BreechBE be)) {
            EzDebug.warn("Breech Arm Point should have a breech be");
            return stack;
        }
        BreechBE breechBE = (BreechBE)level.getBlockEntity(pos);
        if (!breechBE.isCold()) return stack;*/
        IBreech.BreechRecord record = BlockRecordRWMgr.getRecord(sLevel, pos);
        if (record == null) {
            EzDebug.warn("can't get breechRecord at " + pos.toShortString());
            return stack;
        }
        if (!record.isCold())  return stack;
        if (!breech.canLoadDockerNow(level, pos, stack)) {
            EzDebug.warn("the docker is not loadable");
            return stack;
        }


        Dest<Ship> prevMunitionShip = new Dest<>();
        Dest<Boolean> isPrevTriggered = new Dest<>();
        Dest<Direction> prevMunitionDirInShip = new Dest<>();
        //boolean hasPrevMunition = breech.getLoadedMunitionData(level, pos, prevMunitionShip, isPrevTriggered, prevMunitionDirInShip);

        /*if (hasPrevMunition && !isPrevTriggered.get()) {
            EzDebug.warn("has prev untriggered munition");
            return stack;  //has munition that's not triggered
        }*/

        IBreech iBreech = WorldUtil.getBlockInterface(sLevel, pos, null);
        if (iBreech == null) {
            EzDebug.warn("fail to get breech at " + pos.toShortString());
            return stack;
        }
        /*if (prevMunitionShip.hasValue() && !simulate) {
            iBreech.unloadShell(sLevel, (ServerShip)prevMunitionShip.get(), prevMunitionDirInShip.get(), pos);
        }*/

        ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, pos);
        Direction breechDir = DirectionAdder.getDirection(breechState);//JomlUtil.nearestDir(Objects.requireNonNull(docker.getLocalPivot(stack)));
        if (!simulate) {
            iBreech.loadMunition(sLevel, pos, breechState, stack);
            //record.loadDockerShip(sLevel, stack, artilleryShip, pos, breechDir);
            record.startColdDown();
        }
        return ItemStack.EMPTY;

        //Dest<Vector3d> placePos = new Dest<>();
        //Dest<Vector3d> placeDir = new Dest<>();
        //breech.loadMunition(sLevel, pos, breechState, placePos, placeDir);
        //return loadMunition(sLevel, stack, (ServerShip)prevMunitionShip.get(), prevMunitionDirInShip.get(), simulate);
    }

    /*private ItemStack loadMunition(ServerLevel sLevel, ItemStack stack, @Nullable ServerShip prevMunition, @Nullable Direction prevMunitionShipDir, boolean simulate) {
        IBreech iBreech = WorldUtil.getBlockInterface(sLevel, pos, null);

        if (prevMunition != null && !simulate) {
            iBreech.unloadShell(sLevel, prevMunition, prevMunitionShipDir, pos);
        }

        iBreech.loadMunition();

        if (!simulate) {
            //make ship since it's not simulated
            //Vector3dc worldBreechPos = ShipUtil.getWorldCenter(sLevel, breechBp);
            ServerShip newMunition = DockerItem.makeShipFromStackWithPool(sLevel, stack, newMunitionPlacePos, breechWorldForward);
            //todo pre check if have holdable
            var holdable = newMunition.getAttachment(HoldableAttachment.class);
            if (holdable == null) {
                ShipPool.getOrCreatePool(sLevel).returnShipAndSetEmpty(newMunition, ShipPool.ResetAndSet.farawayAndNoConstraint);
                newMunition = null;
            }

            if (newMunition == null) {
                EzDebug.warn("fail to load munition ship");
                return ItemStack.EMPTY;
            } else {
                EzDebug.highlight("successfully make ship and place at:" + newMunition.getTransform().getPositionInWorld());
            }

            Direction breechDirInWorldOrShip = sLevel.getBlockState(pos).getValue(DirectionAdder.FACING);
            //todo lock more effective, todo not foreach
            ServerShip finalNewMunition = newMunition;
            ShipBuilder.modify(sLevel, finalNewMunition).foreachBlock((curBp, state, be) -> {
                if (state.getBlock() instanceof PrimerBlock primer) {
                    PrimerBlock.createConstraints(sLevel, curBp,artilleryShip.get(sLevel), finalNewMunition, pos, breechDirInWorldOrShip, holdable);
                }
            });


            IBreech.BreechRecord record = BlockRecordRWMgr.getRecord(sLevel, pos);
            if (record == null) {
                EzDebug.warn("can't get breechRecord at " + pos.toShortString());
                return ItemStack.EMPTY;
            }
            record.startColdDown();
        }

        return ItemStack.EMPTY;
    }*/
}
