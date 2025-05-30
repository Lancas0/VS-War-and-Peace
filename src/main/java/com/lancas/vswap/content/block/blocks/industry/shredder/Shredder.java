package com.lancas.vswap.content.block.blocks.industry.shredder;

import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Predicate;


public class Shredder extends DirectionalAxisKineticBlock implements IBE<ShredderBe> {
    private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public Shredder(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_14PX.get(state.getValue(FACING));
    }

    //public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");
    //private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());


    /*@Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        //super.createBlockStateDefinition(builder.add(new Property[]{ FLIPPED }));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        Direction facing = stateForPlacement.getValue(FACING);
        return (BlockState) stateForPlacement.setValue(FLIPPED, Boolean.valueOf(facing.getAxis() == Direction.Axis.Y && context.getHorizontalDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE));
    }*/

    /*@Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        BlockState newState = super.getRotatedBlockState(originalState, targetedFace);
        if (newState.getValue(FACING).getAxis() != Direction.Axis.Y) {
            return newState;
        }
        if (targetedFace.getAxis() != Direction.Axis.Y) {
            return newState;
        }
        if (!((Boolean) originalState.getValue(AXIS_ALONG_FIRST_COORDINATE)).booleanValue()) {
            newState = (BlockState) newState.cycle(FLIPPED);
        }
        return newState;
    }*/

    /*@Override
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockState newState = super.rotate(state, rot);
        if (state.getValue(FACING).getAxis() != Direction.Axis.Y) {
            return newState;
        }
        if (rot.ordinal() % 2 == 1) {
            if ((rot == Rotation.CLOCKWISE_90) != ((Boolean) state.getValue(AXIS_ALONG_FIRST_COORDINATE)).booleanValue()) {
                newState = (BlockState) newState.cycle(FLIPPED);
            }
        }
        if (rot == Rotation.CLOCKWISE_180) {
            newState = (BlockState) newState.cycle(FLIPPED);
        }
        return newState;
    }*/

    /*@Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        BlockState newState = super.mirror(state, mirrorIn);
        if (state.getValue(FACING).getAxis() != Direction.Axis.Y) {
            return newState;
        }
        boolean alongX = ((Boolean) state.getValue(AXIS_ALONG_FIRST_COORDINATE)).booleanValue();
        if (alongX && mirrorIn == Mirror.FRONT_BACK) {
            newState = (BlockState) newState.cycle(FLIPPED);
        }
        if (!alongX && mirrorIn == Mirror.LEFT_RIGHT) {
            newState = (BlockState) newState.cycle(FLIPPED);
        }
        return newState;
    }*/

    /*@Override
    //hurt entity in
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if ((entityIn instanceof ItemEntity) || !new AABB(pos).deflate(0.10000000149011612d).intersects(entityIn.getBoundingBox())) {
            return;
        }
        withBlockEntityDo(worldIn, pos, be -> {
            if (be.getSpeed() == 0.0f) {
                return;
            }
            entityIn.hurt(CreateDamageSources.saw(worldIn), (float) DrillBlock.getDamage(be.getSpeed()));
        });
    }*/
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
        if (!player.isShiftKeyDown() &&
            player.mayBuild() &&
            placementHelper.matchesItem(heldItem) &&
            placementHelper.getOffset(player, worldIn, state, pos, hit).placeInWorld(worldIn, (BlockItem) heldItem.getItem(), player, handIn, hit).consumesAction()
        ) {
            return InteractionResult.SUCCESS;
        }

        if (player.isSpectator() || !player.getItemInHand(handIn).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (state.getOptionalValue(FACING).orElse(Direction.WEST) != Direction.UP) {
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
        /*return onBlockEntityUse(worldIn, pos, be -> {

            for (int i = 0; i < be.inventory.getSlots(); i++) {
                ItemStack heldItemStack = be.inventory.getStackInSlot(i);
                if (!worldIn.isClientSide && !heldItemStack.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(heldItemStack);
                }
            }
            be.inventory.clear();
            be.notifyUpdate();
            return InteractionResult.SUCCESS;
        });*/
    }


    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) { return super.getFacingForPlacement(context).getOpposite(); }


    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (!(level instanceof ServerLevel sLevel))
            return;

        if (!entity.isRemoved() && entity instanceof ItemEntity itemEntity) {
            withBlockEntityDo(sLevel, pos, be -> {
                if (ItemHandlerHelper.insertItemStacked(be.inInventory, itemEntity.getItem(), true).isEmpty()) {
                    ItemHandlerHelper.insertItemStacked(be.inInventory, itemEntity.getItem(), false);
                    itemEntity.remove(Entity.RemovalReason.KILLED);
                }
            });
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        //EzDebug.log("entity fall on:" + entityIn);
        //AllBlocks.MECHANICAL_DRILL
        super.updateEntityAfterFallOn(worldIn, entityIn);
        /*if (entityIn instanceof ItemEntity && entityIn.level() instanceof ServerLevel level) {
            withBlockEntityDo(worldIn, );
        }*/
        /*if (!(entityIn instanceof ItemEntity) || entityIn.level().isClientSide) {
            return;
        }*/

        /*BlockPos pos = entityIn.blockPosition();
        withBlockEntityDo(entityIn.level(), pos, be -> {
            if (be.getSpeed() == 0.0f) {
                return;
            }
            be.insertItem((ItemEntity) entityIn);
        });*/
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) { return PushReaction.NORMAL; }

    public static boolean isHorizontal(BlockState state) {
        return state.getValue(FACING).getAxis().isHorizontal();
    }

    @Override // com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock, com.simibubi.create.content.kinetics.base.IRotate
    public Direction.Axis getRotationAxis(BlockState state) {
        boolean alongFirst = state.getValue(AXIS_ALONG_FIRST_COORDINATE);
        return switch (state.getValue(FACING).getAxis()) {
            case Y -> alongFirst ? Direction.Axis.Z :Direction.Axis.X;
            case X -> Direction.Axis.Z;
            case Z -> Direction.Axis.X;
        };
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) { return false; }


    @Override
    public Class<ShredderBe> getBlockEntityClass() { return ShredderBe.class; }
    @Override
    public BlockEntityType<? extends ShredderBe> getBlockEntityType() { return WapBlockEntites.SHREDDER_BE.get(); }


    public static class RenderFriend {
        public static final float CRUSHER_MOVE_ALONG = 0.375f;  //似乎使用负值别有一种风格
        public static final float CRUSHER_MOVE_SIDE = 0.1875f;

        public static void getCrusherOffset(BlockState state, Vector3f dest1, Vector3f dest2) {
            Shredder block = (Shredder)state.getBlock();
            //int axisStep = state.getValue(FACING).getAxisDirection().getStep();

            dest1.set(JomlUtil.fNormal(state.getValue(FACING), CRUSHER_MOVE_ALONG));  //along translation
            dest2.set(dest1);

            if (isHorizontal(state)) {
                dest1.add(0, CRUSHER_MOVE_SIDE, 0);
                dest2.add(0, -CRUSHER_MOVE_SIDE, 0);
            } else {
                Direction.Axis rotateAxis = block.getRotationAxis(state);
                switch (rotateAxis) {
                    case X -> {
                        dest1.add(0, 0, CRUSHER_MOVE_SIDE);
                        dest2.add(0, 0, -CRUSHER_MOVE_SIDE);
                    }
                    case Z -> {
                        dest1.add(CRUSHER_MOVE_SIDE, 0, 0);
                        dest2.add(-CRUSHER_MOVE_SIDE, 0, 0);
                    }
                    default -> {
                        EzDebug.schedule("Pulverizer RenderFriend rotationAxis", "unexpected rotation Axis:" + rotateAxis);
                    }
                }
            }
        }

        public static Quaternionf getCrusherRotation(BlockState state, Quaternionf dest) {
            Direction.Axis rotateAxis = ((Shredder)state.getBlock()).getRotationAxis(state);
            return switch (rotateAxis) {
                case X -> dest.set(0, 0, 0, 1);
                case Z -> dest.set(new AxisAngle4f(1.5707963f, 0, 1, 0));
                case Y -> {
                    EzDebug.schedule("PulverizerRenderer rotateAxis", "rotateAxis can't be Y");
                    yield dest.set(0, 0, 0, 1);
                }
            };
        }
    }
    private static class PlacementHelper implements IPlacementHelper {
        private PlacementHelper() { }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            BlockEntry<Shredder> blockEntry = WapBlocks.Industrial.Machine.SHREDDER;
            return blockEntry::isIn;
        }
        @Override // com.simibubi.create.foundation.placement.IPlacementHelper
        public Predicate<BlockState> getStatePredicate() {
            return WapBlocks.Industrial.Machine.SHREDDER::has;
        }

        @Override // com.simibubi.create.foundation.placement.IPlacementHelper
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List<Direction> directions =
                IPlacementHelper.orderedByDistanceExceptAxis(
                    pos,
                    ray.getLocation(),
                    state.getValue(DirectionalKineticBlock.FACING).getAxis(),
                    dir -> world.getBlockState(pos.relative(dir)).canBeReplaced()
                );
            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success(
                pos.relative(directions.get(0)),
                s -> {
                    return s.setValue(
                        FACING,
                        state.getValue(FACING)
                    ).setValue(
                        DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE,
                        state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)
                    );
                }
            );
        }
    }

}