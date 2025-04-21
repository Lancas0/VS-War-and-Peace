package com.lancas.vs_wap.foundation.algorithm;

import org.joml.Vector3i;

import java.util.HashSet;
import java.util.function.BiFunction;

public class AStar {
    public Vector3i start, end;
    public BiFunction<Vector3i, Vector3i, Double> distanceFunc;

    public HashSet<Vector3i> obstacle;

}
