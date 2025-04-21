package com.lancas.vs_wap.compact.create.arminteraction;

import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.content.blocks.cartridge.ShellFrame;
import com.lancas.vs_wap.content.items.docker.IDocker;
import com.lancas.vs_wap.content.items.docker.RefDocker;
import com.lancas.vs_wap.content.saved.BlockRecordRWMgr;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
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

public class ShellFrameArmPoint extends ArmInteractionPoint {
    public ShellFrameArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public int getSlotCount() { return 1; }

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (!(level instanceof ServerLevel sLevel)) return stack;  //the ship deleting must be in server
        //todo the interface provider a method, getting if it can be insert to shellFrame with arm
        if (!(stack.getItem() instanceof RefDocker refDocker)) return stack;

        ShellFrame.ShellFrameRecord record = BlockRecordRWMgr.getRecord(sLevel, pos);
        if (record == null) {
            EzDebug.warn("shell frame has no record at " + pos.toShortString());
            return stack;
        }
        /*if (!(sLevel.getBlockEntity(pos) instanceof ShellFrameBE be)) {
            EzDebug.warn("Breech Arm Point is not on a breech");
            return stack;
        }*/
        //if (!be.isCold()) return stack;
        if (record.lockingShipId >= 0) return stack;  //already has one ship in it

        //todo check if the will-generated ship can be lock during simulate
        //create the ship if not simulated
        if (!simulate) {
            BlockState state = sLevel.getBlockState(pos);
            Vector3d shellFrameWorldPos = WorldUtil.getWorldCenter(sLevel, pos);
            Vector3d shellFrameWorldDir = WorldUtil.getWorldDirection(sLevel, pos, DirectionAdder.getDirection(state));

            ShipBuilder shipBuilder = refDocker.makeShipBuilderFromStack(sLevel, stack);
            HoldableAttachment holdable = shipBuilder.get().getAttachment(HoldableAttachment.class);
            IDocker.setShipTransformByHoldable(shipBuilder, holdable, shellFrameWorldPos, shellFrameWorldDir);

            //ServerShip madeShip = DockerItem.makeShipFromStackWithPool(sLevel, stack, shellFrameWorldPos, shellFrameWorldDir);
            /*if (shipBuilder == null) {
                EzDebug.error("fail to made ship");
                return stack;
            }*/

            //DebugShipPos.shipIds.add(shipBuilder.getId());
            ShellFrame.lockShip(sLevel, shipBuilder.get(), pos);

            //be.resetColdDown();
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

        /*if (!(sLevel.getBlockEntity(pos) instanceof ShellFrameBE be)) {
            EzDebug.warn("Breech Arm Point is not on a breech");
            return ItemStack.EMPTY;
        }*/
        ShellFrame.ShellFrameRecord record = BlockRecordRWMgr.getRecord(sLevel, pos);
        if (record == null) {
            EzDebug.warn("shell frame has no record at " + pos.toShortString());
            return ItemStack.EMPTY;
        }

        if (record.lockingShipId < 0) return ItemStack.EMPTY;

        //todo return a loadable docker, and do not really get the actual docker when simulating
        if (simulate) return WapItems.Docker.REF_DOCKER.asStack();  //todo loadable docker


        long releasedShip = ShellFrame.releaseShip(sLevel, pos);
        ServerShip ship = ShipUtil.getServerShipByID(sLevel, releasedShip);
        if (ship == null) {
            EzDebug.warn("the released ship id is " + releasedShip + " but get null ship");
            return ItemStack.EMPTY;
        }
        ItemStack extractStack = RefDocker.stackOf(sLevel, ship);//DockerItem.stackOfShip(sLevel, ship);
        //ShipPool.ResetAndSet.farawayAndNoConstraint.accept(sLevel, ship);
        //ShipUtil.deleteShip(sLevel, ship);
        //todo use pool
        //ShipPool.getOrCreatePool(sLevel).returnShipAndSetEmpty(ship, ShipPool.DefaultReset.farawayAndNoConstraint);
        return extractStack;
    }
}
