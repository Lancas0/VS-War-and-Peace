package com.lancas.vswap.content.block.blocks;

import com.lancas.vswap.content.block.blockentity.SignalDetectorBlockEntity;
import com.lancas.vswap.register.KeyBinding;
import com.simibubi.create.foundation.block.IBE;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Hashtable;
import java.util.UUID;


public class SignalDetectorBlock extends Block implements IBE<SignalDetectorBlockEntity> {
    //public static TriggerLinkBlock Default = new TriggerLinkBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    //public static final IntegerProperty KEY_CODE = IntegerProperty.create("key_code", 0, Integer.MAX_VALUE);


    public static Hashtable<Pair<UUID, Integer>, Boolean> singalDic = new Hashtable<>();
    public static void setSingal(UUID playerUUID, int signalKey, boolean signal) {
        singalDic.put(new Pair<>(playerUUID, signalKey), signal);
    }

    private final KeyBinding key;
    public SignalDetectorBlock(KeyBinding inKey, Properties p_49795_) {
        super(p_49795_);
        key = inKey;
    }


    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (blockAccess.getBlockEntity(pos) instanceof SignalDetectorBlockEntity blockEntity) {
            return blockEntity.getCurrentSignal();
        }
        return 0;
    }
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
    @Override
    public int getDirectSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return getSignal(state, blockAccess, pos, side);
    }


    @Override
    public Class<SignalDetectorBlockEntity> getBlockEntityClass() { return SignalDetectorBlockEntity.class; }
    @Override
    public BlockEntityType<? extends SignalDetectorBlockEntity> getBlockEntityType() { return null;/*todo WapBlockEntites.SIGNAL_DETECTOR_BE.get();*/ }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        SignalDetectorBlockEntity be = this.getBlockEntityType().create(pos, state);
        if (be != null)
            be.setKeyBinding(key);
        return be;
    }
    /*@Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        //SignalDetectorBlockEntity createdBlockEntity =
        //    (SignalDetectorBlockEntity)blockEntity.get().create(pos, state);
        SignalDetectorBlockEntity createdBlockEntity =
            (SignalDetectorBlockEntity)blockEntity.get().create(pos, state);
            //(SignalDetectorBlockEntity)AllBlockEntites.SignalDetectorBlockEntity.get().create(pos, state);

        if (createdBlockEntity == null)
            return null;

        EzDebug.Log("set key to block entity:" + key.toString());
        return createdBlockEntity.setKeyBinding(key);
    }*/

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level, BlockState state, BlockEntityType<T> type
    ) {
        return level.isClientSide ? null : // 仅在服务端执行
            (lvl, pos, blockState, blockEntity) -> ((SignalDetectorBlockEntity) blockEntity).tick();
    }
}