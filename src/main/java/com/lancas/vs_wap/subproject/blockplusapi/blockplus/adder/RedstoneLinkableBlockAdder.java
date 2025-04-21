package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.world.level.block.state.BlockState;

public class RedstoneLinkableBlockAdder implements IBlockAdder {
    public boolean provideRedstoneSrcVerification(BlockState state) { return true; }
}
