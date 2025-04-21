package com.lancas.vs_wap.subproject.blockplusapi.exception;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;

public class NoSuchBlock extends RuntimeException {
    public NoSuchBlock(Class<? extends BlockPlus> type) {
        super("No such block:" + type.getName());
    }
}
