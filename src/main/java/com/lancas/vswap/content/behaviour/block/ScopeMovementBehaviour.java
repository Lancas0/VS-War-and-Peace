package com.lancas.vswap.content.behaviour.block;

import com.lancas.vswap.content.block.blocks.scope.IScopeBlock;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.client2server.ScopeOnContraptionPacketC2S;
import com.lancas.vswap.register.KeyBinding;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ScopeMovementBehaviour implements MovementBehaviour {
    private boolean lastSetScoping = false;

    @Override
    public void tick(MovementContext ctx) {
        //only tick in client side
        if (!ctx.world.isClientSide)
            return;

        if (!(ctx.state.getBlock() instanceof IScopeBlock iscope))
            return;

        Direction scopeDir = IScopeBlock.getScopeDirection(ctx.state);
        if (scopeDir == null) return;

        ClientLevel cLevel = (ClientLevel)ctx.world;
        Boolean keyPressing = KeyBinding.ScopeKey.isPressing();

        if (keyPressing != null) {
            //keyPressing is true || lastSetScoping is true(while keyPressing is false);
            if (keyPressing || lastSetScoping) {

                //EzDebug.Log("[ScopeMoveBehaviour]send to server");

                BlockPos anchorBP = ctx.contraption.anchor;
                Vector3f cameraOffsetRotedByDir = IScopeBlock.getRotationByScopeDir(scopeDir).transform(iscope.getCameraOffsetAlongForwardF());
                NetworkHandler.channel.sendToServer(
                    new ScopeOnContraptionPacketC2S(
                        keyPressing,
                        iscope.getFovMultiplier(),
                        anchorBP,
                        JomlUtil.f(ctx.position.subtract(anchorBP.getCenter())),
                        JomlUtil.f(ctx.rotation.apply(JomlUtil.v3(scopeDir.getNormal()))),
                        JomlUtil.f(ctx.rotation.apply(JomlUtil.v3(cameraOffsetRotedByDir)))
                    )
                );
                lastSetScoping = keyPressing;
            }
        }
    }

    private Vec3 getLocalMovingPos(MovementContext ctx) {
        return ctx.position.subtract(ctx.contraption.anchor.getCenter());
    }
}