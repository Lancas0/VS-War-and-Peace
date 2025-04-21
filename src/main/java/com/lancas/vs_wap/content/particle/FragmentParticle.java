package com.lancas.vs_wap.content.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

// 客户端粒子类
public class FragmentParticle extends TextureSheetParticle/* implements ParticleOptions*/ {
    private final Vector3f axisDirection; // 圆锥中心轴方向
    private final float coneAngle; // 圆锥角度（弧度）
    private final float radius;

    protected FragmentParticle(ClientLevel level, double x, double y, double z, Vector3f axisDir, float angleDeg, float radius) {
        super(level, x, y, z);
        this.axisDirection = axisDir.normalize();
        this.coneAngle = (float) Math.toRadians(angleDeg);
        this.radius = radius;
        this.lifetime = 200; // 粒子持续时间（20 ticks = 1秒）
        this.hasPhysics = false; // 禁用物理
    }

    @Override
    public void tick() {
        super.tick();
        // 在圆锥范围内随机生成位置
        if (age < lifetime) {
            // 生成极坐标（θ在圆锥角度内随机）
            double theta = random.nextFloat() * coneAngle;
            double phi = random.nextFloat() * 2 * Math.PI;

            // 转换为笛卡尔坐标（局部坐标系）
            double r = radius * (age / (float)lifetime); // 随时间扩大
            double xLocal = r * Math.sin(theta) * Math.cos(phi);
            double yLocal = r * Math.sin(theta) * Math.sin(phi);
            double zLocal = r * Math.cos(theta);

            // 将局部坐标旋转到中心轴方向
            //Vector3f pos = rotateVector(new Vector3f((float)xLocal, (float)yLocal, (float)zLocal), axisDirection);
            //this.setPos(pos.x + x, pos.y + y, pos.z + z);
        }
    }



    // 粒子渲染类型
    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }



    //ParticleOptions interface
    /*
    @Override
    public ParticleType<?> getType() { return EinheriarParticles.CONE_PARTICLE.get(); }
    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
        buf.writeVector3f(new Vector3f((float)this.xo, (float)this.yo, (float)this.zo));
        buf.writeVector3f(axisDirection);
        buf.writeFloat(coneAngle);
        buf.writeFloat(radius);
    }

    @Override
    public String writeToString() {
        return "";  //todo
    }*/


    // 粒子提供者（工厂）
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            FragmentParticle particle = new FragmentParticle(level, x, y, z, new Vector3f((float)xSpeed, (float)ySpeed, (float)zSpeed), 45, 10); // todo use by block params
            particle.pickSprite(spriteSet);
            return particle;
        }
    }
}