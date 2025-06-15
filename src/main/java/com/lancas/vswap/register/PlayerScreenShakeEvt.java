package com.lancas.vswap.register;

import com.lancas.vswap.WapConfig;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class PlayerScreenShakeEvt {
    private static float lastIntensity = 0;
    private static int remainTick = 0;

    public static void setShakeTickNoLessThan(int x) {
        remainTick = Math.max(x, remainTick);
    }
    public static void setShakeTicksNoLessThanDefaultTicks() { remainTick = Math.max(WapConfig.shakeTicks, remainTick); }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        //event.getPartialTick();
        /*event.getCamera();
        if (RPLClient.onCameraSetup(event.getCamera(), (float) event.getPartialTick(), new ForgeCameraModifier(event)) && event.isCancelable()) {
            event.setCanceled(true);
        }*/
        /*float intensity;
        if (remainTick <= 0) {
            intensity = Mth.lerp((float)event.getPartialTick(), lastIntensity, 0);
        } else {
            intensity = Mth.lerp((float)event.getPartialTick(), lastIntensity, (float)WapCommonConfig.shakeIntensity);
        }


        lastIntensity = intensity;

        if (event.getCamera().getEntity() instanceof Player player) {
            float yawOffset = intensity * (player.getRandom().nextFloat() - 0.5f);
            float pitchOffset = intensity * (player.getRandom().nextFloat() - 0.5f);

            event.setYaw(event.getYaw() + yawOffset);
            event.setPitch(event.getPitch() + pitchOffset);
        }

        if (remainTick > 0)
            remainTick--;*/
    }
}
