package com.lancas.vswap.sandbox.ballistics.data;

import com.lancas.vswap.WapCommonConfig;
import com.lancas.vswap.util.JomlUtil;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class BallisticFlyingContext {
    public final Vector3d gravity = new Vector3d();
    public double displacementIntensity = 0;

    public BallisticFlyingContext(Vector3dc inGravity, double inDisplacement) {
        gravity.set(inGravity);
        displacementIntensity = inDisplacement;
    }

    public static BallisticFlyingContext getDefault() {
        return new BallisticFlyingContext(new Vector3d(0, -9.8, 0), WapCommonConfig.projectileRandomDisplacement);
    }
}
