package com.lancas.vswap.content.block.blocks.cartridge.propellant.empty;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;

import java.util.List;

public class EmptyGauge12 extends BlockPlus implements IEmptyPropellant {
    public EmptyGauge12(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            EmptyShelledPropellant.class,
            () -> List.of(new DefaultCartridgeAdder())
        );
    }
}
