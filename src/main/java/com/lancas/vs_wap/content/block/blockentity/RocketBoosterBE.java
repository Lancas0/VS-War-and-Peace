package com.lancas.vs_wap.content.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RocketBoosterBE extends BlockEntity {
    public enum BoosterTickState {
        Ready,
        Boost,
        Ended
    }

    public RocketBoosterBE(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    private int remainReadyTick = 5;
    private int remainBoostTicks = 10;

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("remain_ready_tick", remainReadyTick);
        tag.putInt("remain_boost_tick", remainBoostTicks);
    }
    @Override
    public void load(CompoundTag tag) {
        remainReadyTick = tag.getInt("remain_ready_tick");
        remainBoostTicks = tag.getInt("remain_boost_tick");
    }

    public BoosterTickState tickNext() {
        if (remainReadyTick > 0) {
            remainReadyTick--;
            return BoosterTickState.Ready;
        }

        if (remainBoostTicks > 0) {
            remainBoostTicks--;
            //EzDebug.warn("boost");
            return BoosterTickState.Boost;
        }

        return BoosterTickState.Ended;
    }
}
