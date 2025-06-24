package com.lancas.vswap.content.block.blocks.cartridge.primer;

import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.util.ShapeBuilder;

import java.util.List;

public class EmptyPrimer extends BlockPlus {
    public static final List<IBlockAdder> adders = List.of(
        new DirectionAdder(
            false,
            true,
            ShapeBuilder.ofBoxPixel(2, 5, 2, 14, 11, 14)
                .append(box(3, 11, 3, 13, 16, 13))
                .get()
        )
    );

    public EmptyPrimer(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() { return adders; }
}
