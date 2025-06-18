package com.lancas.vswap.subproject.sandbox.constraint;

import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.constraint.base.IAxisOrientation;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderOrientationConstraint;
import org.joml.Vector3dc;

import java.util.UUID;

public class AxisSliderOrientationConstraint extends SliderConstraint implements ISliderConstraint, IAxisOrientation {
    public AxisSliderOrientationConstraint(UUID selfUuid, UUID inBodyAuuid, UUID inBodyBuuid, Vector3dc inLocalAPos, Vector3dc inLocalBPos, Vector3dc sliderAxisInALocal) {
        super(selfUuid, inBodyAuuid, inBodyBuuid, inLocalAPos, inLocalBPos, sliderAxisInALocal);
    }


}
