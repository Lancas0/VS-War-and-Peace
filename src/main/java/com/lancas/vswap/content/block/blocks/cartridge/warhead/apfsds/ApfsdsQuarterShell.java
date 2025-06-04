package com.lancas.vswap.content.block.blocks.cartridge.warhead.apfsds;

import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class ApfsdsQuarterShell extends BlockPlus {

    public ApfsdsQuarterShell(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(ApfsdsQuarterShell.class, () -> List.of(
            /*new DirectionAdder(true, true,
                Shapes.box(0.1875, 0.1875, 0.1875, 0.5, 0.5, 0.5)
            )*/
            new DirectionAdder(true, true, Shapes.block())
        ));
    }
}
