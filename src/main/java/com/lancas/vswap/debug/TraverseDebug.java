package com.lancas.vswap.debug;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.ship.ballistics.collision.traverse.BlockTraverser;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.CreateClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mod.EventBusSubscriber(modid = VsWap.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TraverseDebug {
    @SubscribeEvent
    public static void renderTraverseBlocks(TickEvent.ClientTickEvent event) {
        if (true) return;

        Player player = Minecraft.getInstance().player;
        Level level = Minecraft.getInstance().level;
        if (player == null || level == null) return;

        BlockPos start = BlockPos.containing(player.getEyePosition());
        Vec3 dir = player.getLookAngle();

        var traverseBlocks = BlockTraverser.VanillaMissOnEmpty.traverseAllIncludeShip(
            level,
            new ClipContext(
                start.getCenter(),
                start.getCenter().add(dir.scale(20)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            ),
            null
        );

        EzDebug.light("traverse blocks count:" + traverseBlocks.size() + ", key size:" + traverseBlocks.keySet().size());

        int i = 0;
        for (var cur : traverseBlocks.keySet()) {
            BlockPos curBp = cur;//hit.getBlockPos();

            if (!VSGameUtilsKt.isBlockInShipyard(level, cur)) continue;

            //EzDebug.light("debuging at " + i + " : " + StrUtil.getBlockPos(curBp));

            CreateClient.OUTLINER.showAABB("debug_traverse_" + i, JomlUtil.centerExtended(curBp, 0.5))
                .colored(i * 100)
                .lineWidth(1 / 16f);
            /*CreateClient.OUTLINER.showLine("debug_traverse_" + i + "_line", curBp.getCenter(), curBp.getCenter().add(new Vec3(0, 25, 0)))
                .colored(i * 100)
                .lineWidth(1 / 16f);*/

            i++;
        }
    }
}