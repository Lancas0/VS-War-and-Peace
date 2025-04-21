package com.lancas.vs_wap.ship.ballistics.helper;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.math.ForceOnPos;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.foundation.TriTuple;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBic;

import java.lang.Math;
import java.util.Random;

import static com.lancas.vs_wap.ship.ballistics.data.BallisticStateData.PROPELLANT_RELEASE_FACTOR;

public class BallisticsMath {
    public static final double RAD2DEG = 57.29577951308232;
    public static final double DEG2RAD = 0.017453292519943295;
    public static final double PASS_RAD = 80 * DEG2RAD;
    public static final double PI = 3.14159265358979323846;

    public static final Vector3dc ANTI_GRAVITY = new Vector3d(0, -10, 0);

    //todo add armour fatigue
    public static class TerminalContext {
        public Vector3d velocity;
        public final Vector3dc normal;
        public final double incidenceRad;
        public final double obliquedIncidenceRad;
        public final double criticalRad;

        public final Vector3dc velDir;
        public final Vector3dc negNormal;

        public final double projectileHardness;
        public final double armourHardness;
        public final double armourToughness;


        public final double projectileMass;
        public final double projectileAreaNoScale;
        public final double projectileEnergy;

        public final double projectileScale;
        public final double armourScale;
        public final double equivalentArmourDepth;

        public final double projectileAreaScaled;
        public final double armourArea;
        public final double contactArea;

        public static @Nullable TerminalContext safeContextOrNull(Vector3dc inVelocity, Vector3dc inNormal, double obliqueDegree, double inProjHardness, double inArmourHardness, double inArmourToughness, double inProjMass, double inProjAreaNoScale, double inProjScale, double inArmourScale) {
            if (inVelocity == null)
                return null;

            double velLenSq = inVelocity.lengthSquared();
            if (velLenSq < 1E-14 || !Double.isFinite(velLenSq))
                return null;

            if (inProjMass < 1E-10)
                return null;

            return new TerminalContext(inVelocity, inNormal, obliqueDegree, inProjHardness, inArmourHardness, inArmourToughness, inProjMass, inProjAreaNoScale, inProjScale, inArmourScale);
        }
        public TerminalContext(Vector3dc inVelocity, Vector3dc inNormal, double inObliqueDegree, double inProjHardness, double inArmourHardness, double inArmourToughness, double inProjMass, double inProjAreaNoScale, double inProjScale, double inArmourScale) {
            if (inVelocity == null || inVelocity.lengthSquared() < 1E-14) {
                throw new RuntimeException("Terminal Context: velocity can't be zero or null");
            }

            velocity = new Vector3d(inVelocity);  //vel may be changed by outside
            normal = inNormal.normalize(new Vector3d());

            //note: vel may be zero
            velDir = velocity.normalize(new Vector3d());
            negNormal = normal.negate(new Vector3d());

            incidenceRad = Math.acos(velDir.dot(negNormal));
            obliquedIncidenceRad = Math.min(Math.max(0, incidenceRad - inObliqueDegree * DEG2RAD), PASS_RAD);

            projectileHardness = inProjHardness;
            armourHardness = inArmourHardness;
            armourToughness = inArmourToughness;

            criticalRad = getCriticalRad(projectileHardness, armourHardness);

            //todo note: projectileMass maybe 0
            projectileMass = inProjMass;
            projectileAreaNoScale = inProjAreaNoScale;
            projectileEnergy = 0.5 * inProjMass * velocity.lengthSquared();

            projectileScale = inProjScale;
            armourScale = inArmourScale;
            equivalentArmourDepth = armourScale * armourScale / Math.cos(obliquedIncidenceRad);

            projectileAreaScaled = inProjAreaNoScale * projectileScale * projectileScale;
            armourArea = armourScale * armourScale;
            contactArea = Math.min(projectileAreaScaled, armourArea);
        }
        /*public static TerminalContext selfCollisionTrigger(Level level, TriggerInfo.CollisionTriggerInfo triggerInfo) {
            BallisticsHitInfo hitInfo = triggerInfo.hitInfo;

            Ship projectile = triggerInfo.projectileShip;
            Ship hittedShip = ShipUtil.getShipAt(level, hitInfo.hitBlockPos);
            double armourScale = hittedShip == null ? 1.0 : hittedShip.getTransform().getShipToWorldScaling().x();  //todo 3d scale?

            BlockState projHeadState = triggerInfo.triggerBlockState;
            BlockState armourState = hitInfo.getHitBlockState(level);
            return new TerminalContext(
                hitInfo.hitVelocity,
                hitInfo.worldNormal,
                EinherjarBlockInfos.HARDNESS.valueOrDefaultOf(projHeadState),
                EinherjarBlockInfos.HARDNESS.valueOrDefaultOf(armourState),
                EinherjarBlockInfos.ARMOUR_TOUGHNESS.valueOrDefaultOf(armourState),
                EinherjarBlockInfos.MASS.valueOrDefaultOf(projHeadState),
                EinherjarBlockInfos.AP_HEAD_AREA.valueOrDefaultOf(projHeadState),
                projectile.getTransform().getShipToWorldScaling().x(),
                armourScale
            );
        }*/

        //todo 转正角度
        public boolean isBounce(Random random) {
            double nextFloat = random.nextFloat();
            double probability = getBounceProbability();
            EzDebug.light("nextFloat:" + nextFloat + ", prob:" + probability);
            return nextFloat < probability;
        }
        public boolean isPass() {
            return incidenceRad > PASS_RAD;  //use incidence rad
        }
        public Vector3d getBouncedVelocity(boolean penetrated) {
            Vector3dc startBounceVel = penetrated ? getPenetratedVel() : velocity;
            //EzDebug.log("penetrated:" + penetrated + ", startBounce:" + StrUtil.F2(startBounceVel) + ", pen:" + StrUtil.F2(getPenetratedVel()) + ", vel:" + velocity);
            if (startBounceVel.lengthSquared() < 1E-14)
                return new Vector3d();

            double projection = startBounceVel.dot(negNormal);
            Vector3d velAddon = normal.mul(2 * projection, new Vector3d());
            Vector3d bouncedVelDir = startBounceVel.add(velAddon, new Vector3d()).normalize();

            //EzDebug.log("penetratedVel:" + startBounceVel + ", projection:" + projection + ", velAddon:" + StrUtil.F2(velAddon) + ", bouncedVelDir:" + bouncedVelDir + ", bouncedVelLen:" + startBounceVel.length() * Math.cos(incidenceRad));

            return bouncedVelDir.mul(startBounceVel.length() * Math.cos(incidenceRad), new Vector3d());
        }
        public Vector3d getPostVelocity(Random random) {
            boolean penetrate = canPenetrate();
            boolean bounce = isBounce(random);
            //无法过穿
            if (!penetrate) {
                if (bounce)
                    return getBouncedVelocity(false);
                return getReboundVel();
            }

            //过穿后速度太小，则直接弹回
            Vector3d penetratedVel = getPenetratedVel();
            if (penetratedVel.lengthSquared() <= 1)
                return getReboundVel();

            //过穿后仍有一定速度
            if (bounce)
                return getBouncedVelocity(true);
            return penetratedVel;
        }
        public Quaterniond getBouncedRotation(Quaterniondc prevRotation) {
            Vector3d rotateAxis = velDir.cross(normal, new Vector3d());
            double rotateRad = PI - 2 * incidenceRad;
            return prevRotation.rotateAxis(rotateRad, rotateAxis, new Quaterniond());
        }
        public double getAbsorbedEnergy() {
            return armourHardness * equivalentArmourDepth + armourToughness * equivalentArmourDepth * contactArea;
        }
        public double getResistEnergy() {
            return armourHardness * equivalentArmourDepth;
        }
        public boolean canPenetrate() {
            return getResistEnergy() <= projectileEnergy;
        }
        public double getPenetratedEnergy() {
            //EzDebug.light("energy:" + projectileEnergy + ", abEnergy:" + getAbsorbedEnergy() + ", vel:" + StrUtil.F2(velocity) + ", velSqLen:" + velocity.lengthSquared());
            return Math.max(0, projectileEnergy - getAbsorbedEnergy());
        }
        public Vector3d getReboundVel() {
            if (projectileMass < 1E-10)
                return new Vector3d();

            //todo set vel to zero for now
            if (true)
                return new Vector3d();
            //temp

            double postEnergy = projectileEnergy / 2.0;  //todo conifguarble or physical formula
            Vector3d postVel = velDir.mul(-Math.sqrt(2 * postEnergy / projectileMass), new Vector3d());

            return postVel;
        }
        public Vector3d getPenetratedVel() {
            if (projectileMass < 1E-10)
                return new Vector3d();

            double penetratedEnergy = getPenetratedEnergy();
            Vector3d penetratedVel = velDir.mul(Math.sqrt(2 * penetratedEnergy / projectileMass), new Vector3d());

            //EzDebug.light("getPenetratedEnergy:" + penetratedEnergy + ", penetratedVelLen:" + Math.sqrt(2 * penetratedEnergy / projectileMass) + ", postVel:" + StrUtil.F2(penetratedVel));
            return penetratedVel;
        }
        public Vector3d getImpactForce() {
            return velDir.mul(2 * getAbsorbedEnergy() * projectileMass, new Vector3d());
        }


        public static double getCriticalRad(double projHardness, double armourHardness) {
            double deg = 30 + 45 * (projHardness / (projHardness + armourHardness));
            EzDebug.warn("projHard:" + projHardness + ", armourHard:" + armourHardness + ", get critical deg add:" + (projHardness / (projHardness + armourHardness) + ", deg:" + deg));
            return deg * DEG2RAD;
        }
        public double getBounceProbability() {
            if (obliquedIncidenceRad < criticalRad) return 0;
            return Math.cos(obliquedIncidenceRad - criticalRad) * (armourHardness / (projectileHardness + armourHardness));
        }
        /*
        public static double getIncidenceDeg(Vector3dc velocity, Vector3dc normal) {
            return getIncidenceRad(velocity, normal) * RAD2DEG;
        }
        //跳弹速度视为与装甲垂直方向速度消失，速度大小变为水平方向速度v0*cosθ，方向为出射方向
        public static Vector3d getBouncedVelocity(Vector3dc velocity, Vector3dc normal) {
            Vector3d invNormal = normal.negate(new Vector3d());
            double projection = velocity.dot(invNormal);
            Vector3d velAddon = normal.mul(2 * projection, new Vector3d());

            EzDebug.log("invNormal:" + StrUtil.F2(invNormal) + ", proj:" + projection + ", velAdd:" + StrUtil.F2(velAddon) + ", bouncedVel:"+ StrUtil.F2(velocity.add(velAddon, new Vector3d())));

            return velocity.add(velAddon, new Vector3d());
        }*/
    }





    public static double getForceInBarrel(double mass, double remainEnergy, double timeInBarrel, Vector3dc launchDir, Vector3d forceDest) {
        timeInBarrel = Math.max(0.016, timeInBarrel);  //todo use cosntant

        //F*x=E -> F = sqrt(2E/m)/t
        double applyEnergy = remainEnergy * PROPELLANT_RELEASE_FACTOR;
        remainEnergy -= applyEnergy;
        double fScale = Math.sqrt(2 * applyEnergy * mass) / timeInBarrel;  //todo should use timer in barrel or just use tick time(1/60)?

        forceDest.set(launchDir.normalize(fScale, new Vector3d()));
        forceDest.add(ANTI_GRAVITY.mul(mass, new Vector3d()));
        //physShip.applyInvariantForce(force);
        //physShip.applyInvariantForce(ANTI_GRAVITY.mul(mass, new Vector3d()));

        return remainEnergy;
    }
    public static double getBlockProjectArea(Matrix4dc worldToShip, Vector3dc worldVel) {
        Vector3d velDirInShip = worldToShip.transformDirection(worldVel, new Vector3d()).normalize();
        return Math.abs(velDirInShip.x) + Math.abs(velDirInShip.y) + Math.abs(velDirInShip.z);
    }

    //获取三个受力的面
    //受力的面的法向量与对应轴上的速度投影，夹角<=90
    //或者对应轴上速度投影方向即为面的法向量方向
    //first : airDrag on x face
    //second : airDrag on y face
    //thrid : airDeag on z face
    public static TriTuple.TriCollection<ForceOnPos> calculateAirDrag(Matrix4dc shipToWorld, Matrix4dc worldToShip, double shipScale, AABBic shipAABB, Vector3dc worldVel) {
        TriTuple.TriCollection<BiTuple<Double, Vector3d>> airDragsInShip = new TriTuple.TriCollection<>();

        Vector3d velInShip = worldToShip.transformDirection(new Vector3d(worldVel));

        airDragsInShip.setFirst(new BiTuple<>(
            getAirDragLength(velInShip, shipAABB, shipScale, 0),
            velInShip.x >= 0 ?
                JomlUtil.dFaceCenter(shipAABB, Direction.EAST) :
                JomlUtil.dFaceCenter(shipAABB, Direction.WEST)
        ));
        airDragsInShip.setSecond(new BiTuple<>(
            getAirDragLength(velInShip, shipAABB, shipScale, 1),
            velInShip.y >= 0 ?
                JomlUtil.dFaceCenter(shipAABB, Direction.UP) :
                JomlUtil.dFaceCenter(shipAABB, Direction.DOWN)
        ));
        airDragsInShip.setThird(new BiTuple<>(
            getAirDragLength(velInShip, shipAABB, shipScale, 2),
            velInShip.z >= 0 ?
                JomlUtil.dFaceCenter(shipAABB, Direction.SOUTH) :
                JomlUtil.dFaceCenter(shipAABB, Direction.NORTH)
        ));

        return airDragsInShip.map(
            lenAndShipPos -> {
                double length = lenAndShipPos.getFirst();
                Vector3d forceInWorld = worldVel.mul(-length, new Vector3d());
                Vector3d worldPos = shipToWorld.transformPosition(lenAndShipPos.getSecond(), new Vector3d());
                return new ForceOnPos(forceInWorld, worldPos);
            }
        );
    }
    private static double getAirDragLength(Vector3dc velInShip, AABBic shipAABB, double shipScale, int component) {
        double airDragConst = 0.00025;

        Vector3d velDirInShip = velInShip.normalize(new Vector3d());
        double areaScale = shipScale * shipScale;

        double velInShipComp = velInShip.get(component);
        Direction dir = switch (component) {
            case 0 -> Direction.EAST;
            case 1 -> Direction.UP;
            case 2 -> Direction.SOUTH;
            default -> throw new RuntimeException("invalid component:" + component);
        };
        double projectileArea = JomlUtil.sideArea(shipAABB, dir) * Math.abs(velDirInShip.get(component));

        return airDragConst * velInShipComp * velInShipComp * projectileArea * areaScale;
    }
}
