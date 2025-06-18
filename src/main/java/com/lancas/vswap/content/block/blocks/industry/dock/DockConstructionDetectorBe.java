package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.saved.vs_constraint.ConstraintSmartHolder;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.foundation.handler.construct.ShipConstructHandler;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DockConstructionDetectorBe extends DockBe {
    public DockConstructionDetectorBe(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    protected boolean powered = false;
    //protected LazyTicks lazy = new LazyTicks(10);
    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide)
            return;

        ShipConstructHandler handler = getControllerBE().constructHandler;
        if (powered) {  //powered to unpowered
            if (handler == null || !handler.isCompleted()) {
                BlockState prev = level.getBlockState(worldPosition);
                level.setBlockAndUpdate(worldPosition, DockConstructionDetector.powered(prev, false));
                powered = false;
                setChanged();
            }
        } else {  //unpowered to powered
            if (handler != null && handler.isCompleted()) {
                BlockState prev = level.getBlockState(worldPosition);
                level.setBlockAndUpdate(worldPosition, DockConstructionDetector.powered(prev, true));
                powered = true;
                setChanged();
            }
        }
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        NbtBuilder.modify(tag)
            .putBoolean("powered", powered);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        powered = tag.getBoolean("powered");
    }
}