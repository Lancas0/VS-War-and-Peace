package com.lancas.vswap.register;


import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.util.WorldUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.ServerShip;

@Mod.EventBusSubscriber
public class ReleaseRefShipEvt {
    //todo pay notice that if there will be unreleased ship

}