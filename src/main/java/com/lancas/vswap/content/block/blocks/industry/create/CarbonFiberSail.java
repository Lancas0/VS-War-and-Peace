package com.lancas.vswap.content.block.blocks.industry.create;

import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.util.ShapeBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.bearing.SailBlock;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class CarbonFiberSail extends SailBlock implements IPowerfulSailBlock {
    //private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());
    //public static int SAIL_POWER_ADDITION = 3;

    /*public static CarbonFiberSail CARBON_FIBER_SAIL = new CarbonFiberSail()

    protected CarbonFiberSail(Properties properties, boolean frame, DyeColor color) {
        super(properties, frame, color);
    }*/
    public CarbonFiberSail(Properties properties) {
        super(properties, false, null);
    }

    @Override
    public int getSailPower() {
        return 4;
    }



    /*@Override
    public List<IBlockAdder> getAdders() { return BlockPlus.addersIfAbsent(CarbonFiberSail.class, () -> List.of(
        new DirectionAdder(
            true, false,
            ShapeBuilder.ofBoxPixel(0, 5, 0, 16, 9, 16).get()
        )
    ));}*/
    /*private final ShapeBuilder shapeBuilder = ShapeBuilder.ofBoxPixel(0, 5, 0, 16, 9, 16);
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return shapeBuilder.getRotated(state.getValue(FACING));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(FACING, (state.getValue(FACING)).getOpposite());
    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
        if (!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(heldItem)) {
            placementHelper.getOffset(player, world, state, pos, ray).placeInWorld(world, (BlockItem)heldItem.getItem(), player, hand, ray);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static class PlacementHelper implements IPlacementHelper {
        private PlacementHelper() { }

        public Predicate<ItemStack> getItemPredicate() {
            return (i) -> AllBlocks.SAIL.isIn(i) || AllBlocks.SAIL_FRAME.isIn(i) || WapBlocks.Industrial.Create.CARBON_FIBER_SAIL.isIn(i);
        }

        public Predicate<BlockState> getStatePredicate() {
            return (s) -> s.getBlock() instanceof SailBlock || s.getBlock() instanceof CarbonFiberSail;
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(
                pos,
                ray.getLocation(),
                (state.getValue(SailBlock.FACING)).getAxis(),
                (dir) -> world.getBlockState(pos.relative(dir)).canBeReplaced()
            );
            return directions.isEmpty() ?
                PlacementOffset.fail() :
                PlacementOffset.success(
                    pos.relative(directions.get(0)),
                    (s) -> s.setValue(DirectionalBlock.FACING, state.getValue(DirectionalBlock.FACING))
                );
        }
    }*/
}
