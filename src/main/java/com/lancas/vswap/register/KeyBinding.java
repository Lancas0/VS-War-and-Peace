package com.lancas.vswap.register;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.ui.DockerStateScreen;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.KeyPressPacket;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.client2server.SwapShipsInSlotC2S;
import com.lancas.vswap.foundation.network.client2server.ThrowShipPacketC2S;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.lancas.vswap.subproject.mstandardized.gui.MsSelectBlockMenu;
import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.Hashtable;
import java.util.UUID;
import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(modid = VsWap.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum KeyBinding {
    FireKey(
        "Fire Key",
        GLFW.GLFW_MOUSE_BUTTON_LEFT,
        true,
        (kb, pressing) -> NetworkHandler.channel.sendToServer(new KeyPressPacket(kb.getKey(), pressing)),
        null, null
    ),
    /*MoveForwardKey(
        "Move Forward Key",
        GLFW.GLFW_KEY_W,
        true,
        (kb, pressing) -> NetworkHandler.channel.sendToServer(new KeyPressPacket(kb.getKey(), pressing)),
        null, null
    ),
    MoveBackwardKey(
        "Move Backward Key",
        GLFW.GLFW_KEY_S,
        true,
        (kb, pressing) -> NetworkHandler.channel.sendToServer(new KeyPressPacket(kb.getKey(), pressing)),
        null, null
    ),
    MoveLeftKey(
        "Move Left Key",
        GLFW.GLFW_KEY_A,
        true,
        (kb, pressing) -> NetworkHandler.channel.sendToServer(new KeyPressPacket(kb.getKey(), pressing)),
        null, null
    ),
    MoveRightKey(
        "Move Right Key",
        GLFW.GLFW_KEY_D,
        true,
        (kb, pressing) -> NetworkHandler.channel.sendToServer(new KeyPressPacket(kb.getKey(), pressing)),
        null, null
    ),*/
    ScopeKey(
        "Scope Key",
        GLFW.GLFW_MOUSE_BUTTON_RIGHT,
        true,
        null, null, null
        //(kb, pressing) -> { if (!pressing) NetworkHandler.channel.sendToServer(new ScopeZoomPacket(false)); },
        //(kb) -> NetworkHandler.channel.sendToServer(new ScopeZoomPacket(true))
    ),
    /*CantLeftKey(
        "Cant Left Key",
        GLFW.GLFW_KEY_Z,
        true,
        (kb, pressing) -> { if (pressing) NetworkHandler.channel.sendToServer(new CantShipPacketC2S(true)); },
        null, null
    ),
    CantRightKey(
        "Cant Right Key",
        GLFW.GLFW_KEY_C,
        true,
        (kb, pressing) -> { if (pressing) NetworkHandler.channel.sendToServer(new CantShipPacketC2S(false)); },
        null, null
    ),*/
    ThrowKey(
        "Throw Key",
        GLFW.GLFW_KEY_G,
        true,
        null,
        null,
        (kb, tickCnt) -> { NetworkHandler.channel.sendToServer(new ThrowShipPacketC2S(tickCnt)); }
    ),
    CarryBackKey(
        "Carry Back",
        GLFW.GLFW_KEY_B,
        true,
        (key, keyDown) -> {
            if (keyDown) NetworkHandler.sendToServer(new SwapShipsInSlotC2S(ShipHoldSlot.MainHand, ShipHoldSlot.Back));
        },
        null,
        null
    ),
    LeftAlt(
        "AltKey",
        GLFW.GLFW_KEY_LEFT_ALT,
        true,
        (key, keyDown) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null)
                return;

            ItemStack handStack = player.getMainHandItem();
            if (handStack.getItem() instanceof MaterialStandardizedItem) {
                ScreenOpener.open(new MsSelectBlockMenu(handStack, InteractionHand.MAIN_HAND));
            } else if (handStack.getItem() instanceof Docker) {
                ScreenOpener.open(new DockerStateScreen(handStack, InteractionHand.MAIN_HAND));
            }
        },
        null, null
    ),
    TestKey(
        "Test Key",
        GLFW.GLFW_KEY_P,
        true,
        (kb, keyDown) -> { if (!keyDown) return;
            EzDebug.log("current sandbox client level:" + SandBoxClientWorld.INSTANCE.getCurLevelName());
            EzDebug.logs(() -> SandBoxClientWorld.INSTANCE.allClientShips().iterator(), SandBoxClientShip::toString);

        },
        null, null
    ),
    TestKey2(
        "Test Key2",
        GLFW.GLFW_KEY_O,
        true,
        (kb, keyDown) -> { if (!keyDown) return;
            //EzDebug.log("try create ship");
            //NetworkHandler.sendToServer(new CreateShipAtPlayerFromClientPacketC2S(1));
            /*NetworkHandler.sendToServer(new NetworkRunnable(ctx -> {
                EzDebug.log("server recieved remove all ships");
                SandBoxServerWorld.removeAllShip(ctx.getSender().serverLevel());
            }));*/
        },
        null, null
    ),
    TestKey3(
        "Test Key3",
        GLFW.GLFW_KEY_I,
        true,
        (kb, keyDown) -> { if (!keyDown) return;
            Player player = Minecraft.getInstance().player;
            EzDebug.log("try create ship");
            //NetworkHandler.sendToServer(new CreateShipAtPlayerFromClientPacketC2S(9));
            BlockClusterData blockData = new BlockClusterData();
            blockData.setBlock(new Vector3i(0, 0, 0), Blocks.IRON_BLOCK.defaultBlockState());

            SandBoxClientWorld.INSTANCE.addClientShip(new SandBoxClientShip(
                UUID.randomUUID(),
                new RigidbodyData(new TransformPrimitive(JomlUtil.d(player.position()), new Quaterniond(), new Vector3d(1, 1, 1))),
                blockData
            ));
        },
        null, null
    );


    private static Hashtable<String, KeyBinding> name2key = new Hashtable<>();

    private final String desc;
    private final int key;
    private final boolean modifiable;
    private KeyMapping keybind;

    private BiConsumer<KeyBinding, Boolean> keyChangeState;  //arg1 is key, arg2 is keyState
    private BiConsumer<KeyBinding, Integer> keyPressingEvt;  //arg1 is key, arg2 is the tick count since the key is pressed
    private BiConsumer<KeyBinding, Integer> keyReleaseEvt;

    KeyBinding(String inDesc, int defaultKey, boolean inModifialbe, BiConsumer<KeyBinding, Boolean> inOnKeyEvt, BiConsumer<KeyBinding, Integer> inPressingEvt, BiConsumer<KeyBinding, Integer> inKeyReleaseEvt) {
        desc = inDesc;
        key = defaultKey;
        modifiable = inModifialbe;
        keyChangeState = inOnKeyEvt;
        keyPressingEvt = inPressingEvt;
        keyReleaseEvt = inKeyReleaseEvt;
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        for (KeyBinding key : values()) {
            key.keybind = new KeyMapping(key.desc, key.key, VsWap.MODID); //todo mod name
            if (!key.modifiable)
                continue;

            event.register(key.keybind);
        }
    }


    public boolean isPressing() {
        //if (!modifiable)
        //    return isKeyDown(key);
        return keybind.isDown();
    }

    public int getKey() { return keybind.getKey().getValue(); }
    public void invokeOnKeyEvent(boolean pressing) {
        if (keyChangeState != null)
            keyChangeState.accept(this, pressing);
    }
    public void invokePressingEvent(int tickCnt) {
        if (keyPressingEvt != null)
            keyPressingEvt.accept(this, tickCnt);
    }
    public void invokeKeyReleaseEvent(int tickCnt) {
        if (keyReleaseEvt != null)
            keyReleaseEvt.accept(this, tickCnt);
    }

    /*
    public boolean isUp(InputEvent.Key event) {
        EzDebug.Log("current key:" + event.getKey() + ", boundKey:" + keybind.getKey().getValue());

        return
                event.getKey() == keybind.getKey().getValue() &&
                        event.getAction() == 0;
    }
    public boolean isUp(InputEvent.MouseButton event) {
        EzDebug.Log("current key:" + event.getButton() + ", boundKey:" + keybind.getKey().getValue());

        return
                event.getButton() == keybind.getKey().getValue() &&
                        event.getAction() == 0;
    }*/

    /*private static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }*/
}
