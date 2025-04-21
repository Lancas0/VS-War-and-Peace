package com.lancas.vs_wap.compact.create.arminteraction;

import com.lancas.vs_wap.content.blockentity.BreechBE;
import com.lancas.vs_wap.content.blocks.artillery.IBreech;
import com.lancas.vs_wap.content.blocks.cartridge.PrimerBlock;
import com.lancas.vs_wap.content.items.docker.DockerItem;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.helper.LazyShip;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.util.WorldUtil;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

public class BreechArmPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public BreechArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
        artilleryShip = LazyShip.ofBlockPos(pos);
    }

    private final LazyShip artilleryShip;

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (!(level instanceof ServerLevel sLevel)) return stack;  //the ship deleting must be in server
        //todo the interface provider a method, getting if it can be loaded with arm
        BlockState breechState = level.getBlockState(pos);
        if (!(breechState.getBlock() instanceof IBreech breech)) {
            EzDebug.warn("Breech Arm Point is not on a breech");
            return stack;
        }

        if (!(sLevel.getBlockEntity(pos) instanceof BreechBE be)) {
            EzDebug.warn("Breech Arm Point should have a breech be");
            return stack;
        }
        BreechBE breechBE = (BreechBE)level.getBlockEntity(pos);
        if (!breechBE.isCold()) return stack;

        if (!breech.isDockerLoadable(level, pos, stack))
            return stack;


        Dest<Ship> prevMunitionShip = new Dest<>();
        Dest<Boolean> isPrevTriggered = new Dest<>();
        Dest<Direction> prevMunitionDirInShip = new Dest<>();
        boolean hasPrevMunition = breech.getLoadedMunitionData(level, pos, prevMunitionShip, isPrevTriggered, prevMunitionDirInShip);

        //EzDebug.light("get prevMunition:" + prevMunitionShip.get() + ", triggered:" + isPrevTriggered.get());

        if (hasPrevMunition && !isPrevTriggered.get()) {
            //EzDebug.warn("can not insert because has unTriggered prev");
            return stack;  //has munition that's not triggered
        }

        Dest<Vector3d> placePos = new Dest<>();
        Dest<Vector3d> placeDir = new Dest<>();
        breech.getMunitionPlaceData(sLevel, pos, breechState, placePos, placeDir);
        return loadMunition(sLevel, placePos.get(), placeDir.get(), stack, (ServerShip)prevMunitionShip.get(), prevMunitionDirInShip.get(), simulate);
    }

    private ItemStack loadMunition(ServerLevel sLevel, Vector3dc newMunitionPlacePos, Vector3dc breechWorldForward, ItemStack stack, @Nullable ServerShip prevMunition, @Nullable Direction prevMunitionShipDir, boolean simulate) {
        //has prevMunition and not simulate,
        if (prevMunition != null && !simulate) {
            IBreech iBreech = WorldUtil.getBlockInterface(sLevel, pos, null);
            iBreech.unloadShell(sLevel, prevMunition, prevMunitionShipDir, pos);
        }

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
            } else {
                EzDebug.highlight("successfully make ship and place at:" + newMunition.getTransform().getPositionInWorld());
            }

            Direction breechDirInWorldOrShip = sLevel.getBlockState(pos).getValue(DirectionAdder.FACING);
            //todo lock more effective, todo not foreach
            ServerShip finalNewMunition = newMunition;
            ShipBuilder.modify(sLevel, newMunition).foreachBlock((curBp, state, be) -> {
                /*if (be instanceof PrimerBE primerBE) {
                    primerBE.createConstraints((ServerShip)artilleryShip.get(sLevel), finalNewMunition, pos, /.*primerDir, primer.getPixelLength(),*./ breechDirInWorldOrShip, holdable);
                }*/
                if (state.getBlock() instanceof PrimerBlock primer) {
                    PrimerBlock.createConstraints(sLevel, curBp, (ServerShip)artilleryShip.get(sLevel), finalNewMunition, pos, breechDirInWorldOrShip, holdable);
                }
            });
            BreechBE breechBE = (BreechBE)level.getBlockEntity(pos);
            breechBE.resetColdDown();
        }

        return ItemStack.EMPTY;
    }
}
