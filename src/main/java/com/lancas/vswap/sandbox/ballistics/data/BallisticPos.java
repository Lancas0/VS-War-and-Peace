package com.lancas.vswap.sandbox.ballistics.data;

import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class BallisticPos implements INBTSerializable<CompoundTag> {
    private final Vector3i localPos = new Vector3i();
    private int fromHead;
    private int fromTail;

    public Vector3ic localPos() {
        return localPos;
    }

    public int fromHead() {
        return fromHead;
    }

    public int fromTail() {
        return fromTail;
    }

    public BallisticPos(Vector3i inLocalPos, int inFromHead, int inFromTail) {
        localPos.set(inLocalPos);
        fromHead = inFromHead;
        fromTail = inFromTail;
    }

    public BallisticPos(CompoundTag tag) {
        deserializeNBT(tag);
    }

    @Override
    public CompoundTag serializeNBT() {
        return new NbtBuilder()
            .putVector3i("local_pos", localPos)
            .putInt("from_head", fromHead)
            .putInt("from_tail", fromTail)
            .get();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readVector3i("local_pos", localPos)
            .readIntDo("from_head", v -> fromHead = v)
            .readIntDo("from_tail", v -> fromTail = v);
    }
}