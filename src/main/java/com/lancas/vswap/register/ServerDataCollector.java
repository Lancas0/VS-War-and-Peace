package com.lancas.vswap.register;

import com.lancas.vswap.VsWap;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

//todo use accessor
@Mod.EventBusSubscriber(modid = VsWap.MODID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerDataCollector {
    public static PlayerList playerList;
}
