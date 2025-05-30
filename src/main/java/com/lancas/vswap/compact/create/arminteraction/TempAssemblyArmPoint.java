package com.lancas.vswap.compact.create.arminteraction;

/*
import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.content.items.docker.DockerItem;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.WorldUtil;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;

public class TempAssemblyArmPoint extends ArmInteractionPoint {
    public TempAssemblyArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public int getSlotCount() { return 1; }

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (!(level instanceof ServerLevel sLevel)) return stack;  //the ship deleting must be in server
        //todo the interface provider a method, getting if it can be insert to shellFrame with arm
        if (!(stack.getItem() instanceof DockerItem)) return stack;

        if (!(sLevel.getBlockEntity(pos) instanceof ShellFrameBE be)) {
            EzDebug.warn("Breech Arm Point is not on a breech");
            return stack;
        }
        if (!be.isCold()) return stack;
        if (be.lockingShipId >= 0) return stack;  //already has one ship in it

        //todo check if the will-generated ship can be lock during simulate
        //create the ship if not simulated
        if (!simulate) {
            BlockState state = sLevel.getBlockState(pos);
            Vector3d shellFrameWorldPos = WorldUtil.getWorldCenter(sLevel, pos);
            Vector3d shellFrameWorldDir = WorldUtil.getWorldDirection(sLevel, pos, DirectionAdder.getDirection(state));
            ServerShip madeShip = DockerItem.makeShipFromStackWithPool(sLevel, stack, shellFrameWorldPos, shellFrameWorldDir);
            be.lockShip(madeShip);

            be.resetColdDown();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(int slot, boolean simulate) {
        return this.extract(slot, 1, simulate);
    }
    @Override
    public ItemStack extract(int slot, int amount, boolean simulate) {
        if (!(level instanceof ServerLevel sLevel)) return ItemStack.EMPTY;  //the ship deleting must be in server

        if (!(sLevel.getBlockEntity(pos) instanceof ShellFrameBE be)) {
            EzDebug.warn("Breech Arm Point is not on a breech");
            return ItemStack.EMPTY;
        }
        if (be.lockingShipId < 0) return ItemStack.EMPTY;

        //return a loadable docker, do not really get the actual docker when simulating
        if (simulate) return WapItems.DOCKER.asStack();  //todo loadable docker


        long releasedShip = be.releaseShip();
        ServerShip ship = ShipUtil.getServerShipByID(sLevel, releasedShip);
        if (ship == null) {
            EzDebug.warn("the released ship id is " + releasedShip + " but get null ship");
            return ItemStack.EMPTY;
        }
        ItemStack extractStack = DockerItem.stackOfShip(sLevel, ship);
        //ShipUtil.deleteShip(sLevel, ship);
        //todo use pool
        ShipPool.getOrCreatePool(sLevel).returnShipAndSetEmpty(ship, ShipPool.DefaultReset.farawayAndNoConstraint);
        return extractStack;
    }
}
*/