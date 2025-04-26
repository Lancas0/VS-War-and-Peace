package com.lancas.vs_wap.foundation.network.debug;

import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkRunnable {
    @FunctionalInterface
    public interface NetworkCtxConsumer extends Consumer<NetworkEvent.Context>, Serializable {
        public static byte[] serialize(NetworkCtxConsumer runnable) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(runnable);
                oos.close();
                return baos.toByteArray();

            } catch (Exception e) {
                EzDebug.error("fail to serialzie function.");
                throw new RuntimeException(e);
            }
        }
        public static NetworkCtxConsumer deserialize(byte[] bytes) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (NetworkCtxConsumer)ois.readObject();
            } catch (Exception e) {
                EzDebug.error("fail to deserialzie function.");
                throw new RuntimeException(e);
            }
        }
    }

    private final NetworkCtxConsumer runnable;
    public NetworkRunnable(@NotNull NetworkCtxConsumer inRunnable) {
        runnable = inRunnable;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBytes(NetworkCtxConsumer.serialize(runnable));
    }
    public static NetworkRunnable decode(FriendlyByteBuf buf) {
        return new NetworkRunnable(NetworkCtxConsumer.deserialize(buf.readByteArray()));
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> runnable.accept(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
