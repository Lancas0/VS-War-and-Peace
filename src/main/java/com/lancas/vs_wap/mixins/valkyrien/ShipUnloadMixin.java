package com.lancas.vs_wap.mixins.valkyrien;

import com.lancas.vs_wap.debug.EzDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.apigame.world.IPlayer;
import org.valkyrienskies.core.apigame.world.chunks.ChunkWatchTasks;
import org.valkyrienskies.core.impl.chunk_tracking.h;
import org.valkyrienskies.core.impl.shadow.zB;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(h.class)
public class ShipUnloadMixin {
    @Inject(
        at = @At("HEAD"),
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        remap = false
    )
    public void onShipLoadStateChange(Set<? extends IPlayer> par1, Set<? extends IPlayer> par2, Iterable<? extends zB> par3, Iterable<? extends zB> par4, CallbackInfoReturnable<ChunkWatchTasks> cir) {
        //par3是正在加载的船，每tick都会包含所有正在加载的船

        AtomicInteger unloadingShipCnt = new AtomicInteger();
        AtomicReference<String> str = new AtomicReference<>("ship to unload:");
        par4.forEach(
            ship -> {
                unloadingShipCnt.getAndIncrement();
                str.updateAndGet(s -> s + ship.getId() + ", ");
            }
        );

        if (unloadingShipCnt.get() > 0)
            EzDebug.log(str.get());
    }


}
