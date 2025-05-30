package com.lancas.vswap.handler;

/*
public class HoldShipHandler {
    public static boolean releaseGrabed(ServerLevel level, ServerShip ship) {  //todo release on ground
        if (ship == null) return false;

        if (donotThrowIfNotHolding) {
            PlayerHoldingAttachment att = ship.getAttachment(PlayerHoldingAttachment.class);
            if (att == null) {
                EzDebug.error("ship should have holding attachment");
                return false;
            }
            if (!att.holding) {
                return false;
            }
        }

        //reset the gravity
        //AntiGravityForceInducer.active(ship, false);
        //reset static
        ship.setStatic(false);

        //todo reset so that it is possible to collide with player
        clearShipID(stack);
        ship.saveAttachment(PlayerHoldingAttachment.class, null);

        if (ship.getTransformProvider() instanceof MountPlayerTP) {
            ship.setTransformProvider(null);
        }

        return true;
    }
    public static void grabShip(ServerPlayer player, ServerShip ship) {
        //ship.setStatic(true);
        //PlayerCollisionAttachment.getOrAdd(ship);
        //todo idk why but it is nesscery to add throw force inducer at first so can be throw later;
        ThrowForceInducer.createOrReset(ship, new Vector3d());

        //set static to avoid shake
        ship.setStatic(true);

        NoPlayerCollisionAttachment.apply(ship, player);

        PlayerHoldingAttachment holdingAtt = new PlayerHoldingAttachment(player.getUUID(), ship.getId(), MountToPlayerTypes.MountToMainHand);
        holdingAtt.addTo(ship);
        ship.setTransformProvider(new MountPlayerHandTP(holdingAtt));
    }
}
*/