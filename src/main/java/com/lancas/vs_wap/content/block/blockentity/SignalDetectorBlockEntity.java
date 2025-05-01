package com.lancas.vs_wap.content.block.blockentity;

import com.lancas.vs_wap.content.block.blocks.SignalDetectorBlock;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.register.KeyBinding;
import com.lancas.vs_wap.ship.feature.hold.HoldingServerTickAttachment;
import com.lancas.vs_wap.util.ShipUtil;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.ServerShip;


public class SignalDetectorBlockEntity extends BlockEntity {
    private static final String KEY_NAME_TAG = "key_name";

    public SignalDetectorBlockEntity(BlockEntityType<?> type, BlockPos p_155229_, BlockState p_155230_) {
        super(type, p_155229_, p_155230_);
    }

    private String keyBindingName; // 实际存储的按键值

    // 从NBT读取数据
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.keyBindingName = tag.getString(KEY_NAME_TAG);
    }
    // 写入NBT数据
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putString(KEY_NAME_TAG, this.keyBindingName);
        super.saveAdditional(tag);
    }
    // 设置按键值（通过外部调用）
    public SignalDetectorBlockEntity setKeyBinding(KeyBinding keyBinding) {
        this.keyBindingName = keyBinding.toString();
        this.setChanged(); // 标记数据需要保存
        return this;
    }
    /*public SignalDetectorBlockEntity setKeyBinding(KeyBinding inKey) {
        CompoundTag keyTag = new CompoundTag();
        keyTag.putString(KEY_TAG, inKey.toString());
        saveAdditional(keyTag);
        return this;
    }
    public KeyBinding getKeyBinding() {
        CompoundTag keyTag = getPersistentData();
        for (String tagKey : keyTag.getAllKeys()) {
            EzDebug.Log("persistentData has key:" + tagKey);
        }

        if (!keyTag.contains(KEY_TAG)) {
            EzDebug.Log("signal detector block entity has no key");
            return null;
        }
        return KeyBinding.valueOf(keyTag.getString(KEY_TAG));
    }*/

    private int currentSignal = 0;

    // 每刻更新红石信号
    public void tick() {
        KeyBinding keyBinding = null;
        try {
            keyBinding = KeyBinding.valueOf(keyBindingName);
        } catch (Exception e) { EzDebug.log("fail to get keyBinding"); return; }

        if (level == null || level.isClientSide) return;

        ServerShip shipOn = ShipUtil.getServerShipAt((ServerLevel)level, worldPosition);
        if (shipOn == null)
            return;

        /*PlayerHoldingAttachment holdingAttachment = shipOn.getAttachment(PlayerHoldingAttachment.class);
        if (holdingAttachment == null)
            return;

        ServerPlayer player = holdingAttachment.getPlayer();
        if (player == null)
            return;*/
        HoldingServerTickAttachment ticker = shipOn.getAttachment(HoldingServerTickAttachment.class);
        if (ticker == null) return;

        Boolean signal = SignalDetectorBlock.singalDic.get(new Pair<>(ticker.getHolderUUID(), keyBinding.getKey()));

        //if (signal != null && signal)
        //    EzDebug.log("[check signal](" + player.getUUID() + ", " + keyBinding.getKey() + "):" + signal);

        currentSignal = (signal == null || signal.equals(false)) ? 0 : 15;
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock()); // 通知周围方块更新
    }

    public int getCurrentSignal() {
        return currentSignal;
    }
}
