package com.lancas.vs_wap.content.block.blockentity;

import com.lancas.vs_wap.util.NbtBuilder;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ProjectorLenBe extends SyncedBlockEntity {
    public ProjectorLenBe(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        scale.chase(1, 0.5, LerpedFloat.Chaser.EXP);
    }

    public static final double SCALE_TO_RAD = 3.1415926 / 2.0;
    public static final double SCALE_STEP = 0.05;
    public static final double MIN_SCALE = 0.05;
    public LerpedFloat scale = LerpedFloat.linear().startWithValue(1);


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        NbtBuilder.modify(tag)
            //.putFloat("val", scale.getValue())
            .putFloat("target", scale.getChaseTarget());
    }
    @Override
    public void load(@NotNull CompoundTag tag) {
        NbtBuilder.modify(tag)
            //.readFloatDo("val", v -> scale.setValue(v))
            .readFloatDo("target", v -> scale.updateChaseTarget(v));
    }

    public void setScaleTarget(double target) {
        scale.updateChaseTarget((float)target);
        this.notifyUpdate();
    }
    public void stepScale(boolean increase) {
        if (increase) {
            setScaleTarget(scale.getChaseTarget() + SCALE_STEP);
        } else {
            setScaleTarget(Math.max(scale.getChaseTarget() - SCALE_STEP, MIN_SCALE));
        }
    }


    public void tick() {
        scale.tickChaser();
        //EzDebug.warn("chased:" + scale.getValue());

        /*if (level.getBlockEntity(worldPosition.below()) instanceof VSProjectorBE projectorBE) {
            projectorBE.scale = scale.getValue();
        }*/
    }
}
