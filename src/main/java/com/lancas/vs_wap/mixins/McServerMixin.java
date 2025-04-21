package com.lancas.vs_wap.mixins;

import com.lancas.vs_wap.register.ServerDataCollector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//todo simply use accessor
@Mixin(MinecraftServer.class)
public abstract class McServerMixin {
    @Shadow
    private PlayerList playerList;


    //todo there must be a better way to get players
    @Inject(
        method = "tickServer",
        at = @At("HEAD")
    )
    private void preTickGetPlayers(final CallbackInfo ci) {
        //EzDebug.Log("get player list:" + playerList);
        ServerDataCollector.playerList = playerList;
    }

    /*
    @Inject(
        method = "tickServer",
        at = @At("TAIL")
    )
    private void postTick(BooleanSupplier p_129871_, CallbackInfo ci) {
        MinecraftServer mcServer =
    }*/

}
