package com.lancas.vswap.subproject.sandbox.obsolete;

/*
//todo reload when change level
public class SandBoxClientShip implements ISandBoxShip {
    public final UUID uuid;
    private final SandBoxTransform transform;
    private final SandBoxTransform prevTransform;
    private final SandBoxTransform lerpTransform;

    private final SandBoxShipBlockCluster blockData;

    /.*private SandBoxClientShip() {
        uuid = -1;
        transform = new SandBoxTransform();
        prevTransform = new SandBoxTransform();
        lerpTransform = new SandBoxTransform();

        blockData = new SandBoxShipBlockData();
    }*./
    public SandBoxClientShip(UUID inId, SandBoxTransformData transformData, SandBoxBlockClusterData clusterData) {
        uuid = inId;
        transform = new SandBoxTransform();     transform.loadData(this, transformData);
        prevTransform = new SandBoxTransform(); prevTransform.loadData(this, transformData);
        lerpTransform = new SandBoxTransform(); lerpTransform.loadData(this, transformData);

        blockData = new SandBoxShipBlockCluster();
        blockData.loadData(this, clusterData);
    }
    /.*public static SandBoxClientShip fromServerShip(SandBoxServerShip serverShip) {
        SandBoxClientShip clientShip = new SandBoxClientShip(
            serverShip.getUuid(),
            new SandBoxTransformData(SandBoxServerShip.TransformOp.)
        );
    }*./

    // 渲染优化数据
    private final AABBd cachedWorldAABB = new AABBd();
    private boolean worldAABBDirty = true;
    private final Set<BlockPos> visibleBlocks = ConcurrentHashMap.newKeySet(); // 可见方块缓存

    private boolean isOpaque(BlockState state) {
        return state != null && state.canOcclude();
    }
    private boolean isVisible(BlockPos localPos) {
        BlockState state = blockData.getBlockOrNull(localPos);
        if (state == null) return false;

        // 检查六个相邻方向
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = localPos.relative(dir);
            BlockState neighborState = blockData.getBlockOrNull(neighborPos);

            if (neighborState == null) {
                return true;  //there is no block at neighbor, can be seen
            }
            if (!isOpaque(neighborState)) {
                return true; //the neighbor is not opaque
            }
        }
        return false;
    }
    private void updateBlockVisibility(BlockPos localPos) {
        if (isVisible(localPos))
            visibleBlocks.add(localPos);
        else
            visibleBlocks.remove(localPos);
    }

    public void setBlock(BlockPos localPos, BlockState state) {
        if (state == null || state.isAir()) {
            removeBlock(localPos);
            return;
        }

        blockData.setBlock(localPos, state);

        updateBlockVisibility(localPos);
        // update the visbility of neighour blocks
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = localPos.relative(dir);
            BlockState neighborState = blockData.getBlockOrNull(neighborPos);

            if (neighborState != null) {
                updateBlockVisibility(neighborPos);
            }
        }

        worldAABBDirty = true;
    }
    public void removeBlock(BlockPos localPos) {
        blockData.removeBlock(localPos);

        visibleBlocks.remove(localPos);
        // update the visbility of neighour blocks
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = localPos.relative(dir);
            BlockState neighborState = blockData.getBlockOrNull(neighborPos);

            if (neighborState != null) {
                updateBlockVisibility(neighborPos);
            }
        }

        worldAABBDirty = true;
    }

    /.*public void setPosition(Vector3dc newPos) {
        transform.setPosition(newPos);
        worldAABBDirty = true;
    }
    public void setRotation(Quaterniondc newRot) {
        transform.setRotation(newRot);
        worldAABBDirty = true;
    }
    public void setScale(Vector3dc newScale) {
        transform.setScale(newScale);
        worldAABBDirty = true;
    }*./
    public void updateTransform(SandBoxTransformData newTransformData) {
        prevTransform.set(transform);
        transform.set(newTransformData);
        worldAABBDirty = true;
    }
    public SandBoxTransform getLerpTransform(double t) {
        return prevTransform.lerp(transform, t, lerpTransform);
    }

    public Set<BlockPos> getVisibleBlocks() {
        return visibleBlocks;
    }


    @Override
    public UUID getUuid() { return uuid; }

    @Override
    @Nullable
    public AABBdc getWorldAABB() {
        if (blockData.getLocalAABB() == null) return null;

        if (worldAABBDirty) {
            if (blockData.getLocalAABB() == null) return null;  //empty aabb, don't set dirty false, it must update later.
            TransformUtil.quickTransform(transform.getLocalToWorld(), blockData.getLocalAABB(), cachedWorldAABB);
            worldAABBDirty = true;
        }
        return cachedWorldAABB;
    }

    @Override
    @Nullable
    public AABBic getLocalAABB() {
        return blockData.getLocalAABB();
    }


    @Override
    public SandBoxTransform getTransform() { return transform; }
    @Override
    public SandBoxShipBlockCluster getCluster() { return blockData; }

    @Override
    public String toString() {
        return "SandBoxClientShip{" +
            "uuid=" + uuid +
            ", transform=" + transform +
            '}';
    }


    /.*
    // ========================= 坐标变换 =========================
    // 局部坐标 → 世界坐标
    public Vector3d localToWorldPosition(Vector3d localPos) {
        Vector4d transformed = new Vector4d(localPos.x, localPos.y, localPos.z, 1.0);
        transformed.mul();
        return transform.get;
    }
    // 世界坐标 → 局部坐标
    public Vector3d worldToLocalPosition(Vector3d worldPos) {
        Vector4d transformed = new Vector4d(worldPos.x, worldPos.y, worldPos.z, 1.0);
        transformed.mul(transform.getWorldToLocal());
        return new Vector3d(transformed.x, transformed.y, transformed.z);
    }
    *./

}
*/