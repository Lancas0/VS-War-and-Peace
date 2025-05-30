package com.lancas.vswap.mixins.valkyrien;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.entity.handling.WorldEntityHandler;


@Mixin(WorldEntityHandler.class)
public abstract class AllowTeleportToShipyardMixin1 {

    @Inject(
        method = "moveEntityFromShipyardToWorld(Lnet/minecraft/world/entity/Entity;Lorg/valkyrienskies/core/api/ships/Ship;DDD)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void allowSetPos(Entity entity, Ship ship, double entityX, double entityY, double entityZ, CallbackInfo ci) {
        EzDebug.log("set pos:" + StrUtil.poslike(entityX, entityY, entityZ));
        entity.setPos(entityX, entityY, entityZ);
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();



        ci.cancel();
    }

    @Inject(
        method = "getTeleportPos",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void allowTeleport(Entity self, Vector3d pos, CallbackInfoReturnable<Vector3d> cir) {
        EzDebug.log("return:" + StrUtil.F2(pos));
        cir.setReturnValue(pos);
    }



    /*
    @Shadow private Vec3 position;
    @Shadow private BlockPos blockPosition;
    @Shadow private BlockState feetBlockState;
    @Shadow private ChunkPos chunkPosition;
    @Shadow private EntityInLevelCallback levelCallback;


    @Shadow public abstract boolean removeTag(String p_20138_);

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    public void teleportTo(double p_19887_, double p_19888_, double p_19889_, CallbackInfo ci) {
        Entity thisEntity = (Entity)(Object)this;

        if (thisEntity.level() instanceof ServerLevel) {
            thisEntity.moveTo(p_19887_, p_19888_, p_19889_, thisEntity.getYRot(), thisEntity.getXRot());
            //thisEntity.teleportPassengers();  //just temp
        }

        ci.cancel();
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At("HEAD"), cancellable = true)
    public void teleportTo(ServerLevel p_265257_, double p_265407_, double p_265727_, double p_265410_, Set<RelativeMovement> p_265083_, float p_265573_, float p_265094_, CallbackInfoReturnable<Boolean> cir) {
        Entity thisEntity = (Entity)(Object)this;

        float f = Mth.clamp(p_265094_, -90.0F, 90.0F);
        if (p_265257_ == thisEntity.level()) {
            thisEntity.moveTo(p_265407_, p_265727_, p_265410_, p_265573_, f);
            //thisEntity.teleportPassengers();
            thisEntity.setYHeadRot(p_265573_);
        } else {
            thisEntity.unRide();
            Entity entity = thisEntity.getType().create(p_265257_);
            if (entity == null) {
                cir.setReturnValue(false);
                return;
            }

            entity.restoreFrom(thisEntity);
            entity.moveTo(p_265407_, p_265727_, p_265410_, p_265573_, f);
            entity.setYHeadRot(p_265573_);
            thisEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
            p_265257_.addDuringTeleport(entity);
        }

        cir.setReturnValue(true);
        return;
    }

    @Overwrite
    public final void setPosRaw(double p_20344_, double p_20345_, double p_20346_) {
        Entity thisEntity = (Entity)(Object)this;
        
        if (position.x != p_20344_ || position.y != p_20345_ || position.z != p_20346_) {
            position = new Vec3(p_20344_, p_20345_, p_20346_);
            int i = Mth.floor(p_20344_);
            int j = Mth.floor(p_20345_);
            int k = Mth.floor(p_20346_);
            if (i != blockPosition.getX() || j != blockPosition.getY() || k != blockPosition.getZ()) {
                blockPosition = new BlockPos(i, j, k);
                feetBlockState = null;
                if (SectionPos.blockToSectionCoord(i) != chunkPosition.x || SectionPos.blockToSectionCoord(k) != chunkPosition.z) {
                    chunkPosition = new ChunkPos(blockPosition);
                }
            }

            levelCallback.onMove();
        }

        if (thisEntity.isAddedToWorld() && !thisEntity.level().isClientSide && !thisEntity.isRemoved()) {
            thisEntity.level().getChunk((int)Math.floor(p_20344_) >> 4, (int)Math.floor(p_20346_) >> 4);
        }

    }*/
    /**
     * 覆盖原版 teleportTo(double, double, double) 的拦截逻辑
     */
    /*@Overwrite
    private void disableVSTeleportHook(double x, double y, double z, CallbackInfo ci) {
        // 不执行任何操作，直接跳过原 Mixin 的逻辑
        // 如果原 Mixin 需要取消，此处可以保持空实现
    }*/

    /**
     * 覆盖原版 teleportTo(ServerLevel, ...) 的拦截逻辑
     */
    /*@Overwrite
    private void disableVSTeleportHook(
        ServerLevel level, double x, double y, double z,
        Set<RelativeMovement> flags, float yRot, float xRot,
        CallbackInfoReturnable<Boolean> ci
    ) {
        // 不执行任何操作，直接跳过原 Mixin 的逻辑
    }*/
}