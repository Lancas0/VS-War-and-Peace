package com.lancas.vs_wap.obsolete;
/*
import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.EzDebug;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SchemeTranslateAndRotateRegister {
    public static final int MIN_DIST = 1;
    public static final int EVENT_ROTATE = 0;
    public static final int EVENT_TRANSLATE = 1;
    //todo 是否需要正常模式(切换物品栏)

    private static Vector3f rotateAxis = new Vector3f(1, 0, 0);
    public static Quaternionf rotation = new Quaternionf();

    private static int scrollEventMode = 0;  //0 for rotate, 1 for move distant
    public static int distance = 5;

    private static boolean lastFrameCtrlPressed = false;
    private static boolean lastFrameAltPressed = false;

    // 监听键盘事件检测 Ctrl 键
    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        boolean curFrameCtrlPressing = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) ||
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL);

        boolean curFrameAltPressed =
            InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT) ||
            InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT);

        if (curFrameCtrlPressing && !lastFrameCtrlPressed) {
            scrollEventMode = (scrollEventMode + 1) % 2;

            switch (scrollEventMode) {
                case EVENT_ROTATE -> EzDebug.log("Currently in rotate mode");
                case EVENT_TRANSLATE -> EzDebug.log("Currently in translate mode");
            }
        }

        if (scrollEventMode == EVENT_ROTATE && curFrameAltPressed && !lastFrameAltPressed) {
            cycleRotateAxis();

            if (rotateAxis.x > 0)
                EzDebug.log("Currently rotate around x axis");
            else if (rotateAxis.y > 0)
                EzDebug.log("Currently rotate around y axis");
            else
                EzDebug.log("Currently rotate around z axis");
        }

        lastFrameAltPressed = curFrameAltPressed;
        lastFrameCtrlPressed = curFrameCtrlPressing;
    }

    // 监听鼠标滚轮事件
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().player == null) return;

        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();


        if (stack.getItem() instanceof ShipSchemeItem) {
            switch (scrollEventMode) {
                case EVENT_ROTATE -> rotate(event.getScrollDelta());
                case EVENT_TRANSLATE -> translate(event.getScrollDelta());
            }

            event.setCanceled(true);
        }

        //todo 是否要设一个重置键，或者某些情况下触发重置旋转
        // 计算旋转方向（滚轮向上为正向，向下为反向）
        //rotationDelta += event.getScrollDelta() > 0 ? 15.0f : -15.0f;
        //event.setCanceled(true); // 阻止物品栏切换
    }

    private static void cycleRotateAxis() {
        float temp = rotateAxis.z;
        rotateAxis.z = rotateAxis.y;
        rotateAxis.y = rotateAxis.x;
        rotateAxis.x = temp;
    }
    private static void rotate(double scrollDelta) {
        float delta = scrollDelta > 0 ? 0.1f : -0.1f;
        rotation.rotateAxis(delta, rotateAxis);
    }
    private static void translate(double scrollDelta) {
        int delta = scrollDelta > 0 ? 1 : -1;
        distance = Math.max(distance + delta, MIN_DIST);
        EzDebug.log("current dist is " + distance);
    }
}
*/