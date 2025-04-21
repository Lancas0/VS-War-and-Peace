package com.lancas.vs_wap.subproject.blockplusapi.blockplus.attribute;

/*
public class BlockInfoAdderImpl extends BlockInfoAdder implements IBlockItemAdderSupplier {
    private final String name;
    private final Function<BlockState, Object> attributeGetter;
    private final Function<Object, String> formatter;
    private final boolean explicit;

    public BlockInfoAdderImpl(String inName, Function<BlockState, Object> inGetter, Function<Object, String> inFormatter, boolean inExplicit) {
        name = inName;
        attributeGetter = inGetter;
        formatter = inFormatter;
        explicit = inExplicit;
    }

    @Override
    public <T> T getInfoValue(BlockState state) {
        if (attributeGetter == null)
            return null;
        return (T)attributeGetter.apply(state);
    }
    @Override
    public void supplyItemAdders(List<ItemAdder> adderList) {
        if (explicit) {
            adderList.add(new ItemHoverTextAppender(
                (stack, level, components, tooltipFlag) -> {
                    BlockState state = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
                    Object infoVal = getInfoValue(state);
                    StringBuilder textBuilder = new StringBuilder()
                        .append(name)
                        .append(": ")
                        .append(formatter == null ? infoVal.toString() : formatter.apply(infoVal));

                    components.add(Component.literal(textBuilder.toString()));
                }
            ));
        }
    }
}*/