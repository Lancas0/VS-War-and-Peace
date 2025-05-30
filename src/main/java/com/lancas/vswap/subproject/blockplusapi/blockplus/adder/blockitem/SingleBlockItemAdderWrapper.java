package com.lancas.vswap.subproject.blockplusapi.blockplus.adder.blockitem;

import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleBlockItemAdderWrapper implements IBlockAdder, IBlockItemAdderSupplier {
    private final ItemAdder adder;
    public SingleBlockItemAdderWrapper(@NotNull ItemAdder inAdder) {
        adder = inAdder;
    }

    @Override
    public void supplyItemAdders(List<ItemAdder> adderList) { adderList.add(adder); }
}
