package com.lancas.vswap.compact.create.arminteraction;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vswap.content.block.blocks.artillery.breech.IBreechBe;
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
    //public static final ArmInteractionTypes.ShellFrameArmType SHELL_FRAME = register("shell_frame_arm_type", ArmInteractionTypes.ShellFrameArmType::new);
    //public static final ArmInteractionTypes.UnderConstructionArmType UNDER_CONSTRUCTION = register("under_construction_arm_type", ArmInteractionTypes.UnderConstructionArmType::new);
    public static final DockArmPoint.Type DOCK = register("dock_arm_type", DockArmPoint.Type::new);

    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(VsWap.asRes(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static class BreechArmType extends ArmInteractionPointType {
        public BreechArmType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return level.getBlockEntity(pos) instanceof IBreechBe;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new BreechArmPoint(this, level, pos, state);
        }
    }
    /*public static class ShellFrameArmType extends ArmInteractionPointType {
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
    }*/
    /*public static class UnderConstructionArmType extends ArmInteractionPointType {
        public UnderConstructionArmType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return level.getBlockState(pos).getBlock() instanceof UnderConstruction;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new UnderConstructionArmPoint(this, level, pos, state);
        }
    }*/

    public static void register() {
    }
}
