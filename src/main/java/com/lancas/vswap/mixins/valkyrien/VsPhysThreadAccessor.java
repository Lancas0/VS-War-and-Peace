package com.lancas.vswap.mixins.valkyrien;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.valkyrienskies.core.impl.shadow.Ap;
import org.valkyrienskies.core.impl.shadow.At;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(Ap.class)
public interface VsPhysThreadAccessor {
    @Accessor(remap = false)
    public boolean getD();

    @Accessor(remap = false)
    public At getB();

    @Accessor(remap = false)
    public ReentrantLock getH();

    @Accessor(remap = false)
    public int getG();
    @Accessor(remap = false)
    public void setG(int g);

    @Accessor(remap = false)
    public Condition getI();

    @Accessor(remap = false)
    public Condition getJ();

    @Accessor(remap = false)
    public long getE();
    @Accessor(remap = false)
    public void setE(long nano);

    @Accessor(remap = false)
    public Queue<Long> getF();

    @Accessor(remap = false)
    public ReentrantLock getK();

    @Accessor(remap = false)
    public Condition getL();

    @Accessor(remap = false)
    public int getC();


}
