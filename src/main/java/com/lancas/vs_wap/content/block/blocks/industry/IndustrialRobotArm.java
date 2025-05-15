package com.lancas.vs_wap.content.block.blocks.industry;

import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.block.blockentity.IndustrialRobotArmBe;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.blockitem.BlockItemHoverTextAdder;
import com.lancas.vs_wap.util.ShapeBuilder;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IndustrialRobotArm extends BlockPlus /*implements IBE<ArmBlockEntity>,  ICogWheel*/ {
    private static final DirectionAdder directionAdder = new DirectionAdder(false, true, Shapes.block());

    public IndustrialRobotArm(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(IndustrialRobotArm.class, () -> List.of(
            directionAdder,
            new BlockItemHoverTextAdder() {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag) {
                    texts.add(Component.literal("test test test"));
                }
            }
        ));
    }


    /*@Override
    public boolean hasShaftTowards(LevelReader levelReader, BlockPos blockPos, BlockState blockState, Direction direction) {
        return false;
    }
    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) { return Direction.Axis.Y; }


    @Override
    public Class<ArmBlockEntity> getBlockEntityClass() { return ArmBlockEntity.class; }
    @Override
    public BlockEntityType<? extends ArmBlockEntity> getBlockEntityType() { return WapBlockEntites.INDUSTRIAL_ROBOT_ARM_BE.get(); }*/
}
