package com.lancas.vs_wap.obsolete;

/*
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
//todo only a ship's AABB has a side with 1 block width and 1 block height can be considered as a ballisticShip
//and the side ends with a primer is the bottom
//todo player may change the ballistic while it is in barrel
public class BallisticShipWrapper {
    private final long shipId;

    @JsonIgnore
    private  ServerShip sShip;
    //private SavedBlockPos primerBp;
    private SavedBlockPos headBp;
    private final Vector3ic headDirInShip;

    public BallisticShipWrapper() {
        shipId = -1;
        //primerBp = new SavedBlockPos();
        headBp = new SavedBlockPos();
        headDirInShip = new Vector3i(0, 0, 1);
    }
    public BallisticShipWrapper(long inShipId, Vector3ic inHeadDirInShip) {
        shipId = inShipId;
        headDirInShip = inHeadDirInShip;
    }
    public BallisticShipWrapper(@NotNull Ship ship, Vector3ic inHeadDirInShip) {
        shipId = ship.getId();
        headDirInShip = inHeadDirInShip;
    }

    public long getShipId(ServerLevel level) {
        if (!gatherShipWithData(level)) return -1;
        return sShip.getId();
    }
    public boolean isExist(ServerLevel level) {
        return gatherShipWithData(level);
    }
    public ServerShip getShip(ServerLevel level) {
        if (!gatherShipWithData(level)) return null;
        return sShip;
    }
    public ServerShip getExistedShip() {
        return sShip;
    }

    public Vector3i getHeadDirInShip(ServerLevel level) {
        if (!gatherShipWithData(level)) return null;
        return new Vector3i(headDirInShip);
    }
    public Vector3d getHeadDirInWorld(ServerLevel level) {
        if (!gatherShipWithData(level)) return null;
        return sShip.getTransform().getShipToWorldRotation().transform(new Vector3d(headDirInShip));
    }
    public void rotateHeadDirInWorldTo(ServerLevel level, Vector3dc worldTowards) {
        Vector3dc headDirInWorld = getHeadDirInWorld(level);
        //Quaterniond rotation = new Quaterniond().rotateTo(headDirInWorld, worldTowards);
        Vector3d rotAxis = headDirInWorld.cross(worldTowards, new Vector3d());
        EzDebug.Log("rotAxis:" + StringUtil.toNormalString(rotAxis) + ", headDirInWorld:" + StringUtil.toNormalString(headDirInWorld) + ", worldTowards:" + StringUtil.toNormalString(worldTowards));
    }

    /.*public void correctAngle(ServerLevel level, Vector3dc targetForward) {
        if (!gatherShip(level)) return;

    }*./
    //a low energy is 4E6?
    public void applyEnergy(@NotNull ServerLevel level, Vector3dc barrelDir, double energy) {  //power is ken energy
        if (!gatherShipWithData(level)) return;
        double mass = sShip.getInertiaData().getMass();
        Vector3dc preVel = sShip.getVelocity();

        double prevEnergy = mass * preVel.lengthSquared();
        double newEnergy = prevEnergy + energy;
        double newSpeed = Math.sqrt(2 * newEnergy / mass);

        //todo preVel may be zero.
        Vector3d newVel = barrelDir.normalize(newSpeed, new Vector3d());
        //EzDebug.Log("energy:" + energy + ", preEnergy:" + prevEnergy + ", newEnergy:" + newEnergy + "\npreVel:" + preVel + ", newSpeed:" + newSpeed + ", newVel:" + newVel);
        setSpeed(level, newVel);
    }
    public void setSpeed(@NotNull ServerLevel level, Vector3dc newVel) {
        if (!gatherShipWithData(level)) return;

        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(sShip, new ShipTeleportDataImpl(
            sShip.getTransform().getPositionInWorld(),
            sShip.getTransform().getShipToWorldRotation(),
            newVel,
            sShip.getOmega(),
            VSGameUtilsKt.getDimensionId(level),
            sShip.getTransform().getShipToWorldScaling().x()
        ));
        EzDebug.Log("set vel:" + StringUtil.toNormalString(newVel));
    }

    private boolean gatherShipWithData(@NotNull ServerLevel level) {
        if (sShip != null) return true;

        if (shipId < 0) {
            EzDebug.error("the ballistic wrapper is not inited!");
            return false;
        }
        sShip = (ServerShip)ShipUtil.getShipByID(level, shipId);
        if (sShip == null) {
            EzDebug.error("fail to get ship with id:" + shipId + ", maybe the ship is removed");
            return false;
        }

        if (!gatherShipData())
            return false;

        return true;
    }
    private boolean gatherShipData() {  //suppose the sShip is not null
        AABBic shipAABB = sShip.getShipAABB();
        if (shipAABB == null) {
            EzDebug.fatal("ship aabb is null");
            return false;
        }

        if (headDirInShip.equals(1, 0, 0)) {
            headBp = new SavedBlockPos(shipAABB.maxX() - 1, shipAABB.minY(), shipAABB.minZ());
        } else if (headDirInShip.equals(-1, 0, 0)) {
            headBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.minZ());
        } else if (headDirInShip.equals(0, 1, 0)) {
            headBp = new SavedBlockPos(shipAABB.minX(), shipAABB.maxY() - 1, shipAABB.minZ());
        } else if (headDirInShip.equals(0, -1, 0)) {
            headBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.minZ());
        } else if (headDirInShip.equals(0, 0, 1)) {
            headBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.maxZ() - 1);
        } else if (headDirInShip.equals(0, 0, -1)) {
            headBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.minZ());
        } else {
            EzDebug.fatal("headDirInShip is not valid:" + headDirInShip);
            return false;
        }

        return true;

        /*int xLen = shipAABB.maxX() - shipAABB.minX();
        int yLen = shipAABB.maxY() - shipAABB.minY();
        int zLen = shipAABB.maxZ() - shipAABB.minZ();
        //todo ballistic may be a cube? (xLen, yLen, zLen are all 1)
        //todo just add this to ballistic ship judging and don't consider the ship as a ballisticShip
        if (xLen == 1 && yLen == 1) {  //z axis ballistic
            SavedBlockPos zLowerBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.minZ());
            SavedBlockPos zHigherBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.maxZ() - 1);
            BlockState zLowerState = level.getBlockState(zLowerBp.toBp());
            BlockState zHigherState = level.getBlockState(zHigherBp.toBp());
            if (zLowerState.getBlock() instanceof IPrimer) {
                primerBp = zLowerBp;
                headBp = zHigherBp;
                headDirInShip = new Vector3d(0, 0, 1);
            } else if (zHigherState.getBlock() instanceof IPrimer) {
                primerBp = zHigherBp;
                headBp = zLowerBp;
                headDirInShip = new Vector3d(0, 0, -1);
            } else {
                EzDebug.error("can not find primer. The primer block must be the bottom of a projectile. zLower:" +
                    StringUtil.getBlockName(zLowerState) + ", at " + StringUtil.getBlockPos(zLowerBp) +
                    ", zHigher:" + StringUtil.getBlockName(zHigherState) + ", at " + StringUtil.getBlockPos(zHigherBp) + "\n" +
                    "shipAABB:" + shipAABB.toString()
                );
                return false;
            }
            return true;

        } else if (yLen == 1 && zLen == 1) {  //x axis ballistic
            SavedBlockPos xLowerBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.minZ());
            SavedBlockPos xHigherBp = new SavedBlockPos(shipAABB.maxX() - 1, shipAABB.minY(), shipAABB.minZ());
            BlockState xLowerState = level.getBlockState(xLowerBp.toBp());
            BlockState xHigherState = level.getBlockState(xHigherBp.toBp());
            if (xLowerState.getBlock() instanceof IPrimer) {
                primerBp = xLowerBp;
                headBp = xHigherBp;
                headDirInShip = new Vector3d(1, 0, 0);
            } else if (xHigherState.getBlock() instanceof IPrimer) {
                primerBp = xHigherBp;
                headBp = xLowerBp;
                headDirInShip = new Vector3d(-1, 0, 0);
            } else {
                EzDebug.error("can not find primer. The primer block must be the bottom of a projectile. xLower:" +
                    StringUtil.getBlockName(xLowerState) +
                    ", xHigher:" + StringUtil.getBlockName(xHigherState)
                );
                return false;
            }
            return true;

        } else if (xLen == 1 && zLen == 1) {  //y axis ballistic
            SavedBlockPos yLowerBp = new SavedBlockPos(shipAABB.minX(), shipAABB.minY(), shipAABB.minZ());
            SavedBlockPos yHigherBp = new SavedBlockPos(shipAABB.minX(), shipAABB.maxY() - 1, shipAABB.minZ());
            BlockState yLowerState = level.getBlockState(yLowerBp.toBp());
            BlockState yHigherState = level.getBlockState(yHigherBp.toBp());
            if (yLowerState.getBlock() instanceof IPrimer) {
                primerBp = yLowerBp;
                headBp = yHigherBp;
                headDirInShip = new Vector3d(0, 1, 0);
            } else if (yHigherState.getBlock() instanceof IPrimer) {
                primerBp = yHigherBp;
                headBp = yLowerBp;
                headDirInShip = new Vector3d(0, -1, 0);
            } else {
                EzDebug.error("can not find primer. The primer block must be the bottom of a projectile. yLower:" +
                    StringUtil.getBlockName(yLowerState) +
                    ", yHigher:" + StringUtil.getBlockName(yHigherState)
                );
                return false;
            }
            return true;

        } else {
            EzDebug.error("the ballistic ship's aabb' side area is not 1");
            return false;
        }*./
    }


}*/
