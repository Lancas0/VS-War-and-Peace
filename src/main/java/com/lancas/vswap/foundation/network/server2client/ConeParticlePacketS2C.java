package com.lancas.vswap.foundation.network.server2client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Supplier;

public class ConeParticlePacketS2C {
    private final BlockPos origin;
    private final Vector3f axisDir;
    private final float angle;
    private final float radius;

    public ConeParticlePacketS2C(BlockPos inPos, Vector3f inAxisDir, float inAngle, float inRadius) {
        origin = inPos;
        axisDir = inAxisDir.normalize(new Vector3f());
        angle = inAngle;
        radius = inRadius;
    }

    // 序列化与反序列化
    public static void encode(ConeParticlePacketS2C msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.origin);
        buffer.writeVector3f(msg.axisDir);
        buffer.writeFloat(msg.angle);
        buffer.writeFloat(msg.radius);
    }
    public static ConeParticlePacketS2C decode(FriendlyByteBuf buffer) {
        return new ConeParticlePacketS2C(
            buffer.readBlockPos(),
            buffer.readVector3f(),
            buffer.readFloat(),
            buffer.readFloat()
        );
    }

    // 客户端处理
    public static void handle(ConeParticlePacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            //Vec3 originVec = msg.origin.getCenter();
            Random random = new Random();
            Vector3f originPos = msg.origin.getCenter().toVector3f();

            // 生成圆锥范围内的粒子
            for (int i = 0; i < 800; i++) { // 粒子数量可调
                /*
                // 随机生成极坐标
                double theta = Math.toRadians(random.nextFloat() * msg.angle);
                double phi = random.nextFloat() * 2 * Math.PI;
                double r = msg.radius * random.nextFloat();

                // 转换为局部坐标

                double x = r * Math.sin(theta) * Math.cos(phi);
                double y = r * Math.sin(theta) * Math.sin(phi);
                double z = r * Math.cos(theta);

                // 对齐到圆锥方向
                Vector3f rotated = rotateVector(new Vector3f((float)x, (float)y, (float)z), msg.axisDir);

                // 生成原版粒子（使用CRIT粒子）
                level.addParticle(
                    ParticleTypes.POOF,
                    originVec.x + rotated.x,
                    originVec.y + rotated.y,
                    originVec.z + rotated.z,
                    0, 0, 0 // 无速度
                );*/

                float h = random.nextFloat() * msg.radius;
                float r = random.nextFloat() * (float)Math.tan(msg.angle / 2f) * h;

                //在xoz平面上随机取一点在半径为r的原上的点
                float alpha = random.nextFloat((float)Math.PI * 2f);
                float locX = (float)Math.cos(alpha) * r;
                float locZ = (float)Math.sin(alpha) * r;

                Vector3f verticalVec = rotateVector(new Vector3f(locX, 0, locZ), msg.axisDir);
                Vector3f pos = msg.axisDir.mul(h, new Vector3f()).add(verticalVec).add(originPos);

                level.addParticle(
                    ParticleTypes.POOF,
                    pos.x,
                    pos.y,
                    pos.z,
                    0, 0, 0 // 无速度
                );

                //EzDebug.Log("spawn particle at " + pos + ", along:" + msg.axisDir);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // 向量旋转工具方法（将局部坐标对齐到目标方向）
    private static Vector3f rotateVector(Vector3f locPos, Vector3f targetDir) {
        Vector3f defaultAxis = new Vector3f(0, 1, 0); // 假设原方向是Y轴
        if (targetDir.dot(defaultAxis) > 0.99) return locPos;

        Quaternionf quat = new Quaternionf().rotationTo(
            defaultAxis,
            targetDir
        );
        return quat.transform(locPos);
    }
}
