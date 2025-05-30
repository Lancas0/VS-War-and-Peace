package com.lancas.vswap.obsolete.mixin;

/*
import com.lancas.vs_wap.ship.attachment.TestForceInductor;
import com.lancas.vs_wap.obsolete.item.EinBoot;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.foundation.network.PlayerFollowShipPacket;
import com.lancas.vs_wap.obsolete.PlayerShipMgr;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ServerShip;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        //if (true) return;

        ServerPlayer player = (ServerPlayer) (Object) this;
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        ServerShip ship = PlayerShipMgr.getOrCreateShip((ServerLevel)player.level(), player.getUUID());
        if (ship == null) return;

        if (boots != null && boots.getItem() instanceof EinBoot) {
            ship.getAttachment(TestForceInductor.class).playerFollowIt = true;

            Vector3dc shipPos = ship.getTransform().getPositionInWorld();

            // 发送数据包到客户端
            NetworkHandler.channel.send(
                PacketDistributor.PLAYER.with(() -> player),
                new PlayerFollowShipPacket(JomlUtil.v3(shipPos))
            );
        } else {
            ship.getAttachment(TestForceInductor.class).playerFollowIt = false;
        }
    }
}
*/