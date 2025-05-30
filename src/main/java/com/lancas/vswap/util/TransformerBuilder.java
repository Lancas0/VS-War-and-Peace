package com.lancas.vswap.util;

import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3dc;

public class TransformerBuilder {
    private Matrix4d transformer = new Matrix4d();

    public TransformerBuilder() {}
    public TransformerBuilder moveWorld(Vector3dc movement) {
        transformer.translate(movement);
        return this;
    }

}
