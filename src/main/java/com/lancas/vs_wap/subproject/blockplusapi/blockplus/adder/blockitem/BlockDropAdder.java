package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.blockitem;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.storage.loot.LootPool;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockDropAdder implements IBlockAdder {
    //not working now
    public final float causeFoodExhaustion;
    public final boolean cancelVanillaDrop;
    //not working now
    public final boolean dropXp;
    //private final QuadConsumer<Level, Player, TriTuple.BlockTuple, ItemStack> onMined;
    //level, player, blockTuple, mineWithStack, dest
    //private final PentaConsumer<Level, Player, TriTuple.BlockTuple, ItemStack, List<ItemStack>> dropsAppender;
    //private final List<ItemStack> dropsCache = new ArrayList<>();
    //private final PentaConsumer<>

    public BlockDropAdder(@NotNull BlockEntry<? extends BlockPlus> block,  float causeFoodExhaustion, boolean cancelVanillaDrop, boolean dropXp, @NotNull List<LootPool> appendDrops) {
        this.causeFoodExhaustion = causeFoodExhaustion;
        this.cancelVanillaDrop = cancelVanillaDrop;
        this.dropXp = dropXp;

        //block.get().getD

        //BlockDropEvent.addDrops(block.getId(), cancelVanillaDrop, appendDrops);
    }


    @Override
    public float foodExhaustionOnMinedAppend() { return causeFoodExhaustion; }
    @Override
    public boolean cancelVanillaItemDrop() { return cancelVanillaDrop; }
    @Override
    public boolean dropXpOnMinedAppend() { return dropXp; }


    /*@Override
    public void onPlayerMined(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack minedWithStack) {
        if (level.isClientSide) return;
        if (dropsAppender == null) return;

        dropsCache.clear();
        dropsAppender.apply(level, player, new TriTuple.BlockTuple(pos, state, be), minedWithStack, dropsCache);
        if (dropsCache.isEmpty()) return;

        for (ItemStack stack : dropsCache) {
            Block.popResource(level, pos, stack);
            //do not drop xp because it's handled in blockplus
        }
    }*/
}
