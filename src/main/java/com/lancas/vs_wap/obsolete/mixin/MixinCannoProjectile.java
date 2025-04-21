package com.lancas.vs_wap.obsolete.mixin;

/*
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rbasamoyai.createbigcannons.munitions.big_cannon.AbstractBigCannonProjectile;

@Mixin(AbstractBigCannonProjectile.class)
public abstract class MixinCannoProjectile extends Entity {

    @Unique public int stopTick = 4;

    public MixinCannoProjectile(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);

        AbstractBigCannonProjectile projectile = (AbstractBigCannonProjectile)((Object)this);

        if (--stopTick < 0) {
            stopTick = 4;
            /.*if (projectile.getDeltaMovement().length() < 0.02) {
                EzDebug.log("cannon project speed is low, pos:" + StringUtil.poslike(x, y, z));
            } else {
                EzDebug.log("currentPos:" + StringUtil.poslike(x, y, z));
            }*./EzDebug.log("currentPos:" + StrUtil.poslike(x, y, z) + ", deltaMove:" + StrUtil.F2(projectile.getDeltaMovement().length()));

        }

    }



}
*/