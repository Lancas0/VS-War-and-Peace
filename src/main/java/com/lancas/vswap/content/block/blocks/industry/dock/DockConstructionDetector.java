package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.*;
import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class DockConstructionDetector extends BlockPlus implements IBE<DockConstructionDetectorBe> {
    public static BlockState powered(BlockState state, boolean powered) {
        return state.setValue(BlockStateProperties.POWERED, powered);
    }

    public DockConstructionDetector(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() { return BlockPlus.addersIfAbsent(DockConstructionDetector.class, () -> List.of(
        //new PropertyAdder<>(BlockStateProperties.POWERED, false),
        new PropertyAdder<>(Dock.CONNECT_N, false),
        new PropertyAdder<>(Dock.CONNECT_S, false),
        new PropertyAdder<>(Dock.CONNECT_W, false),
        new PropertyAdder<>(Dock.CONNECT_E, false),
        new ShapeByStateAdder(state -> Shapes.block()),
        Dock.DockBlockAdder,
        new ToggleLikeBlockAdder(),
        new RedstoneLinkableBlockAdder()
    )); }


    @Override
    public Class<DockConstructionDetectorBe> getBlockEntityClass() { return DockConstructionDetectorBe.class; }
    @Override
    public BlockEntityType<? extends DockConstructionDetectorBe> getBlockEntityType() { return WapBlockEntites.DOCK_CONSTRUCTION_DETECTOR_BE.get(); }

    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return (level, blockPos, blockState, s) -> ((DockConstructionDetectorBe)s).tick();
    }
}
