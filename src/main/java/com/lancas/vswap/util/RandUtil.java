package com.lancas.vswap.util;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.core.BlockPos;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Random;
import java.util.Stack;
import java.util.UUID;

public class RandUtil {
    private static final Stack<Random> RandomStack = new Stack<>();
    static {
        RandomStack.add(new Random());
    }
    //public static final Random RANDOM = new Random();
    public static final double _2PI = 6.283185307179586476925286766559;

    public static Vector3d onUnitSphere() {
        double phi = RandomStack.peek().nextDouble(_2PI);
        double cosTheta = 1 - 2 * RandomStack.peek().nextDouble();
        double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);

        return new Vector3d(
            sinTheta * Math.cos(phi),
            cosTheta,
            sinTheta * Math.sin(phi)
        );
    }
    public static Vector3d onSphere(double radius) { return onUnitSphere().mul(radius); }
    public static Vector3d onRandSphere(double lower, double upper) { return onSphere(RandomStack.peek().nextDouble(lower, upper)); }

    public static double nextD(double min, double max) { return RandomStack.peek().nextDouble(min, max); }
    public static double nextD() { return RandomStack.peek().nextDouble(); }
    public static float nextF() { return RandomStack.peek().nextFloat(); }
    public static double nextG() { return RandomStack.peek().nextGaussian(); }
    public static double nextG(double mean, double std) { return RandomStack.peek().nextGaussian(mean, std); }

    public static double nextRadD() { return RandomStack.peek().nextDouble(_2PI); }
    public static float nextRadF() { return RandomStack.peek().nextFloat((float)_2PI); }

    public static int nextColor() { return RandomStack.peek().nextInt(0, 16777215 + 1); }

    public static boolean nextBool(double probability01) { return nextD() <= probability01; }

    public static Quaterniond nextQuaterniond(double minRad, double maxRad) {
        Vector3d axis = new Vector3d(nextG(), nextG(), nextG()).normalize();
        double rad = nextD(minRad, maxRad);
        return new Quaterniond().rotateAxis(rad, axis);
    }

    public static void pushSeed()            { RandomStack.push(new Random()); }
    //public static void pushSeed(long seed)   { RandomStack.push(new Random(seed)); }
    //public static void pushSeed(String seed) { RandomStack.push(new Random(seed.hashCode())); }
    public static void pushSeed(Object seed) { RandomStack.push(new Random(seed.hashCode())); }
    public static void popSeed() {
        if (RandomStack.size() <= 1) {
            EzDebug.warn("can't pop last random");
            return;
        }
        RandomStack.pop();
    }
}
