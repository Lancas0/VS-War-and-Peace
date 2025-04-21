package com.lancas.vs_wap.obsolete.ship.subcontrol;

/*
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class TailFinControl implements ISubForceInducer {
    @JsonIgnore  //just in case. All @JsonIgnore above static variable is for just in case.
    public static final double AIR_DENSITY = 1.29;

    private Vector3d finShipCenter;
    private Vector3d shipNormal;
    //private float attackAngleInDeg;  //attack angle is 0 for tail fin
    private double liftFactor;  //area effect is included in factor
    private double dragFactor;

    private TailFinControl() {}
    public TailFinControl(Vector3dc inFinShipCenter, Vector3dc inShipNormal, double inLiftFactor, double inDragFactor) {
        finShipCenter = inFinShipCenter.get(new Vector3d());
        shipNormal = inShipNormal.get(new Vector3d());
        liftFactor = inLiftFactor;
        dragFactor = inDragFactor;
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        Vector3d worldFinCenter = physShip.getTransform().getShipToWorld().transformPosition(finShipCenter, new Vector3d());

        Vector3dc velocity = physShip.getVelocity();
        Vector3d worldNormal = physShip.getTransform().getShipToWorldRotation().transform(shipNormal, new Vector3d());

        double dot = velocity.dot(worldNormal);
        double q = 0.5 * AIR_DENSITY * velocity.lengthSquared();

        Vector3d lift = worldNormal.mul(dot > 0 ? -1 : 1, new Vector3d()).mul(q * liftFactor);  //lift should along normal and resist velocity
        Vector3d drag = velocity.normalize(new Vector3d()).negate().mul(q * dragFactor);

        //physShip.applyInvariantForce(lift.add(drag)); //notice lift has changed since here
        Vector3d force = lift.add(drag, new Vector3d());
        if (force.isFinite())
            physShip.applyInvariantForceToPos(lift.add(drag), worldFinCenter);
        EzDebug.Log("try apply lift:" + lift + ", drag:" + drag + ", at " + worldFinCenter + ", q = " + q + ", vel * vel = " + velocity.lengthSquared());
    }
}*/
/*
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class TailFinControl implements ISubForceInducer<BallisticsController> {
    //@JsonIgnore  //just in case. All @JsonIgnore above static variable is for just in case.
    //public static final double AIR_DENSITY = 1.29;

    private Vector3d shipPos;      //the shipyard position of fin
    private Vector3d shipForward;  //the forward direction of fins, also the expected velocity direction
    private double stableFactor;  //area effect is included in factor
    private double antiGravityFactor;
    private double dragFactor;

    private TailFinControl() {}
    public TailFinControl(Vector3dc inShipPos, Vector3dc inShipForward, double inStableFactor, double inDragFactor) {
        shipPos = inShipPos.get(new Vector3d());
        shipForward = inShipForward.normalize(new Vector3d());
        stableFactor = inStableFactor;
        dragFactor = inDragFactor;



    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {

    }

    /*@Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (true) return;

        Vector3dc massCenter = physShip.getCenterOfMass();
        Vector3d worldPos = physShip.getTransform().getShipToWorld().transformPosition(shipPos, new Vector3d());

        double velSqLen = physShip.getVelocity().lengthSquared();

        Vector3d worldForward =  physShip.getTransform().getShipToWorldRotation().transform(shipForward, new Vector3d());
        Vector3dc velDir = physShip.getVelocity().normalize(new Vector3d());

        Vector3d r = worldPos.sub(massCenter, new Vector3d());  //is direction right?
        //Vector3d torque = velDir.cross(worldForward, new Vector3d()).mul(velSqLen * r.length() * correctFactor);

        /.*Vector3d worldPos = physShip.getTransform().getShipToWorld().transformPosition(shipPos, new Vector3d());
        //physShip.getCenterOfMass();

        Vector3d worldForward =  physShip.getTransform().getShipToWorldRotation().transform(shipForward, new Vector3d());
        Vector3dc velocity = physShip.getVelocity();

        double projOnForward = velocity.dot(worldForward);  //length of forward is 1, so not to div length of forward
        Vector3d expectedVelocity = worldForward.mul(projOnForward, new Vector3d());

        Vector3d bias = expectedVelocity.sub(velocity, new Vector3d());
        Vector3d biasWithSqLen = bias.mul(bias.length(), new Vector3d());

        Vector3d correctForce = biasWithSqLen.mul(correctFactor, new Vector3d());
        Vector3d dragForce = velocity.mul(-velocity.length() * dragFactor, new Vector3d());

        Vector3d force = correctForce.add(dragForce, new Vector3d());
        physShip.applyInvariantForce(force); //notice correctForce has changed since here
        EzDebug.Log("try apply correct:" + correctForce + ", drag:" + dragForce + ", q = " + q + ", vel * vel = " + velocity.lengthSquared());*./

        Vector3d torque = physShip.getVelocity().cross(worldForward, new Vector3d());

        if (torque.isFinite())
            physShip.applyInvariantTorque(torque);

        EzDebug.Log("apply torque:" + torque);

    }*./
}*/