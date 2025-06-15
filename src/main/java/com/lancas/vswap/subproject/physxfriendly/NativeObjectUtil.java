package com.lancas.vswap.subproject.physxfriendly;

/*
import kotlin.Function;
import org.lwjgl.system.MemoryStack;
import physx.NativeObject;
import physx.common.PxVec3;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class NativeObjectUtil {
    //allocator, Allocate
    public <T extends NativeObject> void withNativeDo(BiFunction<MemoryStack, NativeObject.Allocator<MemoryStack>, T> creator, Consumer<T> consumer) {
        try (MemoryStack mem = MemoryStack.stackPush()) {
            // create an object of PxVec3. The native object is allocated in memory
            // provided by MemoryStack

            T nativeObj = creator.apply(mem, MemoryStack::nmalloc);
            consumer.accept(nativeObj);
        }
    }
}
*/