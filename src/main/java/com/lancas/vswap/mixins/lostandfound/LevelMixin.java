package com.lancas.vswap.mixins.lostandfound;


import com.lancas.vswap.subproject.lostandfound.content.LostAndFoundBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class LevelMixin {
    /*@Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onSetBlock(BlockPos pos, BlockState newState, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level)(Object)this;
        BlockState oldState = level.getBlockState(pos);

        // 过滤无效更新（相同状态）
        if (oldState == newState) return;

        LostAndFoundEvent.preBlockChangeEvt.invokeAll(level, pos, oldState, newState);
    }*/
    /*@Inject(
        method = "removeBlockEntity",
        at = @At("HEAD")
    )
    private void onRemoveBe(BlockPos bp, CallbackInfo ci) {
        Level level = (Level)(Object)this;
        BlockEntity be = level.getBlockEntity(bp);
        /.*if (be instanceof IRemoveCallbackBe rcbe) {
            rcbe.onRemoveFromLevel(level);
        }*./
        if (be instanceof SmartBlockEntity smartBe) {
            var lafBehaviour = smartBe.getBehaviour(LostAndFoundBehaviour.TYPE);
            if (lafBehaviour != null)
                lafBehaviour.onRemoveFromLevel();
        }
    }*/
}
