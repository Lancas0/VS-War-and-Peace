package com.lancas.vswap.subproject.lostandfound;

import com.lancas.vswap.subproject.sandbox.api.ISavedObject;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class UuidState implements ISavedObject<UuidState> {
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putEnum("state", state)
            .putBlockPos("bp", blockPos)
            .putIfNonNull("involve_uuid", involveUuid, NbtBuilder::putUUID)
            .putInt("count_down", waitCountDown)
            .get();
    }
    @Override
    public UuidState load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readEnumDo("state", State.class, v -> state = v)
            .readBlockPosDo("bp", v -> blockPos = v)
            .readDoIfExist("involve_bp", v -> involveUuid = v, NbtBuilder::getUUID)
            .readIntDo("count_down", v -> waitCountDown = v);
        return this;
    }


    public enum State {
        Alive, Missing, Dead, WaitAndSee, Forget
        //Alive      : the uuid is held by a BE
        //Missing    : the BE holding this uuid is just removed
        //Dead       : the BE holding this uuid removed long ago and no one hold this uuid
        //WaitAndSee : there is a BE trying to hold an alive uuid
        //Forget     : no one care the uuid, BE or beHolder, will be removed next tick or something
    }

    //protected List<BlockPos> involvingBp = new ArrayList<>();
    private State state;
    private BlockPos blockPos;
    //private BlockPos involveBp = null;
    private UUID involveUuid = null;
    private int waitCountDown = 0;

    protected UuidState(State inState, BlockPos inBp) {
        state = inState;
        blockPos = inBp;
    }
    public UuidState(CompoundTag tag) { load(tag); }

    public static UuidState Alive(BlockPos blockPos) {
        return new UuidState(State.Alive, blockPos);
    }

    public boolean is(State targetState) { return state == targetState; }
    public State getState() { return state; }
    public UUID getInvolvedUuid() { return involveUuid; }
    public BlockPos getBlockPos() { return blockPos; }


    public UuidState setAlive(BlockPos bp) {
        state = State.Alive;
        blockPos = bp;
        involveUuid = null;
        waitCountDown = 0;
        return this;
    }
    public UuidState setMissing() {
        state = State.Missing;
        involveUuid = null;
        waitCountDown = 10;  //lazied count down mean after how many lazyTicks(default 10tick pre lazyTick) state will be DEAD
        return this;
    }
    public UuidState setWaitAndSee(UUID involved) {
        state = State.WaitAndSee;
        involveUuid = involved;
        waitCountDown = 10;  //lazied count down mean after how many lazyTicks(default 10tick pre lazyTick) state will be ALIVE
        return this;
    }
    public UuidState setDead() {
        state = State.Dead;
        involveUuid = null;
        waitCountDown = 0;
        return this;
    }
    /*public UuidState missingCountDown(Dest<Boolean> isDead) {
        if (state != State.Missing) {
            EzDebug.warn("state is not Missing, should be called missingCountDown");
            return this;
        }

        if (--laziedCountDown <= 0) {
            isDead.set(true);
            state = State.Dead;
        }
        return this;
    }*/
    public void countDown(int ticks) {
        switch (state) {
            case Missing -> {
                waitCountDown -= ticks;
                if (waitCountDown <= 0)
                    setDead();
            }
            case WaitAndSee -> {
                waitCountDown -= ticks;
                if (--waitCountDown <= 0)
                    setAlive(blockPos);
            }
        }
    }
    //Forget have no constructor
}
