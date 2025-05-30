package com.lancas.vswap.foundation.data;

import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.sandbox.api.ISavedObject;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public record BlockPosAndState(Dest<BlockPos> bp, Dest<BlockState> state) implements ISavedObject<BlockPosAndState> {
    public BlockPosAndState(BlockPos inBp, BlockState inState) {
        this(new Dest<>(inBp), new Dest<>(inState));
    }
    public BlockPosAndState(CompoundTag tag) {
        this(new Dest<>(), new Dest<>());
        load(tag);
    }

    public BlockPos getBp() { return bp.get(); }
    public BlockState getState() { return state.get(); }

    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putBlockPos("bp", bp.get())
            .putBlockState("state", state.get())
            .get();
    }
    @Override
    public BlockPosAndState load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readBlockPosDo("bp", bp::set)
            .readBlockStateDo("state", state::set);
        return this;
    }
}
