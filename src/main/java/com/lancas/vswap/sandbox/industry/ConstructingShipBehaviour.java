package com.lancas.vswap.sandbox.industry;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.data.BlockPosAndState;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.subproject.mstandardized.CategoryRegistry;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.ServerOnlyBehaviour;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//todo it stores category name instead of Category, when reload something bad could happen
//even use Category, reload could cause something bad to happen
public class ConstructingShipBehaviour extends ServerOnlyBehaviour<ConstructingShipData> {
    @Override
    protected ConstructingShipData makeInitialData() {
        return new ConstructingShipData();
    }
    public void resetSchemeData(RRWChunkyShipSchemeData schemeData, boolean copy) {
        data.resetSchemeData(schemeData, copy);
    }
    //will change stack
    public boolean putMaterial(ItemStack stack, boolean simulate) {
        if (!(stack.getItem() instanceof MaterialStandardizedItem ms))
            return false;

        int toConsume = MaterialStandardizedItem.getCntByScale(data.getScale());
        int stackCnt = stack.getCount();
        if (stackCnt < toConsume) {
            return false;
        }

        String categoryName = MaterialStandardizedItem.getCategoryName(stack);
        if (putCategoryBlock(categoryName, simulate)) {
            if (!simulate) {
                stack.setCount(stackCnt - toConsume);
            }

            return true;
        }

        return false;
    }
    public void creativePutMaterial() {
        String anyUnConstructedCateName = data.unConstructed.keySet().stream().findAny().orElse(null);
        if (anyUnConstructedCateName == null) return;  //construction finished

        putCategoryBlock(anyUnConstructedCateName, false);
    }

    public boolean isCompleted() {
        if (data.unConstructed.isEmpty())
            return true;

        //for safe
        var unConstructedIt = data.unConstructed.entrySet().iterator();
        while (unConstructedIt.hasNext()) {
            var entry = unConstructedIt.next();
            String categoryName = entry.getKey();
            Queue<BlockPosAndState> q = entry.getValue();

            if (q.isEmpty()) {
                EzDebug.warn("unConstructed has empty queue of category:" + categoryName);
                unConstructedIt.remove();
            } else {
                return false;
            }
        }

        return true;
    }
    public RRWChunkyShipSchemeData getSchemeData() {
        return data.schemeData;
    }
    public void resetConstruction() {
        //data.resetSchemeData(data.schemeData, true);
        ship.getBlockCluster().getDataWriter().clear(true);

        data.unConstructed.clear();
        data.schemeData.foreachBlockInLocal((bp, state) -> {
            String categoryName = CategoryRegistry.getCategory(state.getBlock()).categoryName;
            var q = data.unConstructed.computeIfAbsent(categoryName, k ->new ConcurrentLinkedQueue<>());
            q.add(new BlockPosAndState(bp, state));
        });
    }

    protected boolean putCategoryBlock(String categoryName, boolean simulate) {  //todo don't use string as category key but a custom class
        //CategoryRegister.getCategory(categoryName);  //todo refresh category every game restart.

        var q = data.unConstructed.get(categoryName);
        if (q == null) {
            EzDebug.light("no such category to place:" + categoryName);
            return false;  //do such category  //todo maybe category is changed?
        }
        if (q.isEmpty()) {
            EzDebug.warn("the queue of unConstructed category:" + categoryName + ", is empty, which should be already removed");
            data.unConstructed.remove(categoryName);
            return false;
        }

        if (simulate) {
            return true;  //q is extracted and not null already. return turn if simulate
        }

        var constructTuple = q.poll();
        if (q.isEmpty()) {
            data.unConstructed.remove(categoryName);
        }

        BlockState prev = ship.getBlockCluster().getDataWriter().setBlock(JomlUtil.i(constructTuple.getBp()), constructTuple.getState());
        EzDebug.light("place blockState:" + constructTuple.getState());
        if (!prev.isAir()) {
            EzDebug.warn("the previous blockState of just put blockPos is not air, maybe put block at same pos");
        }
        return true;
    }

    @Override
    public void serverTick(ServerLevel level) { }
    /*@Override
    public void serverAsyncLogicTick() {
        //this method maybe slow, put it in async logic tick
        data.forceFlushAllConstructed();
    }*/

    @Override
    public Class<?> getDataType() { return ConstructingShipData.class; }
}
