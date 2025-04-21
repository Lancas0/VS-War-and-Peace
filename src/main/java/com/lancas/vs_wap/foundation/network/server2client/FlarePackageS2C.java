package com.lancas.vs_wap.foundation.network.server2client;

import com.lancas.vs_wap.handler.FlareEffectClientMgr;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlarePackageS2C {
    private final int flareRange;
    private final Vec3 flarePos;
    private final int flareTicks;
    private final float flareGamma;

    public FlarePackageS2C(int inFlareRange, Vec3 inFlarePos, int inFlareTicks, float inFlareGamma) {
        flareRange = inFlareRange;
        flarePos = inFlarePos;
        flareTicks = inFlareTicks;
        flareGamma = inFlareGamma;
    }

    public static void encode(FlarePackageS2C msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.flareRange);
        buf.writeVector3f(msg.flarePos.toVector3f());
        buf.writeInt(msg.flareTicks);
        buf.writeFloat(msg.flareGamma);
    }
    public static FlarePackageS2C decode(FriendlyByteBuf buf) {
        return new FlarePackageS2C(
            buf.readInt(),
            JomlUtil.v3(buf.readVector3f()),
            buf.readInt(),
            buf.readFloat()
        );
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            //flare effect
            FlareEffectClientMgr.addFlareData(flareRange, flarePos, flareTicks, flareGamma);

            //flare particle
            RandomSource random = mc.level.random;
            for (int i = 0; i < 50; i++) {
                double x = flarePos.x + 0.5 + random.nextGaussian() * 0.5;
                double y = flarePos.y + 0.5 + random.nextGaussian() * 0.5;
                double z = flarePos.z + 0.5 + random.nextGaussian() * 0.5;

                mc.level.addParticle(ParticleTypes.FLAME,
                    x, y, z, 0, 0, 0);
                    /*random.nextGaussian() * 0.02,
                    random.nextDouble() * 0.1,
                    random.nextGaussian() * 0.02);*/
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
