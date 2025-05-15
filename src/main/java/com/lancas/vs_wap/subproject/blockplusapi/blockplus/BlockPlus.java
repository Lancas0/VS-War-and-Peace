package com.lancas.vs_wap.subproject.blockplusapi.blockplus;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public abstract class BlockPlus extends Block {
    public static String defaultID(Class<? extends BlockPlus> type) {
        StringBuilder idBuilder = new StringBuilder();
        String typeSimpleName = type.getSimpleName();
        int typenameLen = typeSimpleName.length();

        for (int i = 0; i < typenameLen - 1; ++i) {
            char c = typeSimpleName.charAt(i);
            char nextC = typeSimpleName.charAt(i + 1);

            if (Character.isLowerCase(c) && Character.isUpperCase(nextC)) {
                idBuilder
                    .append(Character.toLowerCase(c))
                    .append('_');
            } else {
                idBuilder.append(Character.toLowerCase(c));  //todo uppercase, lowercase or 1-9, _ will be all fine?
            }
        }
        idBuilder.append(Character.toLowerCase(typeSimpleName.charAt(typenameLen - 1)));
        return idBuilder.toString();
    }
    private static final Hashtable<Class<? extends BlockPlus>, List<IBlockAdder>> addersCache = new Hashtable<>();
    private static final Hashtable<Class<? extends BlockPlus>, BlockPlus> instances = new Hashtable<>();
    //public static Hashtable<Class<? extends BlockPlus>, String> typeToId = new Hashtable<>();

    public abstract Iterable<IBlockAdder> getAdders();

    protected BlockPlus(Properties p_49795_) {
        super(p_49795_);

        var adders = getAdders();
        if (adders == null) {
            throw new RuntimeException("BlockPlus can't have null adders! You can set adders a empty list. Remember that if you want to a list variable, set it to static, or simply use BLockPlus::addersIfAbsent");
        }

        AtomicReference<BlockState> defaultState = new AtomicReference<>(this.stateDefinition.any());
        adders.forEach(p -> {
            defaultState.set(p.setValueForDefaultState(defaultState.get()));
        });
        this.registerDefaultState(defaultState.get());

        /*getAdders().forEach(p -> {
            p.onInit(this);
        });
        this.registerDefaultState(this.stateDefinition.any());*/

        instances.put(this.getClass(), this);
    }
    public static BlockPlus getInstance(Class<? extends BlockPlus> type) {
        return instances.get(type);
    }

    public static List<IBlockAdder> addersIfAbsent(Class<? extends BlockPlus> type, Supplier<List<IBlockAdder>> addersSupplier) {
        if (!addersCache.containsKey(type))
            addersCache.put(type, addersSupplier.get());

        return addersCache.get(type);
    }

    /*public <T extends Comparable<T>> void acceptPropertyForDefaultState(Property<T> property, T defaultVal) {
        //this.stateDefinition.any().setValue(property, defaultVal);
    }*/
    /*private BlockState buildDefaultState() {
        BlockState baseState = super.defaultBlockState();
        StateDefinition.Builder<Block, BlockState> builder = new
        // 复制原版属性
        baseState.getProperties().forEach(prop ->
            builder.setValue(prop, baseState.getValue(prop))
        );
        // 添加 PropertyAdder 的默认值
        getAdders().forEach(adder -> {
            if (adder instanceof AbstractPropertyAdder) {
                AbstractPropertyAdder<?> propAdder = (AbstractPropertyAdder<?>) adder;
                builder.setValue(propAdder.getProperty(), propAdder.getDefaultValue());
            }
        });
        return builder.create();
    }*/
    /*public void acceptPropertyForStateDefinition(StateDefinition.Builder<Block, BlockState> builder, Property<?> property) {
        builder.add(property);
    }
    // 如果直接使用acceptPropertyForDedaultState(必须在super之后)，那么会导致提前注册defaultState后再向defaultState中注册property
    // 重写 registerDefaultState 以正确设置默认值
    /*@Override
    public void registerDefaultState(BlockState state) {
        BlockState.Builder newStateBuilder = new BlockState.Builder(this);
        getAdders().forEach(adder -> {
            if (adder instanceof AbstractPropertyAdder<?> propAdder) {
                newStateBuilder.setValue(propAdder.getProperty(), propAdder.getDefaultValue());
            }
        });
        super.registerDefaultState(newStateBuilder.create());
    }*/
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        getAdders().forEach(p -> {
            p.onCreateBlockStateDefinition(builder);
        });
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
        AtomicReference<BlockState> state = new AtomicReference<>(super.getStateForPlacement(ctx));
        getAdders().forEach(p -> state.set(p.getStateForPlacement(ctx, state.get())));
        return state.get();
    }

    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        final VoxelShape[] shape = {Shapes.empty()};
        getAdders().forEach(p ->
            shape[0] = Shapes.join(shape[0], p.appendShape(state), BooleanOp.OR)
        );
        return shape[0];
    }

    @Override
    public int getSignal(@NotNull BlockState state, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        final AtomicInteger signal = new AtomicInteger(0);
        getAdders().forEach(p -> signal.addAndGet(p.getRedstoneModifyValue(state, blockAccess, pos, side)));

        if (signal.get() <= 0)
            return 0;
        if (signal.get() >= 15)
            return 15;
        return signal.get();
    }
    @Override
    public boolean isSignalSource(@NotNull BlockState state) {
        AtomicBoolean isSrc = new AtomicBoolean(false);
        getAdders().forEach(p -> isSrc.set(isSrc.get() || p.provideRedstoneSrcVerification(state)));
        return isSrc.get();
    }
    @Override
    public int getDirectSignal(@NotNull BlockState state, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {  //todo ?
        return getSignal(state, blockAccess, pos, side);
    }
    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        AtomicInteger analogSignal = new AtomicInteger();
        getAdders().forEach(p -> {
            analogSignal.addAndGet(p.getAnalogModifySignal(state, level, pos));
        });

        if (analogSignal.get() <= 0)
            return 0;
        if (analogSignal.get() >= 15)
            return 15;
        return analogSignal.get();
    }


    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        AtomicBoolean anyFail = new AtomicBoolean(false);
        AtomicBoolean preventLaterAction = new AtomicBoolean(false);

        getAdders().forEach(p -> {
            InteractionResult result = p.onInteracted(state, level, pos, player, hand, hit);

            switch (result) {
                case FAIL -> {
                    anyFail.set(true);
                    preventLaterAction.set(true);
                }
                case CONSUME, SUCCESS -> preventLaterAction.set(true);
            }
        });

        //todo success for arm swing animation
        if (anyFail.get())
            return InteractionResult.FAIL;
        if (preventLaterAction.get())
            return InteractionResult.CONSUME;

        return InteractionResult.PASS;
    }


    //todo check redstone signal at place
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        getAdders().forEach(p -> p.onNeighborChanged(state, level, pos, block, fromPos, isMoving));
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        getAdders().forEach(p -> p.onPlacedBy(level, pos, state, placer, stack));
    }
    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        getAdders().forEach(p -> p.onPlace(state, level, pos, oldState, isMoving));
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        getAdders().forEach(p -> p.onRemove(state, level, pos, newState, isMoving));
    }

    /*@Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack mineWithStack) {
        player.awardStat(Stats.BLOCK_MINED.get(this));

        AtomicBoolean vanillaDrop = new AtomicBoolean(true);
        AtomicBoolean dropXp = new AtomicBoolean(false);
        AtomicReference<Float> causeFoodExhaustion = new AtomicReference<>(0F);

        getAdders().forEach(
            p -> {
                if (p.cancelVanillaItemDrop())
                    vanillaDrop.set(false);

                if (p.dropXpOnMinedAppend())
                    dropXp.set(true);

                causeFoodExhaustion.updateAndGet(f -> f + p.foodExhaustionOnMinedAppend());

                p.onPlayerMined(level, player, pos, state, be, mineWithStack);
            }
        );

        player.causeFoodExhaustion(causeFoodExhaustion.get());
        if (vanillaDrop.get())
            dropResources(state, level, pos, be, player, mineWithStack, dropXp.get());
    }*/
}
