package com.lancas.vs_wap.content.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ActivatorBlockEntity extends BlockEntity {
    protected static final int DELAYED_TICKS = 4;
    protected static final int ACTIVATOR_TICKS = 10;
    public ActivatorBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    //do not save so can retrigger every times replaced(for example use docker) todo is it a good solution?
    public int remainDelayTicks = DELAYED_TICKS;
    public int remainTicks = ACTIVATOR_TICKS;
    protected boolean activating = false;
    protected boolean stop = false;


    // 数据保存与读取
    /*
        protected void saveAdditional(CompoundTag tag)
        public void load(CompoundTag tag)
        not override save & load because the state should not be saved. (copy an unactivated activator and will past an activated activator)
    */


    public int getRedstone() {
        return activating ? 15 : 0;
    }

    public void tick() {
        //only tick in client side
        if (this.level == null || level.isClientSide) return;
        if (stop) return;

        if (!activating) {
            if (--remainDelayTicks < 0) {
                activating = true;
                setChanged();
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
        } else {  //activating
            if (--remainTicks < 0) {
                activating = false;
                setChanged();
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                stop = true;
                return;
            }
        }

    }
}
