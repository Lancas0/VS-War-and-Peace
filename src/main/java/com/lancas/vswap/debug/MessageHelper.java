package com.lancas.vswap.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class MessageHelper {
    public static void sendMessage(@NotNull Player player, String msg) {
        player.sendSystemMessage(Component.literal(msg));
    }
}
