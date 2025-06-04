package com.lancas.vswap.content.block.blocks.cartridge.glider;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class GliderWing extends BlockPlus {
    public GliderWing(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(GliderWing.class, () -> List.of(
            new DefaultCartridgeAdder()
            //, new ShapeByStateAdder(s -> Shapes.block())
        ));
    }
}
