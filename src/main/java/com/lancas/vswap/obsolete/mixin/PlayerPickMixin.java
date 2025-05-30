package com.lancas.vswap.obsolete.mixin;



/*
@Mixin(Player.class)
public abstract class PlayerPickMixin {
    @Redirect(
        method = "pick",
        at = @At("HEAD")
        /.*at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
        )*./
    )
    private VoxelShape ignoreInvisibleBlockCollision(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getBlock() instanceof InvisibleBlock) {
            return Shapes.empty(); // 返回空碰撞箱，使射线检测不命中
        }
        return state.getCollisionShape(level, pos);
    }
}*/