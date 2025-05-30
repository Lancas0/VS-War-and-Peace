package com.lancas.vswap.obsolete;

/*
public class FollowPlayerTP implements ServerShipTransformProvider {
    private static final Vector3d GRAVITY = new Vector3d(0, -9.8, 0);

    public static FollowPlayerTP getOrCreate(ServerShip ship, UUID setIfCreate) {
        ServerShipTransformProvider tp = ship.getTransformProvider();
        if (tp instanceof FollowPlayerTP) {
            return (FollowPlayerTP)tp;
        } else {
            FollowPlayerTP newTp = new FollowPlayerTP(setIfCreate);
            ship.setTransformProvider(newTp);
            return newTp;
        }
    }

    private final UUID playerUUID;
    public Vector3d nextPos;
    public boolean playerFollowIt = false;

    public FollowPlayerTP(UUID inPlayerUUID) {
        playerUUID = inPlayerUUID;
    }

    @Override
    public @Nullable NextTransformAndVelocityData provideNextTransformAndVelocity(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1) {
        ServerPlayer player = ServerDataCollector.playerList.getPlayer(playerUUID);
        if (player == null) return null;

        Vector3d curPos;
        if (nextPos == null) {
            curPos = JomlUtil.d(player.position().add(0, 1, 0));
        } else {
            curPos = nextPos;
            nextPos = null;
        }

        return new NextTransformAndVelocityData(
            new ShipTransformImpl(
                curPos,
                shipTransform.getPositionInShip(),
                new Quaterniond().rotationTo(
                    new Vector3d(1, 0, 0),
                    calculateViewVector(0, player.getYRot())
                ),
                shipTransform.getShipToWorldScaling()
            ),
            new Vector3d(),
            new Vector3d()
        );
    }


    private Vector3d calculateViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * ((float)Math.PI / 180F);
        float f1 = -p_20173_ * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vector3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }
}
*/