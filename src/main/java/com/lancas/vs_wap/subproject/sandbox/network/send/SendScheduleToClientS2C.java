package com.lancas.vs_wap.subproject.sandbox.network.send;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.api.ISavedObject;
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vs_wap.subproject.sandbox.thread.api.IScheduleExecutor;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduleData;
import com.lancas.vs_wap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;
import java.util.function.Supplier;

public class SendScheduleToClientS2C {
    private final String levelName;
    private final String threadTypename;
    private final String scheduleDataTypename;
    private final CompoundTag savedData;

    private SendScheduleToClientS2C(String inLevelName, String inThreadTypename, String inScheduleDataTypename, CompoundTag inSavedData) {
        levelName = inLevelName;
        threadTypename = inThreadTypename;
        scheduleDataTypename = inScheduleDataTypename;
        savedData = inSavedData;
    }
    public static <T extends IScheduleData & ISavedObject<T>> SendScheduleToClientS2C create(
        ServerLevel levelSendFrom,
        Class<? extends ISandBoxThread<?>> inThreadType,
        T inScheduleData
    ) {
        return new SendScheduleToClientS2C(
            VSGameUtilsKt.getDimensionId(levelSendFrom),
            inThreadType.getName(),
            inScheduleData.getClass().getName(),
            inScheduleData.saved()
        );
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(levelName)
            .writeUtf(threadTypename)
            .writeUtf(scheduleDataTypename)
            .writeNbt(savedData);
    }
    public static SendScheduleToClientS2C decode(FriendlyByteBuf buffer) {
        return new SendScheduleToClientS2C(
            buffer.readUtf(),
            buffer.readUtf(),
            buffer.readUtf(),
            buffer.readNbt()  //todo read any size NBT?
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;

            if (!Objects.equals(clientWorld.getCurLevelName(), levelName)) {
                return;  //no need for sync because it is not in current level
            }

            try {
                Class<?> threadType = Class.forName(threadTypename);
                ISandBoxThread<SandBoxClientWorld> thread = clientWorld.getThread(threadType);
                IScheduleExecutor<?> scheduleExecutor = (IScheduleExecutor<?>)thread;

                if (thread == null) { throw new RuntimeException("fail to get thread:" + threadTypename); }

                IScheduleData data = SerializeUtil.createByClassName(scheduleDataTypename);
                ISavedObject<?> savableData = (ISavedObject<?>)data;

                savableData.load(savedData);
                scheduleExecutor.schedule(data);

            } catch (Exception e) {
                EzDebug.error("fail to send schedule to client");
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
