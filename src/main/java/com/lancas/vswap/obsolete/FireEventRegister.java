package com.lancas.vswap.obsolete;

/*
@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FireEventRegister {

    public static KeyBinding fireKey = KeyBinding.TEST_KEY;

    private static boolean isMouseBtnPressing = false;

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (fireKey.isPressing())
            EzDebug.Log("test key is pressing, action = " + event.getAction());
    }
    @SubscribeEvent
    public static void onMouseBtnDown(InputEvent.MouseButton.Post event) {
        isMouseBtnPressing = true;

        if (fireKey.isPressing())
            EzDebug.Log("test key is down");
    }
    @SubscribeEvent
    public static void onMouseBtnUp(InputEvent.MouseButton.Pre event) {
        isMouseBtnPressing = false;

        if (fireKey.isPressing())
            EzDebug.Log("test key is up");
    }

    @SubscribeEvent
    public static void onMouseBtnPressing(TickEvent.ClientTickEvent event) {
        if (!isMouseBtnPressing)
            return;

        if(fireKey.isPressing())
            EzDebug.Log("test key is pressing");
    }
}*/