package com.lancas.vs_wap.obsolete.ship;

/*
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.content.blocks.artillery.IBarrel;
import com.lancas.vs_wap.content.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.content.blocks.cartridge.propellant.IPropellant;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsMath;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.HashSet;


public class BallisticsCalculation {
    public static final double PHYS_FRAME_TIME = 0.016;
    public static final Vector3dc GRAVITY = new Vector3d(0, -10, 0);

    public static double calculateEnergyFromPrimer(Level level, BlockPos primerBp, Direction primerDir) {
        if (!(level.getBlockState(primerBp).getBlock() instanceof IPrimer))
            return 0.0;

        double totalEnergy = 0.0;
        BlockPos curPropellantBp = primerBp;
        while (true) {
            curPropellantBp = curPropellantBp.relative(primerDir);
            BlockState propellantState = level.getBlockState(curPropellantBp);

            if (propellantState.getBlock() instanceof IPropellant propellant) {
                if (!propellant.isEmpty(propellantState)) {
                    /.*if (consumePropellant) {
                        totalEnergy += propellant.getEnergy();
                    }*./
                    totalEnergy += propellant.getPower(propellantState);
                }
            } else {
                return totalEnergy;
            }
        }
    }
    public static BlockPos calculateBallisticHead(Level level, BlockPos primerBp, Direction primerDir) {
        BlockPos curBp = primerBp;
        while (true) {
            curBp = curBp.relative(primerDir);
            BlockState curState = level.getBlockState(curBp);

            if (!(curState.getBlock() instanceof IPropellant)) {
                return curBp;  //now curBp is not propellant
            }
        }
    }
    public static HashSet<BlockPos> getHeadPart(Level level, BlockPos headStartBp, Direction primerDir) {
        HashSet<BlockPos> headBps = new HashSet<>();

        BlockPos curBp = headStartBp;
        while (true) {
            BlockState state = level.getBlockState(curBp);
            if (state.isAir()) return headBps;

            headBps.add(curBp);
            curBp = curBp.relative(primerDir);
        }
    }
    public static BlockPos preCalculateHitPos() {
        return new BlockPos(0, 0, 0);
    }

    public static HashSet<BlockPos> calculateBarrelBps(Level level, BlockPos breechBp, Direction breechDir) {
        HashSet<BlockPos> poses = new HashSet<>();
        BlockPos curPos = breechBp;
        poses.add(curPos);

        //Direction breechDir = breechState.getValue(DirectionProvider.FACING);//((ShellThrowingBreechBlock)breechState.getBlock()).getDirection(breechState);
        while (true) {
            curPos = curPos.relative(breechDir);
            if (level.getBlockState(curPos).getBlock() instanceof IBarrel) {
                poses.add(curPos);
            } else {
                break;
            }
        }
        return poses;
    }

    public static Vector3d calculateHitPos(Level level, BlockPos primerBp, double totalEnergy, HashSet<BlockPos> barrelBps, HashSet<BlockPos> headBps) {
        Ship ship = ShipUtil.getShipAt(level, primerBp);
        Direction primerDir = level.getBlockState(primerBp).getValue(DirectionAdder.FACING);
        Vector3d worldPrimerDir;
        if (ship == null) {
            EzDebug.warn("ship is null, fail to calculate Hit Pos");
            return new Vector3d(0, 0, 0);
        } else {
            worldPrimerDir = ship.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(primerDir));
        }
        Vector3dc startPos = ship.getTransform().getPositionInWorld();

        double headWeight = headBps.size() * 1000;   //todo get real weight
        double sqBarrelLen = (barrelBps.size() + 1) * (barrelBps.size() + 1);
        double remainEnergy = totalEnergy;

        Vector3d sandboxPos = startPos.get(new Vector3d());
        Vector3d acceleration = new Vector3d();
        Vector3d velocity = new Vector3d();
        double timeInBarrel = PHYS_FRAME_TIME;
        //In barrel stage
        while (sandboxPos.distanceSquared(startPos) <= sqBarrelLen) {
            Vector3d currentForce = new Vector3d();
            remainEnergy = BallisticsMath.getForceInBarrel(headWeight, remainEnergy, timeInBarrel, worldPrimerDir, currentForce);

            acceleration.set(currentForce.div(headWeight, new Vector3d()));  //no gravity
            velocity.add(acceleration.mul(PHYS_FRAME_TIME, new Vector3d()));
            sandboxPos.add(velocity.mul(PHYS_FRAME_TIME, new Vector3d()));

            timeInBarrel += PHYS_FRAME_TIME;
        }

        while (sandboxPos.y > startPos.y() - 4) {
            acceleration.set(GRAVITY);
            velocity.add(acceleration.mul(PHYS_FRAME_TIME, new Vector3d()));
            sandboxPos.add(velocity.mul(PHYS_FRAME_TIME, new Vector3d()));
        }

        return sandboxPos;
    }
}
*/