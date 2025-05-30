package com.lancas.vswap.content.recipe;

public interface IProcessHandler<T extends IProcessContext> {
    public boolean canProcess();

    public float getCurrentProgress();
    public float increaseAndGetProgress(float prog);

    public void reset();
    public void onComplete();

    public default void tick(T ctx) {
        if (!canProcess())
            return;

        float prog = increaseAndGetProgress(ctx.getTickProgression());
        if (prog >= 1f) {
            onComplete();
            reset();
        }
    }
}
