package com.lancas.vswap.subproject.sandbox.obsolete;

/*
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.network.NetSerializeUtil;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vs_wap.subproject.sandbox.ship.ScheduleShipData;
import com.lancas.vs_wap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;
import java.util.function.Supplier;

public class SendScheduleClientShipS2C {
    private final String levelName;
    private final CompoundTag dataNbt;

    public SendScheduleClientShipS2C(ServerLevel levelSendFrom, ScheduleShipData scheduleClientShipMadeInServer) {
        levelName = VSGameUtilsKt.getDimensionId(levelSendFrom);
        dataNbt = new CompoundTag();

        byte[] bytes = SerializeUtil.safeSerialize(scheduleClientShipMadeInServer.getCallback());
        if (bytes == null || bytes.length == 0) {
            EzDebug.warn("fail to serialize callback in network nbt");
            //scheduleClientShipMadeInServer.chaseUp(levelSendFrom);
        } else {
            dataNbt.putByteArray("callback_bytes", bytes);
        }

        dataNbt.put("ship", NetSerializeUtil.serializeShipForSendToClient(scheduleClientShipMadeInServer.ship));
        dataNbt.putInt("remain_tick", scheduleClientShipMadeInServer.getRemainTick());
        dataNbt.putBoolean("canceled", scheduleClientShipMadeInServer.isCanceled());  //todo maybe if it's already canceled, don't send
    }
    private SendScheduleClientShipS2C(String inLevelName, CompoundTag nbt) {
        levelName = inLevelName;
        dataNbt = nbt;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(levelName).writeNbt(dataNbt);
    }
    public static SendScheduleClientShipS2C decode(FriendlyByteBuf buffer) {
        return new SendScheduleClientShipS2C(buffer.readUtf(), buffer.readNbt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;

            if (!Objects.equals(clientWorld.getCurLevelName(), levelName)) {
                return;  //no need for sync because it is not in current level
            }
            //EzDebug.log("remove render:" + uuid);

            SandBoxClientShip ship = NetSerializeUtil.deserializeAsClientShip(dataNbt.getCompound("ship"));
            int remainTick = dataNbt.getInt("remain_tick");
            boolean canceled = dataNbt.getBoolean("canceled");

            ScheduleShipData.ScheduleCallback callback = null;
            if (dataNbt.contains("callback_bytes"))
                callback = SerializeUtil.safeDeserialize(dataNbt.getByteArray("callback_bytes"));

            //clientWorld.addClientShip(NetSerializeUtil.deserializeAsClientShip(dataNbt));  //todo add ship
            clientWorld.scheduleShipOverwriteIfExisted(new ScheduleShipData(
                ship, remainTick, callback  //todo handle canceled
            ));
        });
        ctx.get().setPacketHandled(true);
    }
}
*/