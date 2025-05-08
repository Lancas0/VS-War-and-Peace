package com.lancas.vs_wap.event.listener;

import java.util.function.Consumer;

public abstract class CancelableMonoListener<T> implements Consumer<T>, ICancelableListener { }