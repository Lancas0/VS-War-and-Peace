package com.lancas.vs_wap.mixins;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    /*@Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
    private void blockMovement(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof EinBoot) {
            ci.cancel(); // 阻止客户端发送移动数据
        }
    }*/
    @Inject(method = "tick", at = @At("HEAD"))
    private void debugTest(CallbackInfo ci) {
        //EzDebug.Log("south:" + StringUtil.toF2String(Direction.SOUTH.getRotation().getEulerAnglesYXZ(new Vector3f()).get(new Vector3d()).mul(180 / Math.PI)));
        //EzDebug.Log("north:" + StringUtil.toF2String(Direction.NORTH.getRotation().getEulerAnglesYXZ(new Vector3f()).get(new Vector3d()).mul(180 / Math.PI)));
        //EzDebug.Log("west:" + StringUtil.toF2String(Direction.WEST.getRotation().getEulerAnglesYXZ(new Vector3f()).get(new Vector3d()).mul(180 / Math.PI)));
        //EzDebug.Log("east:" + StringUtil.toF2String(Direction.EAST.getRotation().getEulerAnglesYXZ(new Vector3f()).get(new Vector3d()).mul(180 / Math.PI)));

        //EzDebug.Log("debug graphics test");
        //LocalPlayer player = (LocalPlayer) (Object) this;
        //EzDebugGraphics.AddShape("LocalPlayerDebugTest", new DebugVector(JomlUtil.d(player.position()), new Vector3d(0, 5, 0), 255, 0));
        //TestRenderer2.poses.put("LocalPlayerDebugTest", JomlUtil.d(player.position()));
    }
}
