package com.lancas.vswap.handler;

/*
public class ScopeServerHandler {
    //private static final Map<UUID, Boolean> isZooming = new HashMap<>();

    //todo do not use forward but the direction scope pointing
    public static void scopeOnContraption(ServerPlayer player, boolean scoping, float inScopeFovMultiplier, BlockPos anchorBP, Vector3f anchorRelative, Vector3f contraptionForward, Vector3f cameraOffsetOnContraption) {
        //todo use in client
        ItemStack mainHandStack = player.getMainHandItem();

        //todo determine the right player and scope
        //if (!(mainHandStack.getItem() instanceof EinheriarWand))
        //    return;

        //ServerShip ship = EinheriarWand.getShip((ServerLevel)player.level(), mainHandStack);//ShipUtil.getShipByID((ServerLevel)player.level(), shipId);
        //todo check is holding
        ServerShip ship = ShipUtil.getServerShipAt((ServerLevel)player.level(), anchorBP);
        if (ship == null) return;

        Quaterniondc shipRot = ship.getTransform().getShipToWorldRotation();
        Vector3f worldForward = shipRot.transform(contraptionForward, new Vector3f());

        /.*Quaternionf locRot = new Quaternionf().rotateTo(new Vector3f(0, 0, 1), contraptionForward);
        Vector3f locRotEuler = locRot.getEulerAnglesYXZ(new Vector3f());
        locRot.rotateYXZ(locRotEuler.y, locRotEuler.x, 0);
        Vector3d locOffset = locRot.transform(cameraOffsetAlongForward).get(new Vector3d());*./

        Matrix4dc ship2World = ship.getShipToWorld();
        Vector3d cameraWorldPos = ship2World.transformPosition(
            JomlUtil.d(anchorBP.getCenter()).add(anchorRelative).add(cameraOffsetOnContraption)
        );

        //EzDebug.Log("locRot:" + locRotEuler + ", locOffset:" + locOffset + ", wrdFwd:" + worldForward);

        //Vector3f additionalOffset = worldForward.mul(cameraOffsetAlongForward, new Vector3f());
        //Quaterniondc anchorRot = new Quaterniond().rotateTo(new Vector3d(0, 0, 1), contraptionForward.get(new Vector3d()));
        //Quaterniondc rotation = shipRot.mul(anchorRot, new Quaterniond());
        // 同步状态到客户端（用于渲染）
        NetworkHandler.channel.send(
            PacketDistributor.PLAYER.with(() -> player),
            new ScopeSetClientDataPacketS2C(
                scoping,
                inScopeFovMultiplier,
                cameraWorldPos.get(new Vector3f()),
                //rotation.get(new Quaternionf()),
                worldForward
            )
        );
        //for test
        //EzDebug.Log("try set player pos");
        //player.setPos(JomlUtil.mc(scopeWorldPos));
    }

    public static void scopeOnShip(Player player, boolean isScoping, float inScopeFovMultiplier, BlockPos scopePos, Direction scopeDir, Vector3f cameraOffsetAlongForward) {
        if (!player.isLocalPlayer()) {
            EzDebug.error("scopeOnShip is called in server. that should not happen.");
            return;
        }

        Ship ship = ShipUtil.getServerShipAt((ServerLevel)player.level(), scopePos);
        if (ship == null) return;

        Quaterniondc shipRot = ship.getTransform().getShipToWorldRotation();
        Vector3d localForward = JomlUtil.d(scopeDir.getNormal());
        Vector3d worldForward = shipRot.transform(localForward, new Vector3d());

        Vector3f locOffset = IScopeBlock.getRotationByScopeDir(scopeDir).transform(cameraOffsetAlongForward);
        Matrix4dc ship2World = ship.getShipToWorld();
        Vector3d cameraWorldPos = ship2World.transformPosition(JomlUtil.d(scopePos.getCenter()).add(locOffset));//.add(offset);

        ScopeClientManager.setScopeData(isScoping, inScopeFovMultiplier, cameraWorldPos, worldForward);
        // 同步状态到客户端（用于渲染）
        /.*NetworkHandler.sendToClientPlayer(
            player,
            new ScopeSetClientDataPacketS2C(
                isScoping,
                inScopeFovMultiplier,
                cameraWorldPos.get(new Vector3f()),
                worldForward.get(new Vector3f())
            )
        );*./
    }
}*/