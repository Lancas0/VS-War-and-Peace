package com.lancas.vswap.subproject.blockplusapi.exception;

import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;

public class NullBlockID extends RuntimeException {
    public NullBlockID(Class<? extends BlockPlus> type) {
        super("Block with type:" + type.getName() + " has null id!");
    }
}
