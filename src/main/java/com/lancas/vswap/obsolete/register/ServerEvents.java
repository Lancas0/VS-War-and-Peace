package com.lancas.vswap.obsolete.register;

import com.lancas.vswap.ModMain;
import com.lancas.vswap.content.block.blocks.explosive.AbstractExplosiveBlock;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.IWarheadBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    //不知道为什么，但是和瓦尔基里物理结构兼容，真奇怪
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getLevel().isClientSide) return;

        ServerLevel level = (ServerLevel)event.getLevel();
        Explosion explosion = event.getExplosion();

        for (BlockPos pos : event.getAffectedBlocks()) {
            BlockState state = level.getBlockState(pos);

            //warhead block
            if (state.getBlock() instanceof IWarheadBlock warhead) {
                warhead.onDestroyByExplosion(level, pos, state, explosion);
            }

            //explosive block
            if (state.getBlock() instanceof AbstractExplosiveBlock explosiveBlock) {
                explosiveBlock.explodeByExplosion(level, pos, state);
            }
        }
    }
}
