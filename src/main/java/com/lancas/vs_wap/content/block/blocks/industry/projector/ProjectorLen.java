package com.lancas.vs_wap.content.block.blocks.industry.projector;


import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.block.blockentity.ProjectorLenBe;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import com.lancas.vs_wap.util.ShapeBuilder;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class ProjectorLen extends BlockPlus implements IBE<ProjectorLenBe> {
    public ProjectorLen(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(ProjectorLen.class, () -> List.of(
            new ShapeByStateAdder(state -> ShapeBuilder.ofBox(0, 0, 0, 16, 5, 16).get()),
            new InteractableBlockAdder() {
                @Override
                public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    //todo maybe simply do both side and don't use syncedBlockEntity
                    if (!level.isClientSide && level.getBlockEntity(pos) instanceof ProjectorLenBe be) {
                        boolean increase = !player.isShiftKeyDown();
                        be.stepScale(increase);
                        EzDebug.warn("new scale:" + be.scale.getValue());
                    }

                    return InteractionResult.PASS;
                }
            }
        ));
    }


    @Override
    public Class<ProjectorLenBe> getBlockEntityClass() { return ProjectorLenBe.class; }
    @Override
    public BlockEntityType<? extends ProjectorLenBe> getBlockEntityType() { return WapBlockEntites.PROJECTOR_LEN_BE.get(); }
    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> type) {
        return (l, bp, bState, be) -> ((ProjectorLenBe)be).tick();
    }
}
