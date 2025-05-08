package com.lancas.vs_wap.util;

import org.joml.Vector3d;

import java.util.Random;

public class RandUtil {
    public static final Random RANDOM = new Random();
    public static final double _2PI = 6.283185307179586476925286766559;

    public static Vector3d onUnitSphere() {
        double phi = RANDOM.nextDouble(_2PI);
        double cosTheta = 1 - 2 * RANDOM.nextDouble();
        double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);

        return new Vector3d(
            sinTheta * Math.cos(phi),
            cosTheta,
            sinTheta * Math.sin(phi)
        );
    }
    public static Vector3d onSphere(double radius) { return onUnitSphere().mul(radius); }
    public static Vector3d onRandSphere(double lower, double upper) { return onSphere(RANDOM.nextDouble(lower, upper)); }

    public static double nextD() { return RANDOM.nextDouble(); }
    public static float nextF() { return RANDOM.nextFloat(); }
    public static double nextG() { return RANDOM.nextGaussian(); }
    public static double nextG(double mean, double std) { return RANDOM.nextGaussian(mean, std); }

    public static double nextRadD() { return RANDOM.nextDouble(_2PI); }
    public static float nextRadF() { return RANDOM.nextFloat((float)_2PI); }
}
