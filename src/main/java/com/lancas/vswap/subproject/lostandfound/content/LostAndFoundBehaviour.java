package com.lancas.vswap.subproject.lostandfound.content;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.ShipUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//todo docker save adjust
public class LostAndFoundBehaviour extends BlockEntityBehaviour {
    public static final String UUID_KEY = "lost_and_found:uuid";
    public static ConcurrentHashMap<UUID, BlockPos> latestClaimBp = new ConcurrentHashMap<>();

    public static final BehaviourType<LostAndFoundBehaviour> TYPE = new BehaviourType<>();
    @Override
    public BehaviourType<?> getType() { return TYPE; }

    //@Nullable protected UUID uuid = null;
    //private UUID toClaimUuid = null;
    @Nullable protected UUID uuid = null;
    public @Nullable UUID getUuid() { return uuid; }

    public LostAndFoundBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        if (clientPacket) return;  //LostAndFound don't care about client side

        if (nbt.contains(UUID_KEY)) {
            //toClaimUuid = nbt.getUUID(UUID_KEY);
            uuid = nbt.getUUID(UUID_KEY);
            //latestClaimBp.put(uuid, getPos());  //don't set pos here: it sometimes set the localPos in cortaption
        }
        //EzDebug.log("read tag:" + nbt);
    }
    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        if (clientPacket) return;

        if (uuid == null) {
            //EzDebug.warn("when LostAndFound behaviour saving: uuid is null!");
            return;
        }

        nbt.putUUID(UUID_KEY, uuid);
        //EzDebug.log("write tag:" + nbt);
    }

    @Override
    public void tick() {
        super.tick();

        if (!(getWorld() instanceof ServerLevel level))
            return;

        /*if (uuid == null) {  //no uuid, claim one
            uuid = LostAndFound.claimUuid(level, getPos(), toClaimUuid);
        }*/
        if (uuid == null) {
            uuid = UUID.randomUUID();
            latestClaimBp.put(uuid, getPos());
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        Level level = getWorld();
        if (level == null)
            return;

        if (uuid != null) {
            //EzDebug.log("at " + getPos() + ", loaded?:" + level.isLoaded(getPos()) + ", ship:" + ShipUtil.getShipAt(level, getPos()));
            latestClaimBp.put(uuid, getPos());
        }
    }

    /*@Override
        public void destroy() {

        }*/
    public void onRemoveFromLevel() {
        //EzDebug.warn("Laf_Destroy with uuid:" + uuid + ", level:" + getWorld());

        /*Level level = getWorld();
        if (level == null) {
            EzDebug.error("the level is null when destroy the LostAndFound Behaviour!");
            return;
        }

        if (level.isClientSide)
            return;

        EzDebug.warn("notify destroy uuid:" + uuid);
        if (uuid != null) {
            LostAndFound.notifyDestroy((ServerLevel)level, uuid);
        }*/
    }


    /*public @Nullable BlockPos currentBlockPos() {
        if (!(getWorld() instanceof ServerLevel level))
            return null;

        if (uuid == null)
            return null;

        UuidState state = LostAndFound.getOrCreate(level).getState(uuid);
        if (state == null)
            return null;

        if (state.is(UuidState.State.Alive))
            return state.getBlockPos();

        return null;
    }*/

}
