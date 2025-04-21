package com.lancas.vs_wap.content.blocks.cartridge.propellant.empty;

import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;

import java.util.List;

public class EmptyShelledPropellant extends BlockPlus implements IEmptyPropellant {
    public EmptyShelledPropellant(Properties p_49795_) {
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
