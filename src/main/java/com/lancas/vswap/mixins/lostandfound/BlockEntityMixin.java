package com.lancas.vswap.mixins.lostandfound;

/*
import com.lancas.vswap.debug.EzDebug;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static com.lancas.vswap.subproject.lostandfound.LostAndFound.UUID_KEY;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void onSave(CompoundTag tag, CallbackInfo ci) {
        if (!tag.contains(UUID_KEY)) {  //don't have uuid, will be recreated when saving. Although it's expected to already spawn a uuid when load
            EzDebug.warn("generate lost and found key when saving.");
            tag.putUUID(UUID_KEY, UUID.randomUUID());
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (!tag.contains(UUID_KEY)) {  //generate new uuid when load and no prev uuid
            tag.putUUID(UUID_KEY, UUID.randomUUID());
        }
        //todo check if the uuid is dead, if dead then spawn an other one.
        //check if the uuid is alive, if alive then spawn an other one.
    }
}
*/