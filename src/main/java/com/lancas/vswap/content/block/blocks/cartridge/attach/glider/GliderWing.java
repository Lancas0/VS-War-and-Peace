package com.lancas.vswap.content.block.blocks.cartridge.attach.glider;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;

import java.util.List;

public class GliderWing extends BlockPlus {
    public GliderWing(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(GliderWing.class, () -> List.of(
            new DefaultCartridgeAdder(true)
            //, new ShapeByStateAdder(s -> Shapes.block())
        ));
    }
}
