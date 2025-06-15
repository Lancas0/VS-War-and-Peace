package com.lancas.vswap.subproject.sandbox.thread.impl;

import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.thread.api.ISandBoxThread;

import java.util.Timer;
import java.util.TimerTask;

public abstract class TimerBasedThread<TWorld extends ISandBoxWorld<?>> implements ISandBoxThread<TWorld> {
    //protected TWorld world;
    //public TWorld getWorld() { return world; }

    protected final Timer timer = new Timer(getTimerName());
    protected volatile boolean started = false;
    protected volatile boolean pausing = false;


    /*@Override
    //public void initial(TWorld inWorld) {
        world = inWorld;
    }*/
    @Override
    public void start() {
        pausing = false;

        if (!started) {
            timer.scheduleAtFixedRate(getTimerTask(), 0, getTimerPeriodMS());
            started = true;
        }
    }
    @Override
    public void pause() { pausing = true; }

    protected abstract String getTimerName();
    protected abstract TimerTask getTimerTask();
    protected abstract int getTimerPeriodMS();
}
