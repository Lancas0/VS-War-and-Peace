package com.lancas.vs_wap.subproject.blockplusapi.register;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Hashtable;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockInteractEvent {
    private static Hashtable<Class<? extends BlockPlus>, QuadConsumer<Level, Player, BlockPos, BlockState>>
        interactableBlocks = new Hashtable<>();

    public static void addInteractableBlock(BlockPlus block, QuadConsumer<Level, Player, BlockPos, BlockState> event) {
        if (event != null)
            interactableBlocks.put(block.getClass(), event);
    }

    @SubscribeEvent
    public static void onBlockInteracted(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Player player = event.getEntity();

        if (!(state.getBlock() instanceof BlockPlus blockPlus)) return;

        var interact = interactableBlocks.get(blockPlus.getClass());
        if (interact != null && level != null && pos != null && state != null && player != null) {
            interact.apply(level, player, pos, state);
        }
    }
}
