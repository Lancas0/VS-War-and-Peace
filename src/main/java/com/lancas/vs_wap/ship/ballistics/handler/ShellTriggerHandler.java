package com.lancas.vs_wap.ship.ballistics.handler;

import com.lancas.vs_wap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.content.block.blocks.cartridge.propellant.IPropellant;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.ship.feature.spilt.DirectionalSplitHandler;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Iterator;

public class ShellTriggerHandler {
    public static class PropellantIterable implements Iterable<BiTuple.BlockTuple>, Iterator<BiTuple.BlockTuple> {
        private final Level level;
        private final BlockPos primerBp;
        private final Direction primerDir;

        private BlockPos curBp;

        public PropellantIterable(Level inLevel, BlockPos inPrimerBp) {
            level = inLevel;
            primerBp = inPrimerBp;

            primerDir = level.getBlockState(primerBp).getValue(DirectionAdder.FACING);

            curBp = primerBp;
        }
        @Override
        public @NotNull Iterator<BiTuple.BlockTuple> iterator() { return this; }

        @Override
        public boolean hasNext() {
            if (level.getBlockState(curBp.relative(primerDir)).getBlock() instanceof IPropellant)
                return true;
            EzDebug.warn("has no next becasue next bp is " + StrUtil.getBlockName(level.getBlockState(curBp.relative(primerDir))));
            return false;
        }

        @Override
        public BiTuple.BlockTuple next() {
            curBp = curBp.relative(primerDir);
            return new BiTuple.BlockTuple(curBp, level.getBlockState(curBp));
        }
    }

    private final ServerLevel level;
    private final BlockPos primerBp;
    private final BlockState primerState;
    private final Direction primerDir;
    private final IPrimer primer;

    private final ServerShip shipPrimerIn;
    private final long artilleryOrGroundId;

    protected ShellTriggerHandler(ServerLevel inLevel, ServerShip inPrimerShip, BlockPos inPrimerBp, BlockState inPrimerState, long inArtilleryOrGroundId) {
        level = inLevel;
        primerBp = inPrimerBp;

        primerState = inPrimerState;
        primerDir = DirectionAdder.getDirection(primerState);
        primer = (IPrimer)primerState.getBlock();

        shipPrimerIn = inPrimerShip;
        artilleryOrGroundId = inArtilleryOrGroundId;
    }
    public static ShellTriggerHandler ofArtilleryOnGround(ServerLevel inLevel, BlockPos inPrimerBp) {
        BlockState primerState = inLevel.getBlockState(inPrimerBp);
        if (!(primerState.getBlock() instanceof IPrimer inPrimer)) {
            EzDebug.error("primer should implement IPrimer");
            return null;
        }

        if (!(ShipUtil.getShipAt(inLevel, inPrimerBp) instanceof ServerShip shipPrimerIn)) {
            EzDebug.error("primer should on ship.");
            return null;
        }

        Long groundId = ShipUtil.getGroundId(inLevel);
        if (groundId == null) {
            EzDebug.error("can't get gound id");
            return null;
        }

        return new ShellTriggerHandler(inLevel, shipPrimerIn, inPrimerBp, primerState, groundId);
    }
    public static ShellTriggerHandler ofArtilleryOnShip(ServerLevel inLevel, BlockPos inPrimerBp, ServerShip artilleryShip) {
        BlockState primerState = inLevel.getBlockState(inPrimerBp);
        if (!(primerState.getBlock() instanceof IPrimer inPrimer)) {
            EzDebug.error("primer should implement IPrimer");
            return null;
        }
        if (!(ShipUtil.getShipAt(inLevel, inPrimerBp) instanceof ServerShip shipPrimerIn)) {
            EzDebug.error("primer should on ship.");
            return null;
        }
        if (artilleryShip == null) {
            EzDebug.error("artillery ship is null.");
            return null;
        }

        return new ShellTriggerHandler(inLevel, shipPrimerIn, inPrimerBp, primerState, artilleryShip.getId());
    }
    public static ShellTriggerHandler ofArtilleryOnShipOrGround(ServerLevel inLevel, BlockPos inPrimerBp, ServerShip artilleryShip) {
        return artilleryShip == null ?
            ofArtilleryOnGround(inLevel, inPrimerBp) :
            ofArtilleryOnShip(inLevel, inPrimerBp, artilleryShip);
    }

    public boolean tryTrigger(Dest<Double> totalEnergyDest, Dest<ProjectileWrapper> projectileDest) {
        if (primer.isTriggered(primerState)) {
            EzDebug.light("return by triggered");
            return false;
        }

        //只要开始trigger了，就一定需要设置triggered为true
        //相当于就算子弹哑火，底火也不能再用了
        level.setBlockAndUpdate(primerBp, primer.getTriggeredState(primerState));
        //todo remove constraints?

        Dest<BlockPos> propellantEndBpDest = new Dest<>();
        getPropellantData(level, primerBp, true, totalEnergyDest, propellantEndBpDest);
        if (totalEnergyDest.get() < 1E-20) {
            EzDebug.light("return by no energy");
            return false;
        }


        ProjectileWrapper projectile = makeProjectileShip(propellantEndBpDest.get());
        if (projectile == null) {
            EzDebug.warn("fail to create projectile ship.");
            return false;
        }

        EzDebug.highlight("new projectile id:" + projectile.shipId);

        //sometimes disable the collision with the shell and artillery to help align
        //now re able the collision
        VSGameUtilsKt.getShipObjectWorld(level).enableCollisionBetweenBodies(shipPrimerIn.getId(), artilleryOrGroundId);


        projectileDest.set(projectile);
        return true;
    }
    private void getPropellantData(ServerLevel level, BlockPos primerBp, boolean setEmpty, Dest<Double> totalEnergyDest, Dest<BlockPos> propellantEndBpDest) {
        double totalEnergy = 0;

        for (var tuple : new PropellantIterable(level, primerBp)) {
            BlockPos propellantBp = tuple.getBlockPos();
            BlockState propellantState = tuple.getBlockState();

            //everytime set a val so at end it is the propellantEndBp
            propellantEndBpDest.set(propellantBp);

            if (!(propellantState.getBlock() instanceof IPropellant propellant)) {
                EzDebug.warn("the block is not propellant, skip it.");
                continue;
            }
            //EzDebug.log("bp:" + propellantBp + ", block:" + propellantState.getBlock().getName().getString() + ", power:" + propellant.getEnergy());

            if (!propellant.isEmpty(propellantState)) {
                totalEnergy += propellant.getEnergy(propellantState);

                if (setEmpty)
                    level.setBlockAndUpdate(propellantBp, propellant.getEmptyState(propellantState));
            }
        }

        totalEnergyDest.set(totalEnergy);
    }
    private ProjectileWrapper makeProjectileShip(BlockPos propellantEndBp) {
        //ShipBuilder shipBuilder = ShipPool.getOrCreatePool(level).getOrCreateEmptyShipBuilder();
        //todo don't use pool because the attachment is not stable
        ShipBuilder shipBuilder = DirectionalSplitHandler.trySplit(level, propellantEndBp, primerDir/*, shipBuilder*/);
        DirectionalSplitHandler.trySplit(level, propellantEndBp, primerDir, shipBuilder);

        if (shipBuilder == null || shipBuilder.isEmpty()) {
            EzDebug.warn("fail to split projectile part");
            return null;
        }

        EzDebug.highlight("get projectile id:" + shipBuilder.getId());
        //todo 将方向设置为炮闩方向，然后初始化的时候设置旋转，有时炮弹未即时更新会导致发射方向不对
        //todo if not valid, 炸膛？
        return ProjectileWrapper.ofIfValid(shipBuilder.get(), primerDir);
    }
    /*
    private BlockPos getProjectileTailBp() {
        //The blockstate changes after triggered.
        BlockPos curBp = primerBp;
        while (true) {
            curBp = curBp.relative(primerDir);
            BlockState curState = level.getBlockState(curBp);

            if (!(curState.getBlock() instanceof IPropellant)) {
                return curBp;  //now curBp is not propellant
            }
        }
        /.*  why this not working?
        BlockPos curBp = primerBp.relative(primerDir);
        while (level.getBlockState(curBp) instanceof IPropellant) {
            curBp = primerBp.relative(primerDir);
        }
        return curBp;*./
    }*/
}
