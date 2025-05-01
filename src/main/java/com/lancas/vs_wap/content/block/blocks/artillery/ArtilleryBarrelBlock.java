package com.lancas.vs_wap.content.block.blocks.artillery;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.util.ShapeBuilder;

import java.util.List;

public class ArtilleryBarrelBlock extends BlockPlus implements IBarrel {
    private final static List<IBlockAdder> providers = List.of(
        new DirectionAdder(false, true, ShapeBuilder.cubicRing(0, 0, 0, 2, 16))
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return providers; }

    public ArtilleryBarrelBlock(Properties p_49795_) {
        super(p_49795_);
    }
}
