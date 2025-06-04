package com.lancas.vswap.foundation.network.server2client.send;

import com.lancas.vswap.content.WapSounds;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.server2client.ConeParticlePacketS2C;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Supplier;

public class S2CSendSoundPacket {
    private ResourceLocation soundRes;
    private BlockPos at;


    private S2CSendSoundPacket(ResourceLocation inSoundRes, BlockPos inAt) {
        soundRes = inSoundRes;
        at = inAt;
    }
    // 序列化与反序列化
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(soundRes).writeBlockPos(at);
    }
    public static S2CSendSoundPacket decode(FriendlyByteBuf buffer) {
        return new S2CSendSoundPacket(buffer.readResourceLocation(), buffer.readBlockPos());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundRes);
            if (soundEvent == null) {
                EzDebug.warn("fail to get soundEvt of " + soundRes.toString());
                return;
            }

            //level.playSound(null, at, soundEvent);
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
