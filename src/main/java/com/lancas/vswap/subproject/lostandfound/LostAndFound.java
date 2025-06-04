package com.lancas.vswap.subproject.lostandfound;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class LostAndFound extends SavedData {
    public static void debugAllStates(ServerLevel level) {
        EzDebug.logs(getOrCreate(level).uuidStates, (k, v) -> "key:" + k + ", state:" + v.getState() + ", at " + v.getBlockPos().toShortString());
    }
    public static LostAndFound getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            t -> new LostAndFound().load(t),
            LostAndFound::new,
            VsWap.MODID + "_lost_and_found"
        );
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return new NbtBuilder()
            .putStream(
                "states",

                uuidStates.entrySet().stream()
                    .filter(e -> !e.getValue().is(UuidState.State.Forget)),

                e -> new NbtBuilder()
                    .putUUID("uuid", e.getKey())
                    .putCompound("state", e.getValue().saved())
                    .get()
            ).get();
    }
    public LostAndFound load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("states",
                t -> {
                    UUID uuid = t.getUUID("uuid");
                    UuidState state = new UuidState(t.getCompound("state"));
                    return new BiTuple<>(uuid, state);
                }, uuidStates);

        return this;
    }



    //public static final String UUID_KEY = "lost_and_found:uuid";
    //protected ConcurrentHashMap<BlockPos, UUID> uuidRecords = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<UUID, UuidState> uuidStates = new ConcurrentHashMap<>();
    private final UuidStateMachine<LostAndFound> stateMachine = new UuidStateMachine<>(this);

    /*static {
        LostAndFoundEvent.preBlockChangeEvt.addListener((l, bp, oldState, newState) -> {
            if (!(l instanceof ServerLevel level))
                return;
            LostAndFound laf = LostAndFound.getOrCreate(level);

            BlockEntity preBe = l.getBlockEntity(bp);
            if (preBe == null) {
                if (laf.uuidRecords.containsKey(bp)) {
                    EzDebug.warn("the be at " + bp.toShortString() + " is empty, why there is a uuid record in this bp?");
                    UUID exceptionalUuid = laf.uuidRecords.remove(bp);
                    laf.uuidStates.remove(exceptionalUuid);
                }
                return;
            }

            UUID preUuid = laf.uuidRecords.get(bp);
            if (preUuid == null)
                return;  //nothing to do - be may be not tracked be

            UuidState state = laf.uuidStates.get(preUuid);

        });
    }*/

    public @NotNull static UUID claimUuid(ServerLevel level, BlockPos bp, @Nullable UUID savedUuid) {
        UUID claimed = getOrCreate(level).claimUuid(bp, savedUuid);
        //EzDebug.highlight("at " + bp.toShortString() + " claim with savedUuid:" + savedUuid + " and claim:" + claimed);
        return claimed;
    }
    public @NotNull UUID claimUuid(BlockPos bp, @Nullable UUID savedUuid) {
        if (savedUuid == null) {
            EzDebug.highlight("claim by spawn new one!");
            return spawnNewUuidAndSetAlive(bp);
        }

        UuidState savedState = uuidStates.get(savedUuid);
        if (savedState == null) {
            EzDebug.highlight("claim by spawn new one!");
            return spawnNewUuidAndSetAlive(bp);
        }

        if (savedState.getBlockPos().equals(bp)) {
            //I guess sometimes(be save/load, chunk load) will cause claim self
            if (!savedState.is(UuidState.State.Alive)) {
                //some time dead:
                EzDebug.error("claim self but find the state is not alive! state:" + savedState.getState());  //todo handle expectional: if not WaitAndSee, make it alive
            }
            return savedUuid;
        }
        /*if (savedState.state == UuidState.State.Forget) {
            EzDebug.warn("Forget state should not be saved!");
            return spawnNewUuidAndSetAlive(bp);
        }*/

        //not forgotten savedState
        return switch (savedState.getState()) {
            case Forget -> {
                EzDebug.warn("Forget state should not be saved!");
                yield spawnNewUuidAndSetAlive(bp);
            }
            case Dead -> { //if state is dead(no be have it but still some holder try to access it)
                EzDebug.highlight("claim by spawn new one because prev with same uuid is dead");
                yield spawnNewUuidAndSetAlive(bp);
            }
            case Alive -> {
                EzDebug.highlight("claim by spawn new one and wait-and-see");

                UUID newUuid = spawnNewUuidAndSetAlive(bp);
                savedState.setWaitAndSee(newUuid);

                yield newUuid;  //maybe will be replaced later
            }
            case Missing -> {
                EzDebug.highlight("claim a missing one!");
                //I can claim it!
                savedState.setAlive(bp);
                yield savedUuid;
            }
            case WaitAndSee -> {
                EzDebug.error("multiple block try to claim a waitAndSee uuid!");
                yield spawnNewUuidAndSetAlive(bp);
            }
        };
    }
    private UUID spawnNewUuidAndSetAlive(BlockPos bp) {
        UUID newUuid = UUID.randomUUID();
        uuidStates.put(newUuid, UuidState.Alive(bp));
        return newUuid;
    }

    protected void reclaimWaitAndSee(UuidState waitAndSeeState) {
        if (!waitAndSeeState.is(UuidState.State.WaitAndSee)) {
            EzDebug.error("[reclaimPreviousWaitAndSee] the waitAndSee is not waitAndSee!");
            return;
        }

        UUID involvedUuid = waitAndSeeState.getInvolvedUuid();
        if (involvedUuid == null) {
            EzDebug.error("[reclaimPreviousWaitAndSee] the involvedBp of waitAndSee is null!");
            return;
        }

        UuidState involvedState = uuidStates.get(involvedUuid);
        if (involvedState == null) {
            EzDebug.error("[reclaimPreviousWaitAndSee] can't find involvedState!");
            return;
        }

        if (!involvedState.is(UuidState.State.Alive)) {
            EzDebug.warn("[reclaimPreviousWaitAndSee] involvedState has state " + involvedState.getState() + ", which is not expected, anyway it will soon be destroyed");
        }
        //remove the involvedState and set waitAndSee's block to the invovled one's

        EzDebug.highlight("reclaim a wait and see at " + waitAndSeeState.getBlockPos().toShortString());
        uuidStates.remove(involvedUuid);
        waitAndSeeState.setAlive(involvedState.getBlockPos());
    }


    public static void notifyDestroy(@NotNull ServerLevel level, @NotNull UUID uuid) {
        getOrCreate(level).notifyDestroy(uuid);
    }
    public void notifyDestroy(@NotNull UUID uuid) {
        UuidState state = uuidStates.get(uuid);
        if (state == null) {
            EzDebug.warn("can't find to destroy uuid:" + uuid);
            return;
        }

        stateMachine.onDestroyHandler.get(state.getState()).accept(state);
    }



    /*public static @Nullable UUID getUuid(@NotNull BlockEntity be) {
        CompoundTag beNbt = be.saveWithoutMetadata();
        if (!beNbt.contains(UUID_KEY))
            return null;

        return beNbt.getUUID(UUID_KEY);
    }
    public static void setUuid(@NotNull BlockEntity be) {
        be.load
    }*/
    //public ConcurrentHashMap<UUID, UuidState> uuidStates = new ConcurrentHashMap<>();


    private static final int lazyTickCnt = 4;
    private static LazyTicks lazyTicks = new LazyTicks(lazyTickCnt);
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        if (!lazyTicks.shouldWork())
            return;

        event.getServer().getAllLevels().forEach(l -> {
            LostAndFound laf = getOrCreate(l);

            laf.uuidStates.values().forEach(u -> u.countDown(lazyTickCnt));
        });
    }


    //todo temp. to use uuidHolder
    public @Nullable UuidState getState(@NotNull UUID uuid) {
        //if (!uuidStates.containsKey(uuid))
        //    EzDebug.warn("no such key:" + uuid);

        return uuidStates.get(uuid);
    }

}
