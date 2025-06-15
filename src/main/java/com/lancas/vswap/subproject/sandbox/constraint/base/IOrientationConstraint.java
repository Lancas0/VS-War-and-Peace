package com.lancas.vswap.subproject.sandbox.constraint.base;

import org.joml.Quaterniondc;

public interface IOrientationConstraint extends IConstraint {
    //public void setALocalRot(Quaterniondc inALocalRot);
    //public void setBLocalRot(Quaterniondc inBLocalRot);
    public void setTargetLocalRot(Quaterniondc inLocalRot);
}
