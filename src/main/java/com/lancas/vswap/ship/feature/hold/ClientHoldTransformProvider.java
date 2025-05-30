package com.lancas.vswap.ship.feature.hold;

import com.lancas.vswap.ship.helper.builder.ShipTransformBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ClientShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

public class ClientHoldTransformProvider implements ClientShipTransformProvider {
    private static final double LERP_SMOOTH = 1;

    private final ShipHoldSlot slot;
    private final BlockPos holdBpInShip;
    private final Direction forwardInShip;

    private static StopWatch sw = new StopWatch();

    public ClientHoldTransformProvider(ShipHoldSlot inSlot, BlockPos inHoldBpInShip, Direction inForwardInShip) {
        slot = inSlot;
        holdBpInShip = inHoldBpInShip;
        forwardInShip = inForwardInShip;
    }

    @Override
    public @Nullable ShipTransform provideNextRenderTransform(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1, double partialTick) {
        /*if (!sw.isStarted()) sw.start();
        EzDebug.log("current time:" + StrUtil.F2(sw.getTime(TimeUnit.MILLISECONDS)) + " ms");

        return getNextTransform(shipTransform, LERP_SMOOTH);*/
        return null;
    }

    @Override
    public @Nullable ShipTransform provideNextTransform(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1, @NotNull ShipTransform shipTransform2) {
        //if (!sw.isStarted()) sw.start();
        //EzDebug.log("current time:" + StrUtil.F2(sw.getTime(TimeUnit.MILLISECONDS)) + " ms");
        return getNextTransform(shipTransform, LERP_SMOOTH);
    }

    private @Nullable ShipTransform getNextTransform(ShipTransform prevTransform, double lerpSmooth) {
        Player player = Minecraft.getInstance().player;
        /*if (!(player instanceof ICanHoldShip icanHoldShip)) {
            EzDebug.fatal("player can't hold ship because of unknown reason");
            return null;
        }*/

        Matrix4dc shipToWorld = prevTransform.getShipToWorld();

        Vector3dc prevWorldPos = prevTransform.getPositionInWorld();
        Quaterniondc prevRotation = prevTransform.getShipToWorldRotation();

        Vector3d newWorldPos = slot.getHoldPos(player, holdBpInShip, forwardInShip, shipToWorld, prevTransform.getPositionInWorld());
        Quaterniond newRotation = slot.getHoldRotation(player, holdBpInShip, forwardInShip);

        return ShipTransformBuilder.copy(prevTransform)
            .setPosInWorld(prevWorldPos.lerp(newWorldPos, lerpSmooth, new Vector3d()))
            .setRotation(prevRotation.nlerp(newRotation, lerpSmooth, new Quaterniond()))
            .get();
    }
}
