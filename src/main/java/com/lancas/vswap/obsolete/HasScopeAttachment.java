package com.lancas.vswap.obsolete;

/*
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class HasScopeAttachment implements ServerTickListener {
    public long shipId = -1;
    public Vector3d offsetWithoutScale;
    public Vector3d scopeDir;  //todo set rotation
    public UUID playerUUID;

    @JsonIgnore
    private ServerShip _ship;

    public HasScopeAttachment() {}
    public HasScopeAttachment(@NotNull ServerShip ship, BlockPos scopeBP, Direction inScopeDir, @NotNull ServerPlayer player) {
        shipId = ship.getId();
        playerUUID = player.getUUID();

        BlockPos shipyardCenterBP = ShipUtil.getShipyardCenterBP(ship);
        offsetWithoutScale = JomlUtil.d(scopeBP.subtract(shipyardCenterBP)).add(1, 0, 0.5);  //z额外加0.5才能让方块变为中心，暂时先这样

        /.*switch (inScopeDir) {
            case NORTH -> scopeDir = new Vector3d(1, 0, 0);
            case
        }*./
    }

    private ServerShip getShip() {
        if (shipId < 0) return null;
        if (_ship != null) return _ship;

        ServerPlayer player = ServerDataCollector.playerList.getPlayer(playerUUID);
        if (player == null) return null;
        _ship = ShipUtil.getShipByID((ServerLevel)player.level(), shipId);
        return _ship;
    }

    public Vector3d getWorldScopePos() {
        ServerShip ship = getShip();
        if (ship == null) return null;

        Matrix4dc ship2World = ship.getShipToWorld();
        Vector3dc scale = ship.getTransform().getShipToWorldScaling();

        //EzDebug.Log("ship2World:" + ship2World + ", shipyardCenter:" + ship.getTransform().getPositionInShip() + ", offset:" + offsetWithoutScale.mul(scale, new Vector3d()));

        return ship2World.transformPosition(ship.getTransform().getPositionInShip().add(offsetWithoutScale.mul(scale, new Vector3d()), new Vector3d()));
    }
    //todo getWorldRotation(X,Y)

    @Override
    public void onServerTick() {
        //var pos = getWorldScopePos();
        //EzDebug.Log("pos is:" + pos);
    }
}
*/