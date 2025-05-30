package com.lancas.vswap.content.block.blocks.blockplus;

import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class BlockInfoAdder<T> implements IBlockAdder {
    private final WapBlockInfos.BlockInfo<T> info;
    private final String blockID;
    private final Function<BlockState, T> getter;

    public BlockInfoAdder(String inBlockID, WapBlockInfos.BlockInfo<T> inInfo, Function<BlockState, T> inGetter) {
        info = inInfo;
        blockID = inBlockID;
        getter = inGetter;
    }

    public void onInit(BlockPlus thisBlock) {
        info.addBlock(blockID, getter);
    }
}
