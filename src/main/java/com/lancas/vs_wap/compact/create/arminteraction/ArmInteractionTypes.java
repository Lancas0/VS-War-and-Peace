package com.lancas.vs_wap.compact.create.arminteraction;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vs_wap.content.block.blocks.cartridge.ShellFrame;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ArmInteractionTypes {
    public static final ArmInteractionTypes.BreechArmType BREECH = register("breech_arm_type", ArmInteractionTypes.BreechArmType::new);
    public static final ArmInteractionTypes.ShellFrameArmType SHELL_FRAME = register("shell_frame_arm_type", ArmInteractionTypes.ShellFrameArmType::new);

    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(ModMain.getResLocation(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static class BreechArmType extends ArmInteractionPointType {
        public BreechArmType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return level.getBlockState(pos).getBlock() instanceof IBreech;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new BreechArmPoint(this, level, pos, state);
        }
    }
    public static class ShellFrameArmType extends ArmInteractionPointType {
        public ShellFrameArmType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return level.getBlockState(pos).getBlock() instanceof ShellFrame;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ShellFrameArmPoint(this, level, pos, state);
        }
    }

    public static void register() {
    }
}
