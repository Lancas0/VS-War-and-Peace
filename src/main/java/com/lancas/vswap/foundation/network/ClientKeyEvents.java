package com.lancas.vswap.foundation.network;

import com.lancas.vswap.ModMain;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.register.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.event.;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Hashtable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientKeyEvents {
    private static class KeyState {
        public boolean keyDown = false;
        public int tickCnt = 0;
    }
    private static boolean inited = false;
    private static Hashtable<KeyBinding, KeyState> prevKeyStates = new Hashtable<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (!inited) {
            initKeys();
            inited = true;
        }

        for (KeyBinding keyBinding : KeyBinding.values()) {
            boolean isKeyDown = keyBinding.isPressing();
            KeyState prevState = prevKeyStates.get(keyBinding);
            if (prevState == null) {
                //this should never be called
                EzDebug.log("[AssertFail]key is not inited");
                return;
            }

            //keyDown发生变化，触发onKeyEvent
            if (prevState.keyDown != isKeyDown) {
                // 发送数据包到服务端
                keyBinding.invokeOnKeyEvent(isKeyDown);

                if (!isKeyDown)
                    keyBinding.invokeKeyReleaseEvent(prevState.tickCnt);
            }

            //pressing evt
            if (isKeyDown) {
                keyBinding.invokePressingEvent(prevState.tickCnt + 1);
            }

            prevState.keyDown = isKeyDown;
            if (isKeyDown)
                prevState.tickCnt++;
            else
                prevState.tickCnt = 0;
        }
    }

    private static void initKeys() {
        for (KeyBinding keyBinding : KeyBinding.values())
            prevKeyStates.put(keyBinding, new KeyState());
    }
}