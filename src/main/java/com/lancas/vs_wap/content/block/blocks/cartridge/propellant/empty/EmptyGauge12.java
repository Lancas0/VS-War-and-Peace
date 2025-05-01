package com.lancas.vs_wap.content.block.blocks.cartridge.propellant.empty;

import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;

import java.util.List;

public class EmptyGauge12 extends BlockPlus implements IEmptyPropellant {
    public EmptyGauge12(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            EmptyShelledPropellant.class,
            () -> List.of(new DefaultCartridgeAdder())
        );
    }
}
