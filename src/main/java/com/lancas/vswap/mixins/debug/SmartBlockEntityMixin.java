package com.lancas.vswap.mixins.debug;

import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.server2client.CreateOutlinePacketS2C;
import com.lancas.vswap.util.RandUtil;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
