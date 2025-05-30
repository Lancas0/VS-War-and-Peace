package com.lancas.vswap.content.block.blocks.industry;
/*
import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.block.blockentity.UnderConstructionBe;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockRemoveCallbackAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class UnderConstruction extends BlockPlus implements IBE<UnderConstructionBe> {
    public UnderConstruction(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(UnderConstruction.class, () -> List.of(
            new ShapeByStateAdder(state -> Shapes.block()),
            (IBlockRemoveCallbackAdder) () -> new Action<BlockChangeContext, Void>() {
                    @Override
                    public Void pre(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
                        if (!(ctx.level instanceof ServerLevel level)) return null;
                        if (!(level.getBlockEntity(ctx.pos) instanceof UnderConstructionBe be)) {
                            EzDebug.warn("UnderConstruction Block can't get BlockEntity on remove!");
                            return null;
                        }
                        be.onRemove(level);

                        return null;
                    }
                }
        ));
    }


    @Override
    public Class<UnderConstructionBe> getBlockEntityClass() { return UnderConstructionBe.class; }
    @Override
    public BlockEntityType<? extends UnderConstructionBe> getBlockEntityType() { return WapBlockEntites.UNDER_CONSTRUCTION_BE.get(); }
    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return (l, bp, state, be) -> {
            ((UnderConstructionBe)be).tick();
        };
    }
}
*/