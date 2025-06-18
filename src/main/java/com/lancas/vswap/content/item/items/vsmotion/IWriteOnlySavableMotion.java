package com.lancas.vswap.content.item.items.vsmotion;

import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWriteOnlySavableMotion extends INBTSerializable<CompoundTag> {
    public void addFrame(ITransformPrimitive transform);
    public boolean isEmpty();
}
