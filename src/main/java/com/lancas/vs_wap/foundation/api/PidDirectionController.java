package com.lancas.vs_wap.foundation.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.foundation.api.math.Pid;
import com.lancas.vs_wap.util.MathUtil;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PidDirectionController {
    public static final double RAD2OMEGA_MAG = 0.5;

    private Pid pid;
    private Vector3d lastErr = new Vector3d();
    private Vector3d integral = new Vector3d();
    private double maxOmega;

    private PidDirectionController() {
        pid = new Pid(0, 0, 0);
        maxOmega = 100;
    }
    public PidDirectionController(double p, double i, double d, double inMaxOmega) {
        pid = new Pid(p, i, d);
        maxOmega = Math.abs(inMaxOmega);
    }

    private Vector3d getTargetOmega(Vector3dc worldTowards, Vector3dc expectTowards) {
        Vector3d rotationAxis = worldTowards.cross(expectTowards, new Vector3d());

        double rad = worldTowards.angle(expectTowards);
        return rotationAxis.normalize(rad * RAD2OMEGA_MAG);
    }

    public Vector3d getTorque(Vector3dc worldTowards, Vector3dc expectTowards, Vector3dc curOmega, Matrix3dc inertia, double dt) {
        Vector3d targetOmega = getTargetOmega(worldTowards, expectTowards);

        Vector3d error = targetOmega.sub(curOmega, new Vector3d());

        integral.add(new Vector3d(error).mul(dt));
        MathUtil.clamp(integral, maxOmega / Math.max(pid.i(), 1E-6));

        Vector3d derivative = new Vector3d(error).sub(lastErr).div(dt);
        lastErr.set(error);

        Vector3d omegaAdd = new Vector3d()
            .add(new Vector3d(error).mul(pid.p()))
            .add(new Vector3d(integral).mul(pid.i()))
            .add(new Vector3d(derivative).mul(pid.d()));
        MathUtil.clamp(omegaAdd, maxOmega);
        return omegaAdd.mul(inertia);
    }
    public Vector3d getNewOmega(Vector3dc worldTowards, Vector3dc expectTowards, Vector3dc curOmega, double dt) {
        Vector3d targetOmega = getTargetOmega(worldTowards, expectTowards);

        Vector3d error = targetOmega.sub(curOmega, new Vector3d());

        integral.add(new Vector3d(error).mul(dt));
        MathUtil.clamp(integral, maxOmega / Math.max(pid.i(), 1E-6));

        Vector3d derivative = new Vector3d(error).sub(lastErr).div(dt);
        lastErr.set(error);

        Vector3d omegaAdd = new Vector3d()
            .add(new Vector3d(error).mul(pid.p()))
            .add(new Vector3d(integral).mul(pid.i()))
            .add(new Vector3d(derivative).mul(pid.d()));
        return curOmega.add(MathUtil.clamp(omegaAdd, maxOmega), new Vector3d());
    }
}


//some formual
/*
double scale_i = compShip.getImpl().getTransform().getShipToWorldScaling().get(id);
double inertia_scale_ratio = Math.pow(scale_i, 5);

double accel_scale = servo.getControllerInfoHolder().calculateControlValueScale(property.angleOrSpeed());
accel_scale = VSMathUtils.clamp(accel_scale, 1000);
double control_torque = property.torque();
double internal_torque = compShip.getImpl().getInertia().getMomentOfInertiaTensor().m00() * inertia_scale_ratio * accel_scale;
*/
