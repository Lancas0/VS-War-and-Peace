package com.lancas.vswap.subproject.blockplusapi.exception;

import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;

public class NoSuchBlock extends RuntimeException {
    public NoSuchBlock(Class<? extends BlockPlus> type) {
        super("No such block:" + type.getName());
    }
}
