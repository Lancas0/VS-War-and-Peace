package com.lancas.vs_wap.subproject.sandbox.api;

import com.lancas.vs_wap.event.api.ILazyEventParam;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;

public class AABBdLazyParamWrapper implements ILazyEventParam<AABBdLazyParamWrapper> {
    private final double minX, minY, minZ, maxX, maxY, maxZ;

    public AABBdLazyParamWrapper(double inMinX, double inMinY, double inMinZ, double inMaxX, double inMaxY, double inMaxZ) {
        minX = inMinX; maxX = inMaxX;
        minY = inMinY; maxY = inMaxY;
        minZ = inMinZ; maxZ = inMaxZ;
    }
    public AABBdLazyParamWrapper(AABBdc aabb) {
        minX = aabb.minX(); maxX = aabb.maxX();
        minY = aabb.minY(); maxY = aabb.maxY();
        minZ = aabb.minZ(); maxZ = aabb.maxZ();
    }
    public AABBd getAABB(AABBd dest) {
        dest.setMin(minX, minY, minZ);
        return dest.setMax(maxX, maxY, maxZ);
    }

    @Override
    public AABBdLazyParamWrapper getImmutable() {
        return this;
    }
}
