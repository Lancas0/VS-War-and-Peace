package com.lancas.vswap.content.block.blocks.artillery;

import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.util.ShapeBuilder;

import java.util.List;

public class ArtilleryBarrelBlock extends BlockPlus implements IBarrel {
    private final static List<IBlockAdder> providers = List.of(
        new DirectionAdder(false, true, ShapeBuilder.cubicRing(0, 0, 0, 2, 16))
    );
    @Override
    public List<IBlockAdder> getAdders() { return providers; }

    public ArtilleryBarrelBlock(Properties p_49795_) {
        super(p_49795_);
    }
}
