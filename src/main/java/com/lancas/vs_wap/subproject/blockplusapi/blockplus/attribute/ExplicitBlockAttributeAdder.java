package com.lancas.vs_wap.subproject.blockplusapi.blockplus.attribute;

/*
import com.lancas.einherjar.subproject.blockplusapi.blockplus.adder.blockitem.IBlockItemAdderSupplier;
import com.lancas.einherjar.subproject.blockplusapi.itemplus.ItemHoverTextAppender;
import com.lancas.einherjar.subproject.blockplusapi.itemplus.ItemAdder;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Function;

public class ExplicitBlockAttributeAdder<T> extends BlockAttributeAdder<T> implements IBlockItemAdderSupplier {
    private final Function<BlockState, T> attributeGetter;
    public ExplicitBlockAttributeAdder(Function<BlockState, T> inGetter) {
        attributeGetter = inGetter;
    }

    @Override
    public T getAttribute(BlockState state) {
        if (attributeGetter == null)
            return null;
        return attributeGetter.apply(state);
    }

    @Override
    public void supplyItemAdders(List<ItemAdder> adderList) {
        adderList.add(new ItemHoverTextAppender(
            (stack, level, components, flag) -> {
                components.
            }
        ));
    }
}
*/