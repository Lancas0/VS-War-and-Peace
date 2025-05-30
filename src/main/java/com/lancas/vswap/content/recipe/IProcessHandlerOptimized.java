package com.lancas.vswap.content.recipe;

public interface IProcessHandlerOptimized<T extends IProcessContext> extends IProcessHandler<T> {
    public boolean isProcessing();
    @Override
    public boolean canProcess();

    @Override
    public float getCurrentProgress();
    @Override
    public float increaseAndGetProgress(float prog);

    @Override
    public void reset();
    @Override
    public void onComplete();

    public boolean shouldLazyWork(T ctx);

    public default void tick(T ctx) {
        if (shouldLazyWork(ctx)) {
            if (!canProcess())
                return;
        }

        if (!isProcessing())
            return;

        float prog = increaseAndGetProgress(ctx.getTickProgression());
        if (prog >= 1f) {
            onComplete();
            reset();
        }
    }
}
