package com.lancas.vswap.content.block.blocks.industry.robotarm;

import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmAngleTarget;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ArmAngleHelper {
    public static record TargetAngles(
        float baseAngle,
        float lowerArmAngle,
        float upperArmAngle,
        float headAngle) {
    }

    public static TargetAngles getDefault() {
        return new TargetAngles(0, 135, 45, 0);
    }
    public static TargetAngles getTargetAngles(Level level, BlockPos armPos, Vector3dc targetPos, Direction clawFacing, boolean ceiling) {
        Vector3d origin = WorldUtil.getWorldCenter(level, armPos).add(0, ceiling ? -0.375 : 0.375, 0);
        Vector3d target = new Vector3d(targetPos).add(JomlUtil.dNormal(clawFacing, 0.5));
        Vector3d diff = target.sub(origin, new Vector3d());

        double horizontalDist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float baseAngle = AngleHelper.deg(Mth.atan2(diff.x, diff.z)) + 180.0F;
        if (ceiling) {
            diff = diff.mul(1.0, -1.0, 1.0);
            baseAngle = 180.0F - baseAngle;
        }

        float alphaOffset = AngleHelper.deg(Mth.atan2(diff.y, horizontalDist));
        float a = 0.875F;
        float a2 = a * a;
        float b = 0.9375F;
        float b2 = b * b;
        float diffLength = Mth.clamp(Mth.sqrt((float)(diff.y * diff.y + (horizontalDist * horizontalDist))), 0.125F, a + b);
        float diffLength2 = diffLength * diffLength;
        float alphaRatio = (-b2 + a2 + diffLength2) / (2.0F * a * diffLength);
        float alpha = AngleHelper.deg(Math.acos((double)alphaRatio)) + alphaOffset;
        float betaRatio = (-diffLength2 + a2 + b2) / (2.0F * b * a);
        float beta = AngleHelper.deg(Math.acos((double)betaRatio));
        if (Float.isNaN(alpha)) {
            alpha = 0.0F;
        }

        if (Float.isNaN(beta)) {
            beta = 0.0F;
        }

        Vec3 headPos = new Vec3(0.0, 0.0, 0.0);
        headPos = VecHelper.rotate(headPos.add(0.0, b, 0.0), (beta + 180.0F), Direction.Axis.X);
        headPos = VecHelper.rotate(headPos.add(0.0, a, 0.0), (alpha - 90.0F), Direction.Axis.X);
        headPos = VecHelper.rotate(headPos, baseAngle, Direction.Axis.Y);
        headPos = VecHelper.rotate(headPos, ceiling ? 180.0 : 0.0, Direction.Axis.X);
        headPos = headPos.add(JomlUtil.v3(origin));
        Vector3d headDiff = targetPos.sub(JomlUtil.d(headPos), new Vector3d());
        if (ceiling) {
            headDiff.mul(1, -1, 1);
        }

        float horizontalHeadDist = (float)Math.sqrt(headDiff.x * headDiff.x + headDiff.z * headDiff.z);//(float)headDiff.multiply((double)1.0F, (double)0.0F, (double)1.0F).length();
        float headAngle = alpha + beta + 135 - AngleHelper.deg(Mth.atan2(headDiff.y, horizontalHeadDist));
        /*this.lowerArmAngle = alpha;
        this.upperArmAngle = beta;
        this.headAngle = -headAngle;
        this.baseAngle = baseAngle;*/
        return new TargetAngles(
            baseAngle,
            alpha,      //lowerArmAngle
            beta,       //upperArmAngle
            -headAngle  //headAngle
        );
    }
    /*public static TargetAngles getByArmPoint(ArmInteractionPoint armPoint, Level level, BlockPos armPos) {

        return new TargetAngles(
            level, armPos, armPoint.get
        );
        if (this.cachedAngles == null) {
            this.cachedAngles = new ArmAngleTarget(armPos, this.getInteractionPositionVector(), this.getInteractionDirection(), ceiling);
        }

        return this.cachedAngles;
    }*/
}


/*
public ArmAngleTarget(BlockPos armPos, Vec3 pointTarget, Direction clawFacing, boolean ceiling) {
    Vec3 origin = VecHelper.getCenterOf(armPos).add((double)0.0F, ceiling ? (double)-0.375F : (double)0.375F, (double)0.0F);
    Vec3 target = pointTarget.add(Vec3.atLowerCornerOf(clawFacing.getOpposite().getNormal()).scale((double)0.5F));
    Vec3 diff = target.subtract(origin);
    float horizontalDistance = (float)diff.multiply((double)1.0F, (double)0.0F, (double)1.0F).length();
    float baseAngle = AngleHelper.deg(Mth.atan2(diff.x, diff.z)) + 180.0F;
    if (ceiling) {
        diff = diff.multiply((double)1.0F, (double)-1.0F, (double)1.0F);
        baseAngle = 180.0F - baseAngle;
    }

    float alphaOffset = AngleHelper.deg(Mth.atan2(diff.y, (double)horizontalDistance));
    float a = 0.875F;
    float a2 = a * a;
    float b = 0.9375F;
    float b2 = b * b;
    float diffLength = Mth.clamp(Mth.sqrt((float)(diff.y * diff.y + (double)(horizontalDistance * horizontalDistance))), 0.125F, a + b);
    float diffLength2 = diffLength * diffLength;
    float alphaRatio = (-b2 + a2 + diffLength2) / (2.0F * a * diffLength);
    float alpha = AngleHelper.deg(Math.acos((double)alphaRatio)) + alphaOffset;
    float betaRatio = (-diffLength2 + a2 + b2) / (2.0F * b * a);
    float beta = AngleHelper.deg(Math.acos((double)betaRatio));
    if (Float.isNaN(alpha)) {
        alpha = 0.0F;
    }

    if (Float.isNaN(beta)) {
        beta = 0.0F;
    }

    Vec3 headPos = new Vec3((double)0.0F, (double)0.0F, (double)0.0F);
    headPos = VecHelper.rotate(headPos.add((double)0.0F, (double)b, (double)0.0F), (double)(beta + 180.0F), Direction.Axis.X);
    headPos = VecHelper.rotate(headPos.add((double)0.0F, (double)a, (double)0.0F), (double)(alpha - 90.0F), Direction.Axis.X);
    headPos = VecHelper.rotate(headPos, (double)baseAngle, Direction.Axis.Y);
    headPos = VecHelper.rotate(headPos, ceiling ? (double)180.0F : (double)0.0F, Direction.Axis.X);
    headPos = headPos.add(origin);
    Vec3 headDiff = pointTarget.subtract(headPos);
    if (ceiling) {
        headDiff = headDiff.multiply((double)1.0F, (double)-1.0F, (double)1.0F);
    }

    float horizontalHeadDistance = (float)headDiff.multiply((double)1.0F, (double)0.0F, (double)1.0F).length();
    float headAngle = alpha + beta + 135.0F - AngleHelper.deg(Mth.atan2(headDiff.y, (double)horizontalHeadDistance));
    this.lowerArmAngle = alpha;
    this.upperArmAngle = beta;
    this.headAngle = -headAngle;
    this.baseAngle = baseAngle;
}*/