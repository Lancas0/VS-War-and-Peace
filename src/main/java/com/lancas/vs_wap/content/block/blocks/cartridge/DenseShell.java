package com.lancas.vs_wap.content.block.blocks.cartridge;

import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;

import java.util.List;

public class DenseShell extends BlockPlus {
    private static List<IBlockAdder> adders = List.of(
        new DefaultCartridgeAdder()
        //, EinherjarBlockInfos.mass.getOrCreateExplicit(DenseShell.class, state -> 180.0)
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return adders; }



    public DenseShell(Properties p_49795_) {
        super(p_49795_);
    }
}
