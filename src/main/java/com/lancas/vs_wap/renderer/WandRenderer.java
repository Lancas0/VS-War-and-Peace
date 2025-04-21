package com.lancas.vs_wap.renderer;

import com.lancas.vs_wap.util.JomlUtil;
import com.simibubi.create.CreateClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WandRenderer {
    public static void drawOutline(BlockPos selection, Direction dir, int color, String slot) {
        Level world = Minecraft.getInstance().level;
        if (selection == null)
            return;
        if(world == null)return;

        BlockState state = world.getBlockState(selection);
        VoxelShape shape = state.getShape(world, selection);
        AABB boundingBox = shape.isEmpty() ? new AABB(BlockPos.ZERO) : shape.bounds();
        AABB worldBounds = boundingBox.move(selection);

        CreateClient.OUTLINER.showAABB(slot + "_pos", worldBounds)
            .colored(color)
            .lineWidth(1 / 32f);

        AABB face = JomlUtil.boundsFace(worldBounds, dir);
        CreateClient.OUTLINER.showAABB(slot + "_dir", face)
            .lineWidth(1 / 16f);
    }
}
