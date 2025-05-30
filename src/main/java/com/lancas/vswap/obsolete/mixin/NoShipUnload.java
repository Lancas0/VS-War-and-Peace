package com.lancas.vswap.obsolete.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.core.impl.shadow.Ak;

@Mixin(Ak.class)
public abstract class NoShipUnload {
    /*
    @Inject(
        method = "b(Lorg/valkyrienskies/core/impl/shadow/Ao;)V",
        at = @org.spongepowered.asm.mixin.injection.At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/ShipPhysicsData;setLinearVelocity(Lorg/joml/Vector3dc;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void test(Ao par1, CallbackInfo ci) {
        //EzDebug.Log(StringUtil.toF2String(var9.c().getVel()));
        for (long id : BallisticsController.ChunkManagement.id2InChunk.keySet()) {
            Ah ah = par1.a().get(id);
            if (ah == null) continue;
            EzDebug.Log("vel:" + ah.c().getVel());
        }
    }*/
}

    /*@Inject(
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/shadow/zB;getActiveChunksSet()Lorg/valkyrienskies/core/api/ships/properties/IShipActiveChunksSet;"
        ),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lkotlin/collections/MapsKt;getValue(Ljava/util/Map;Ljava/lang/Object;)Ljava/lang/Object;",
                shift = At.Shift.BY,
                by = 2
            ),
            to = @At("TAIL")
        ),
        //locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void test(
        Set<? extends IPlayer> par1,
        Set<? extends IPlayer> par2,
        Iterable<? extends zB> par3,
        Iterable<? extends zB> par4,
        CallbackInfoReturnable<ChunkWatchTasks> cir//,
        //zB var25
    ) {
        //EzDebug.Log("var25:" + var25.getId() + ", name:" + var25.getSlug());
        EzDebug.Log("test invoked");
    }*/
    /*@ModifyVariable(
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        at = @At("HEAD"),
        ordinal = 3,
        remap = false
    )
    private Iterable<? extends zB> noUnloadShips(Iterable<? extends zB> shipToUnload) {
        //List<zB> unLoadShips;
        for (zB ship : shipToUnload) {
            EzDebug.Log("unloading ship:" + ship.getId() + ", name: ship:" + ship.getSlug());
        }
        //return List.of();
        return shipToUnload;
    }*/

    /*@ModifyArg(
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        at = @At("HEAD"),
        index = 3,
        remap = false
    )
    private Iterable<? extends zB> noUnloadShips(Iterable<? extends zB> shipToUnload) {
        //List<zB> unLoadShips;
        for (zB ship : shipToUnload) {
            EzDebug.Log("unloading ship:" + ship.getId() + ", name: ship:" + ship.getSlug());
        }
        return List.of();
    }*/

    /*@Inject(
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        at = @At("HEAD"),
        remap = false
    )
    private void getUnloadShips(Set<? extends IPlayer> curPlayers, Set<? extends IPlayer> prePlayers, Iterable<? extends zB> shipToLoad, Iterable<? extends zB> shipToUnload, CallbackInfoReturnable<ChunkWatchTasks> cir) {

    }*/
    /*@Inject(
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        at = @At("HEAD"),
        remap = false
    )*/
    /*@Inject(
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/config/VSCoreConfig$Server;getShipUnloadDistance()D",
            shift = At.Shift.AFTER
        ),
        remap = false
    )*/
    /*@Inject(
        method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
        at = @At("HEAD"),
        remap = false
    )
    private void getUnloadShips(Set<? extends IPlayer> curPlayers, Set<? extends IPlayer> prePlayers, Iterable<? extends zB> shipToLoad, Iterable<? extends zB> shipToUnload, CallbackInfoReturnable<ChunkWatchTasks> cir) {
        shipToUnload.forEach(
            ship -> EzDebug.Log("unload ship: " + ship.getId() + ", name:" + ship.getSlug())
        );
    }*/
