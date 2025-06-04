package com.lancas.vswap.mixins.debug;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SmartBlockEntity.class)
public class SmartBlockEntityMixin {
    /*@Inject(
        method = "tick",
        at = @At("HEAD"),
        remap = false
    )
    public void debugMultiBlockRange(CallbackInfo ci) {
        SmartBlockEntity selfBe = (SmartBlockEntity)(Object)this;
        if (selfBe instanceof IMultiBlockEntityContainer mbec && selfBe.getLevel() instanceof ServerLevel && mbec.isController()) {
            //EzDebug.light("size:" + size + ", pos:" + worldPosition + ", controller:" + controller);
            BlockPos bp = selfBe.getBlockPos();

            String key = "dockbe outline" + bp.toShortString();
            RandUtil.pushSeed(key);
            int color = RandUtil.nextColor();
            RandUtil.popSeed();

            NetworkHandler.sendToAllPlayers(new CreateOutlinePacketS2C(
                key,
                new AABB(bp.getX(), bp.getY(), bp.getZ(), bp.getX() + mbec.getWidth(), bp.getY() + mbec.getHeight(), bp.getZ() + mbec.getWidth()),
                color
            ));
        }
    }*/
}
