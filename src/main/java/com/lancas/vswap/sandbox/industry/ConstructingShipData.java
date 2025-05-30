package com.lancas.vswap.sandbox.industry;

import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.data.BlockPosAndState;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.subproject.mstandardized.CategoryRegistry;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConstructingShipData implements IComponentData<ConstructingShipData> {
    protected RRWChunkyShipSchemeData schemeData;
    //key: categoryName, value: remain count
    //protected Map<String, Integer> remainConstructionData = new HashMap<>();  //todo safe check if there is a zero value
    //protected Queue<String> toFlushConstructedCateName = new ConcurrentLinkedQueue<>();
    //protected List<TriTuple<BlockPos, BlockState, CompoundTag>> unConstructed = Collections.synchronizedList(new ArrayList<>());
    protected ConcurrentHashMap<String, ConcurrentLinkedQueue<BlockPosAndState>> unConstructed = new ConcurrentHashMap<>();

    protected ConstructingShipData() { }
    public ConstructingShipData(RRWChunkyShipSchemeData inSchemeData, boolean copy) {
        resetSchemeData(inSchemeData, copy);
    }
    /*public static BuildingShipData copySchemeData(RRWChunkyShipSchemeData inSchemeData) {
        return new BuildingShipData(new RRWChunkyShipSchemeData().load(inSchemeData.saved()));
    }
    public static BuildingShipData ofSchemeData(RRWChunkyShipSchemeData inSchemeData) {
        return new BuildingShipData(inSchemeData);
    }*/
    public void resetSchemeData(RRWChunkyShipSchemeData inSchemeData, boolean copy) {
        RRWChunkyShipSchemeData toSetSchemeData;
        if (copy) {
            toSetSchemeData = new RRWChunkyShipSchemeData().load(inSchemeData.saved());
        } else {
            toSetSchemeData = inSchemeData;
        }

        schemeData = toSetSchemeData;

        /*remainConstructionData.clear();
        toSetSchemeData.foreachBlockInLocal((bp, state) -> {
            @NotNull Category category = CategoryRegister.getCategory(state);
            Integer prev = remainConstructionData.get(category.categoryName);
            if (prev == null)
                prev = 0;

            remainConstructionData.put(category.categoryName, prev + 1);
        });

        toFlushConstructedCateName.clear();*/
        //schemeData.getCopyOfAllBlocksOverwrite(unConstructed);
        unConstructed.clear();
        schemeData.foreachBlockInLocal((bp, state) -> {
            String categoryName = CategoryRegistry.getCategory(state.getBlock()).categoryName;
            var q = unConstructed.computeIfAbsent(categoryName, k ->new ConcurrentLinkedQueue<>());
            q.add(new BlockPosAndState(bp, state));
        });
    }

    public double getScale() { return schemeData.getScale().x(); }

    /*protected void forceFlushAllConstructed() {  //todo remake unConstructed and remain constructed if there is a toFlush don't match::maybe categories changed
        if (unConstructed.isEmpty())
            return;  //construction is finished

        String polledCategoryName = toFlushConstructedCateName.poll();
        while (polledCategoryName != null) {
            boolean success = false;
            for (int i = unConstructed.size() - 1; i >= 0; --i) {
                BlockState curState = unConstructed.get(i).getSecond();
                Category category = CategoryRegister.getCategory(curState);
                if () {

                }
            }


            polledCategoryName = toFlushConstructedCateName.poll();
        }
    }*/

    @Override
    public ConstructingShipData copyData(ConstructingShipData src) {
        schemeData = src.schemeData;

        /*remainConstructionData.clear();
        remainConstructionData.putAll(src.remainConstructionData);

        toFlushConstructedCateName.clear();
        toFlushConstructedCateName.addAll(src.toFlushConstructedCateName);*/

        unConstructed.clear();
        unConstructed = src.unConstructed;  //todo deep copy?
        return this;
    }
    @Override
    public CompoundTag saved() {
        //forceFlushAllConstructed();

        NbtBuilder builder = new NbtBuilder()
            .putCompound("scheme_data", schemeData.saved())
            /*.putMap("remain_construction", remainConstructionData, (cName, cnt) -> {
                CompoundTag t = new CompoundTag();
                t.putString("key", cName);
                t.putInt("val", cnt);
                return t;
            })
            .putEach("to_flush", toFlushConstructedCateName, NbtBuilder::tagOfString)*/
            //.putEach("unconstructed", unConstructed, NbtBuilder::tagOfBlock);
            .putMap("unconstructed", unConstructed, (cateName, q) ->
                new NbtBuilder()
                    .putString("key", cateName)
                    .putEach("queue", q, BlockPosAndState::saved)
                    .get()
            );
        return builder.get();
    };
    @Override
    public IComponentData<ConstructingShipData> load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readCompoundDo("scheme_data", t -> { schemeData = new RRWChunkyShipSchemeData(); schemeData.load(t); })
            /*.readMapOverwrite("remain_construction", t ->
                new BiTuple<String, Integer>(t.getString("key"), t.getInt("val"))
            , remainConstructionData)
            .readEachCompoundOverwrite("to_flush", NbtBuilder::stringOf, toFlushConstructedCateName)
            .readEachCompoundOverwrite("unconstructed", NbtBuilder::blockOf, unConstructed);*/
            .readMapOverwrite("unconstructed", t -> {
                Dest<String> key = new Dest<>();
                ConcurrentLinkedQueue<BlockPosAndState> q = new ConcurrentLinkedQueue<>();

                NbtBuilder.modify(t)
                    .readString("key", key)
                    .readEachCompoundOverwrite("queue", BlockPosAndState::new, q);

                return new BiTuple<>(key.get(), q);
            }, unConstructed);
        return this;
    }
}
