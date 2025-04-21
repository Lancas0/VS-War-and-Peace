package com.lancas.vs_wap.content.blockentity;

import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DelayedExplosiveBlockEntity extends BlockEntity {
    public static final String ID = "delayed_explosive_be";

    public boolean countingDown = false;
    public int delayedTicks = 0;
    public int explosionPower = 0;

    public DelayedExplosiveBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    // 数据保存与读取
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean("CountingDown", countingDown);
        tag.putInt("DelayedTicks", delayedTicks);
        tag.putInt("ExplosionPower", explosionPower);
    }
    @Override
    public void load(CompoundTag tag) {
        countingDown = tag.getBoolean("CountingDown");
        delayedTicks = tag.getInt("DelayedTicks");
        explosionPower = tag.getInt("ExplosionPower");
    }

    public void startCountingDown(int inDelayedTicks, int inExplosionPower) {
        countingDown = true;
        delayedTicks = inDelayedTicks;
        explosionPower = inExplosionPower;
        setChanged();
    }
    public void tick() {
        if (level == null || level.isClientSide) return;
        if (!countingDown) return;

        if (delayedTicks <= 0) explode();
        EzDebug.log("CountingDown:" + delayedTicks);

        delayedTicks--;
    }
    private void explode() {
        if (level == null || level.isClientSide) return;

        level.removeBlock(worldPosition, false);
        level.explode(null,
            worldPosition.getX() + 0.5,
            worldPosition.getY() + 0.5,
            worldPosition.getZ() + 0.5,
            explosionPower,
            Level.ExplosionInteraction.BLOCK
        );
    }
}
