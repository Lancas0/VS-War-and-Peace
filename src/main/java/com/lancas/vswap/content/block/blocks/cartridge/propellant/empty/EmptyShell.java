package com.lancas.vswap.content.block.blocks.cartridge.propellant.empty;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;

import java.util.List;

public class EmptyShell extends BlockPlus implements IEmptyPropellant {
    public EmptyShell(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            EmptyShell.class,

            () -> List.of(new DefaultCartridgeAdder(true))
        );
    }
}
