package com.lancas.vswap.ship.ballistics.api;


import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface IComponent {
    public Vector3d getAdditionalAirDrag(Vector3dc worldVel);
}
