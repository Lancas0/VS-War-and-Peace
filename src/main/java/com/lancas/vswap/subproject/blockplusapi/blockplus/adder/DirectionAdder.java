package com.lancas.vswap.subproject.blockplusapi.blockplus.adder;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.ShapeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class DirectionAdder extends AbstractPropertyAdder<Direction> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    @Nullable
    public static Direction getDirection(BlockState state) {
        if (!state.hasProperty(FACING))
            return null;
        return state.getValue(FACING);
    }
    public static DirectionAdder Horizon(boolean inDirOppositeToLook, boolean inOppositeWhenShift, @Nullable VoxelShape inUpShape) {
        return new DirectionAdder(inDirOppositeToLook, inOppositeWhenShift, inUpShape) {
            @Override
            public Direction getDirectionForPlacement(BlockPlaceContext ctx) {
                Direction nearestHorizonDir = Arrays.stream(ctx.getNearestLookingDirections())
                     .filter(x -> x.getAxis() != Direction.Axis.Y)
                     .findFirst()
                     .orElseGet(() -> {
                         EzDebug.warn("fail to get nearest horizion dir, will set default dir SOUTH");
                         return Direction.SOUTH;
                     });

                Direction placeDir = dirOppositeToLook ? nearestHorizonDir.getOpposite() : nearestHorizonDir;

                if (oppositeWhenShift && ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
                    placeDir = placeDir.getOpposite();
                }
                return placeDir;
            }
        };
    }
    /*public static VoxelShape getShape(BlockState state) {
        return state.getBlock().getShape(state, null, null, null);
    }*/

    protected final boolean dirOppositeToLook;
    protected final boolean oppositeWhenShift;
    protected final VoxelShape upShape;
    public DirectionAdder(boolean inDirOppositeToLook, boolean inOppositeWhenShift, @Nullable VoxelShape inUpShape) {
        dirOppositeToLook = inDirOppositeToLook;
        oppositeWhenShift = inOppositeWhenShift;
        upShape = inUpShape;
    }

    @Override
    public Property<Direction> getProperty() { return FACING; }
    @Override
    public Direction getDefaultValue() { return Direction.UP; }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx, BlockState dest) {
        return dest.setValue(FACING, getDirectionForPlacement(ctx));
    }

    public Direction getDirectionForPlacement(BlockPlaceContext ctx) {
        Direction placeDir = dirOppositeToLook ? ctx.getNearestLookingDirection().getOpposite() : ctx.getNearestLookingDirection();

        if (oppositeWhenShift && ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
            placeDir = placeDir.getOpposite();
        }
        return placeDir;
    }

    @Override
    public VoxelShape appendShape(BlockState state) {
        if (upShape == null || upShape.isEmpty()) return Shapes.empty();

        Direction dir = state.getValue(FACING);
        return new ShapeBuilder(upShape).getRotated(dir);
    }
}
