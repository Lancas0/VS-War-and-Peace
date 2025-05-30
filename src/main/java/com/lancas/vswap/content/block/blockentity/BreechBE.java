package com.lancas.vswap.content.block.blockentity;

/*
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.ballistics.BallisticsController;
import com.lancas.vs_wap.ship.ballistics.BallisticsServerMgr;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.*;

//todo saveable ballistics
public class BreechBE extends BlockEntity {
    private static final int COLD_DOWN_TICKS = 40;
    private int coldDown = 0;

    public boolean isCold() {
        return coldDown <= 0;
    }
    public void resetColdDown() {
        coldDown = COLD_DOWN_TICKS;
    }

    public BreechBE(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    //private final List<BallisticsController> ballisticsControllers = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
    public void addBallistics(ProjectileWrapper projectile, ServerShip propellantShip, @Nullable ServerShip artilleryShip, double propellantEnergy) {
        EzDebug.log("add Ballistics");
        if (level == null || level.isClientSide) {
            EzDebug.error("add ballistic while level:" + level);
            return;
        }

        //long breechShipId = breechShip == null ? -1 : breechShip.getId();
        //var bc = BallisticsController.apply((ServerLevel)level, projectile, propellantShip.getId(), breechShipId, propellantEnergy);
        //ballisticsControllers.add(bc);
        BallisticsServerMgr.addBallistics((ServerLevel)level, projectile, propellantShip, artilleryShip, propellantEnergy);
    }

    /*public void tick() {
        if (level == null || level.isClientSide) return;

        if (coldDown > 0) coldDown--;

        ballisticsControllers.removeIf(BallisticsController::isTerminated);
        for (var bc : ballisticsControllers) {
            bc.serverTick((ServerLevel)level);
            //todo remove bc and ship
        }
    }*./
}
*/