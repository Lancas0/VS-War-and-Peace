package com.lancas.vs_wap.content.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectBlockEntity extends BlockEntity {
    public ProjectBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    private BlockState representBlock = Blocks.AIR.defaultBlockState();

    public void setRepresentBlock(BlockState state) {
        this.representBlock = state;
        setChanged();
    }
    public BlockState getRepresentBlock() { return representBlock; }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        representBlock = NbtUtils.readBlockState(
            this.level.holderLookup(Registries.BLOCK),
            tag.getCompound("represent_block")
        );
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("represent_block", NbtUtils.writeBlockState(representBlock));
    }

}
