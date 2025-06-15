package com.lancas.vswap.content.block.blockentity;

import com.lancas.vswap.content.block.blocks.scope.IScopeBlock;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.handler.ScopeClientManager;
import com.lancas.vswap.register.KeyBinding;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4dc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.Objects;


public class ScopeBE extends BlockEntity {
    //last scoping cache, do not save
    private boolean lastSetScoping = false;
    //onship cache, do not save
    private Ship onShip = null;

    public ScopeBE(BlockEntityType<?> type, BlockPos p_155229_, BlockState p_155230_) {
        super(type, p_155229_, p_155230_);
    }

    public void tick() {
        //only tick in client side
        if (level == null || !(level instanceof ClientLevel cLevel)) return;

        Player player = Minecraft.getInstance().player;
        if (!(player instanceof ICanHoldShip icanHoldShip)) {
            EzDebug.fatal("player can not hold ship because of unkown reason");
            return;
        }

        onShip = (onShip != null ? onShip : ShipUtil.getShipAt(level, worldPosition));
        if (onShip == null) return;
        //todo other slot?
        Long holdingShipId = icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand);
        //if the holding ship is not this ship, return
        if (!Objects.equals(holdingShipId, onShip.getId())) return;

        BlockState stateOfThis = getBlockState();
        if (!(stateOfThis.getBlock() instanceof IScopeBlock iscope))
            return;

        Direction scopeDir = IScopeBlock.getScopeDirection(stateOfThis);
        if (scopeDir == null) return;


        boolean keyPressing = KeyBinding.ScopeKey.isPressing();
        if (keyPressing || lastSetScoping) {
            /*NetworkHandler.channel.sendToServer(
                new ScopeOnShipPacketC2S(keyPressing, iscope.getFovMultiplier(), this.getBlockPos(), scopeDir, iscope.getCameraOffsetAlongForwardF())
            );*/
            Quaterniondc shipRot = onShip.getTransform().getShipToWorldRotation();
            Vector3d localForward = JomlUtil.d(scopeDir.getNormal());
            Vector3d worldForward = shipRot.transform(localForward, new Vector3d());

            Vector3d locOffset = IScopeBlock.getRotationByScopeDir(scopeDir).transform(iscope.getCameraOffsetAlongForward());
            Matrix4dc ship2World = onShip.getShipToWorld();
            Vector3d cameraWorldPos = ship2World.transformPosition(JomlUtil.dCenter(worldPosition).add(locOffset));//.add(offset);

            boolean scoping = keyPressing;
            ScopeClientManager.setScopeData(scoping, iscope.getFovMultiplier(), cameraWorldPos, worldForward);

            lastSetScoping = keyPressing;
        }
    }

}