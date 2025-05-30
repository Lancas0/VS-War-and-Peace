package com.lancas.vswap.ship.feature.hold;

import com.lancas.vswap.foundation.api.Dest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

//todo shift to lower the ship
public interface ICanHoldShip {
    public void getHoldingShipId(ShipHoldSlot slot, Dest<Long> holdingShipIdDest);
    public boolean isShipHolding(long shipId);
    public boolean isHoldingShip(ShipHoldSlot slot);

    public boolean tryHoldInServer(ShipHoldSlot slot, long shipId, boolean syncClient);
    public void unholdShipInServer(ShipHoldSlot slot, boolean syncClient, @Nullable Dest<Long> prevHoldShipIdDest);

    public void syncHoldShipInClient(ShipHoldSlot slot, long newHoldShipId, BlockPos holdBpInShip, Direction forwardInShip);
    //todo carry ship on back


    public static boolean isShipHolden(ServerShip ship) {
        var holdingAtt = ship.getAttachment(HoldingServerTickAttachment.class);
        return holdingAtt != null && holdingAtt.getHolderUUID() != null;
    }
    /*public Vector3d getHoldPos(BlockPos holdBpInShip, Direction forwardInShip, Matrix4dc shipToWorld, Vector3dc shipPosInWorld);
    public Quaterniond getHoldRotation(Matrix4dc shipToWorld, Direction forwardInShip);*/
}
