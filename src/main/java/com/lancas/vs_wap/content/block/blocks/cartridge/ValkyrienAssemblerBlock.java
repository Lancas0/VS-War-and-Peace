package com.lancas.vs_wap.content.block.blocks.cartridge;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.saved.BlockRecordRWMgr;
import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class ValkyrienAssemblerBlock extends BlockPlus {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AssemblerRecord implements IBlockRecord {
        public String test = "123";

        @Override
        public String toString() {
            return "AssemblerData{" +
                "test='" + test + '\'' +
                '}';
        }
    }


    public ValkyrienAssemblerBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            ValkyrienAssemblerBlock.class,
            () -> List.of(
                new DirectionAdder(true, true, Shapes.block()),
                new InteractableBlockAdder() {
                    @Override
                    public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;

                        AssemblerRecord prevData = BlockRecordRWMgr.getRecord(sLevel, pos);
                        EzDebug.log("prevData:" + prevData);

                        BlockRecordRWMgr.putRecord(sLevel, pos, new AssemblerRecord());
                        EzDebug.log("add data");
                        return InteractionResult.PASS;
                    }
                }
            )
        );
    }




}
