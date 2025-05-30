package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.WapCT;
import com.lancas.vswap.debug.EzDebug;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DockCTBehaviour extends ConnectedTextureBehaviour.Base {

    @Override
    public @Nullable CTSpriteShiftEntry getShift(BlockState blockState, Direction direction, @Nullable TextureAtlasSprite textureAtlasSprite) {
        /*if (direction == Direction.UP)
            return WapCT.DOCK_TOP;
        return null;*/
        return WapCT.DOCK_TOP;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        if (reader.getBlockEntity(pos) instanceof DockBe selfBe && reader.getBlockEntity(otherPos) instanceof DockBe otherBe) {
            boolean connect = selfBe.getController().equals(otherBe.getController());
            return connect;
        }
        return false;
    }
}
