package com.lancas.vswap.subproject.sandbox.thread.api;

import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;

public interface ISandBoxThread<TDest extends ISandBoxWorld> {
    //public void initial(TDest inWorld);
    public void start();
    public void pause();
}
