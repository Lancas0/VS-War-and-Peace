package com.lancas.vs_wap.mixins;

import com.lancas.vs_wap.handler.ScopeClientManager;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    //todo: it shakes, to make it smoothly
    @Unique private static final float einherjar$lerpSmooth = 0.4f;

    @Shadow private Vec3 position;
    @Final @Shadow private BlockPos.MutableBlockPos blockPosition;

    @Final @Shadow private Vector3f forwards;
    @Final @Shadow private Vector3f up;
    @Final @Shadow private Vector3f left;
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Final @Shadow private Quaternionf rotation;

    @Unique private Vec3 einherjar$lastScopingPos = null;
    @Unique private Float einherjar$lastXRot = null;
    @Unique private Float einherjar$lastYRot = null;

    @Inject(
        method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void setScopingPos(Vec3 pos, CallbackInfo ci) {
        Vec3 scopePos = ScopeClientManager.getScopePosV3();

        if (ScopeClientManager.getIsScoping()) {
            Vec3 curPos;
            if (einherjar$lastScopingPos == null) {
                curPos = lerp(pos, scopePos, einherjar$lerpSmooth);
            } else {
                curPos = lerp(einherjar$lastScopingPos, scopePos, einherjar$lerpSmooth);
            }

            this.position = curPos;
            this.blockPosition.set(curPos.x, curPos.y, curPos.z);
            einherjar$lastScopingPos = curPos;

            ci.cancel();
        } else {
            einherjar$lastScopingPos = null;
        }

        //TestRenderer2.poses.put("SetScopingPosDebug", ScopeClientManager.getScopePosD());
    }

    //todo set rotation
    @Inject(method = "setRotation", at = @At("HEAD"), cancellable = true)
    private void setScopeRotation(float _yRot, float _xRot, CallbackInfo ci) {
        /*if (true) {
            this.xRot = 0;
            this.yRot = 45;

            this.rotation.rotationYXZ(-this.yRot * ((float)Math.PI / 180F), this.xRot * ((float)Math.PI / 180F), 0.0F);
            //this.rotation.slerp(targetRot, lerpSmooth);
            //this.rotation.set(ScopeClientManager.getScopeRotationf());

            this.forwards.set(0.0F, 0.0F, 1.0F).rotate(this.rotation);
            this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
            this.left.set(1.0F, 0.0F, 0.0F).rotate(this.rotation);
            ci.cancel();
            return;
        }*/

        if (ScopeClientManager.getIsScoping()) {
            float scopeXRot = ScopeClientManager.getScopeXRot();
            float scopeYRot = ScopeClientManager.getScopeYRot();

            float curXRot, curYRot;
            if (einherjar$lastXRot == null) {
                curXRot = lerp(_xRot, scopeXRot, einherjar$lerpSmooth);
                curYRot = slerp(_yRot, scopeYRot, einherjar$lerpSmooth);
            } else {
                curXRot = lerp(einherjar$lastXRot, scopeXRot, einherjar$lerpSmooth);
                curYRot = slerp(einherjar$lastYRot, scopeYRot, einherjar$lerpSmooth);
            }

            //EzDebug.Log("_yRot:" + _yRot + ", scopeYRot" + scopeYRot + ", curYRot" + curYRot + ", lastYRot" + einheriar$lastYRot);
            //EzDebug.Log("_xRot:" + _xRot + ", scopeXRot" + scopeXRot + ", curXRot" + curXRot + ", lastXRot" + einheriar$lastXRot);

            this.xRot = curXRot;
            this.yRot = curYRot;

            this.rotation.rotationYXZ(-curYRot * ((float)Math.PI / 180F), curXRot * ((float)Math.PI / 180F), 0.0F);
            this.forwards.set(0.0F, 0.0F, 1.0F).rotate(this.rotation);
            this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
            this.left.set(1.0F, 0.0F, 0.0F).rotate(this.rotation);

            einherjar$lastXRot = curXRot;
            einherjar$lastYRot = curYRot;

            ci.cancel();
            return;
        } else {
            einherjar$lastXRot = null;
            einherjar$lastYRot = null;
        }
    }

    private float lerp(float from, float to, float t) { return from + (to - from) * t; }
    private Vec3 lerp(Vec3 from, Vec3 to, float t) {
        return from.scale(1 - t).add(to.scale(t));
    }
    private float slerp(float from, float to, float alpha) {
        // 1. 计算角度差并修正到[-180°, 180°]范围
        float diff = ((to - from + 360 + 180) % 360) - 180;
        // 2. 计算插值后的角度差
        float interpolatedDiff = diff * alpha;
        // 3. 应用差值并规范到0-360范围
        float result = (from + interpolatedDiff + 360) % 360;
        return result;
    }

}
