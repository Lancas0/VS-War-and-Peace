package com.lancas.vswap.event.listener;

import java.util.function.Consumer;

public abstract class CancelableMonoListener<T> implements Consumer<T>, ICancelableListener { }