package com.lancas.vswap.content.recipe;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.world.item.ItemStack;

public abstract class SingleStackProcessHandler<T extends IProcessContext> implements IProcessHandlerOptimized<T> {
    protected float progress = 0;
    protected ItemStack processingStack = ItemStack.EMPTY;

    public ItemStack getProcessingStack() { return processingStack; }

    @Override
    public boolean isProcessing() {
        if (processingStack.isEmpty() && progress > 1E-4) {
            EzDebug.warn("processing stack is empty and progress is:" + progress);
            progress = 0;
            return false;
        }

        return !processingStack.isEmpty();
    }
    @Override
    public boolean canProcess() {
        if (processingStack.isEmpty() && progress > 1E-4) {
            EzDebug.warn("processing stack is empty and progress is:" + progress);
            progress = 0;
        }

        processingStack = processingStack.isEmpty() ? collectStackToProcess() : processingStack;
        return !processingStack.isEmpty();
    }

    @Override
    public float getCurrentProgress() { return progress; }
    @Override
    public float increaseAndGetProgress(float prog) { progress += prog; return progress; }

    @Override
    public void reset() { progress = 0; processingStack = ItemStack.EMPTY; }


    public abstract ItemStack collectStackToProcess();
    @Override
    public abstract void onComplete();
    @Override
    public abstract boolean shouldLazyWork(T ctx);
}
