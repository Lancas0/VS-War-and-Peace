package com.lancas.vs_wap.handler;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.*;

import java.lang.Math;


@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScopeClientManager {
    private static boolean isScoping = false;
    private static float scopeFovMultiplier = 1;
    private static Vector3f scopePos = new Vector3f();
    private static float scopeXRot = 0;
    private static float scopeYRot = 0;
    //private static Quaternionfc scopeRotation;

    public static boolean getIsScoping() { return isScoping; }
    public static Vec3 getScopePosV3() { return JomlUtil.v3(scopePos); }
    public static Vector3d getScopePosD() { return scopePos.get(new Vector3d()); }
    public static float getScopeXRot() { return scopeXRot; }
    public static float getScopeYRot() { return scopeYRot; }
    //public static Quaternionfc getScopeRotationf() { return scopeRotation; }


    public static void setScopeData(boolean inIsScoping, float inScopeFovMultiplier, Vector3dc inScopePos, Vector3dc inForward) {
        isScoping = inIsScoping;
        scopePos = inScopePos.get(new Vector3f());
        scopeFovMultiplier = inScopeFovMultiplier;
        //scopeRotation = shipRot.get(new Quaternionf());

        //Vector3d forwardOnXZ = new Vector3d(inForward.x(), 0, inForward.z()).normalize();

        //Vector3d viewEuler = shipRot.getEulerAnglesYXZ(new Vector3d());
        Vector3d viewEuler = new Quaterniond().lookAlong(inForward, new Vector3d(0, 1, 0)).getEulerAnglesYXZ(new Vector3d());

        // 转换为角度
        float yawDeg = (float) Math.toDegrees(viewEuler.y) + 180;
        float pitchDeg = -(float)Math.toDegrees(Math.asin(inForward.y()));//(float)Math.toDegrees(Math.acos(inForward.dot(forwardOnXZ)));

        //EzDebug.Log("forward:" + inForward + ", yawDeg:" + yawDeg + ", pitchDeg:" + pitchDeg);

        // 调整方向（根据方块朝向）
        //yawDeg += scopeDir.toYRot();  //todo scopeDir

        // 规范化角度范围
        //yawDeg = (yawDeg % 360 + 360) % 360; // 保持0~360
        //pitchDeg = (float) Math.max(-90.0, Math.min(90.0, pitchDeg));

        scopeXRot = pitchDeg; //the x rot should be negated
        scopeYRot = yawDeg;

        //TestRenderer2.vecs.put("ScopeClientMgrForwardDebug", new Pair<>(JomlUtil.d(Minecraft.getInstance().player.position()), inForward.get(new Vector3d())));
    }

    @SubscribeEvent
    public static void onFOVUpdate(ViewportEvent.ComputeFov event) {
        if (isScoping) {
            double originFOV = event.getFOV();
            //原版望远镜为0.1
            event.setFOV(originFOV * scopeFovMultiplier);
        }
    }
}
