package com.lancas.vs_wap.ship.phys;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionPID {
    // assuming task run at physics thread
    private static final Vector3d ANTI_GRAVITY = new Vector3d(0, 10, 0);
    private static final double dt = 0.01667;

    private final double p, i, d;
    private final Vector3d integral;
    private final Vector3d lastError;

    public PositionPID() {
        p = i = d = 0;
        integral = new Vector3d();
        lastError = new Vector3d();
    }
    public PositionPID(double inP, double inI, double inD) {
        p = inP;
        i = inI;
        d = inD;
        integral = new Vector3d();
        lastError = new Vector3d();
    }

    public Vector3d calculateForce(double mass, Vector3dc currentPos, Vector3dc targetPos) {
        Vector3d error = targetPos.sub(currentPos, new Vector3d());

        integral.add(error.mul(dt, new Vector3d())); // 积分项

        Vector3dc accel_p = new Vector3d(error).mul(p);
        Vector3dc accel_d = new Vector3d(error).sub(lastError, new Vector3d()).mul(d / dt);
        Vector3dc accel_i = new Vector3d(0, integral.y(), 0).mul(i);


        lastError.set(error);

        Vector3d force =
            new Vector3d(accel_p).add(accel_d).add(accel_i).add(ANTI_GRAVITY).mul(mass);

        return force;
    }
}