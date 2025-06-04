package com.lancas.vswap.foundation.math;

import com.lancas.vswap.WapCommonConfig;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.io.DataInput;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class WapBallisticMath {
    public static final RadMath RAD = new RadMath();
    public static final DegMath DEG = new DegMath();

    public static final double PASS_DEG = 81;
    public static final double PASS_RAD = Math.toRadians(PASS_DEG);

    public static class RadMath {
        public double calIncidenceRad(Vector3dc velDir, Vector3dc normal) {
            Vector3d negNormal = normal.normalize(new Vector3d()).negate();
            return Math.acos(velDir.normalize(new Vector3d()).dot(negNormal));
        }
        public double calRhae(BlockState armour, double incidenceRad) {
            double equivalentArmourDepth = 1.0 / Math.cos(incidenceRad);
            double rhae = WapBlockInfos.ArmourRhae.valueOrDefaultOf(armour);

            return rhae * equivalentArmourDepth;
        }
        public double calAfterNormalizationRhae(BlockState armour, BlockState warhead, double incidenceRad) {
            double equivalentArmourDepth = 1.0 / Math.cos(incidenceRad);
            double normalization = WapBlockInfos.Normalization.valueOrDefaultOf(warhead);
            double afterNorEquivalentDepth = Math.max(1 * normalization + (1 - normalization) * equivalentArmourDepth, 0);  //todo min rhea

            double rhae = WapBlockInfos.ArmourRhae.valueOrDefaultOf(armour);

            return rhae * afterNorEquivalentDepth;
        }
        public double calFatalPP(BlockState armour, double incidenceRad) {
            double rhae = calRhae(armour, incidenceRad);
            if (!WapCommonConfig.isFatalKEOn())
                return Double.POSITIVE_INFINITY;

            return WapCommonConfig.rawFatalPPRatio * rhae;
        }
        public double calRicochetPob(BlockState warhead, double incidenceRad) {
            double criticalRad = Math.toRadians(WapBlockInfos.CriticalDegree.valueOrDefaultOf(warhead));
            if (incidenceRad < criticalRad)
                return 0;
            if (incidenceRad > PASS_RAD)
                return 1;
            return (incidenceRad - criticalRad) / (PASS_RAD - criticalRad);
        }

        public double getAbsorbStdPP(BlockState armour, BlockState warhead, double incidenceRad) {
            double afterNorRhae = calAfterNormalizationRhae(armour, warhead, incidenceRad);
            double absorbRatio = WapBlockInfos.ArmourAbsorbRatio.valueOrDefaultOf(armour);
            return afterNorRhae * absorbRatio;   //todo relate to velocity
        }

        public Vector3d calBouncedVelDir(Vector3dc incidenceVel, Vector3dc normal) {
            Vector3dc norVel = incidenceVel.normalize(new Vector3d());

            double projection = norVel.dot(normal.normalize(new Vector3d()).negate());
            Vector3d velAddon = normal.mul(2 * projection, new Vector3d());
            Vector3d bouncedVelDir = norVel.add(velAddon, new Vector3d()).normalize();

            return bouncedVelDir;
        }
        public Quaterniond calBouncedRotation(Vector3dc incidenceVel, Vector3dc normal, Quaterniondc prevRotation) {
            Vector3d velDir = incidenceVel.normalize(new Vector3d());
            Vector3d norNormal = normal.normalize(new Vector3d());

            double incidenceRad = calIncidenceRad(velDir, norNormal);

            Vector3d rotateAxis = velDir.cross(norNormal, new Vector3d());
            double rotateRad = Math.PI - 2 * incidenceRad;
            return prevRotation.rotateAxis(rotateRad, rotateAxis, new Quaterniond());
        }
    }
    public static class DegMath {
        public double calIncidenceDeg(Vector3dc velDir, Vector3dc normal) { return Math.toDegrees(RAD.calIncidenceRad(velDir, normal)); }
        public double calRhae(BlockState armour, double incidenceDeg) { return RAD.calRhae(armour, Math.toRadians(incidenceDeg)); }
        public double calAfterNormalizationRhae(BlockState armour, BlockState warhead, double incidenceDeg) {
            return RAD.calAfterNormalizationRhae(armour, warhead, Math.toRadians(incidenceDeg));
        }
        public double calFatalPP(BlockState armour, double incidenceDeg) { return RAD.calFatalPP(armour, Math.toRadians(incidenceDeg)); }
        public double calRicochetPob(BlockState warhead, double incidenceDeg) {
            return RAD.calRicochetPob(warhead, Math.toRadians(incidenceDeg));
        }
    }

    public static Stream<BlockPos> calDestructionBlocks(BlockPos centerBp, double stdPE, BlockState warhead) {
        double halfRadius = WapBlockInfos.Spe_DestructionScalar.valueOrDefaultOf(warhead) * stdPE / 2.0;
        double sqRadius = (halfRadius * 2) * (halfRadius * 2);
        Vec3 center = centerBp.getCenter();

        /*return BlockPos.betweenClosedStream(
            (int)Math.floor(centerBp.getX() - halfRadius),
            (int)Math.floor(centerBp.getY() - halfRadius),
            (int)Math.floor(centerBp.getZ() - halfRadius),
            (int)Math.floor(centerBp.getX() + halfRadius),
            (int)Math.floor(centerBp.getY() + halfRadius),
            (int)Math.floor(centerBp.getZ() + halfRadius)
        )
            .filter(bp -> bp.distToCenterSqr(center) <= sqRadius);*/
        //todo can also break in vsShip blocks
        Queue<BlockPos> open = new LinkedList<>();
        LinkedHashSet<BlockPos> visited = new LinkedHashSet<>();
        open.add(centerBp);
        visited.add(centerBp);

        while (!open.isEmpty()) {
            BlockPos cur = open.poll();
            for (Direction d : Direction.values()) {
                BlockPos next = cur.relative(d);
                if (next.getCenter().distanceToSqr(center) <= sqRadius && !visited.contains(next)) {
                    open.add(next);
                    visited.add(next);
                }
            }
        }
        return visited.stream();
    }
}
