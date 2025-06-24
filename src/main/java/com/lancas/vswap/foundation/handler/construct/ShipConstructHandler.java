package com.lancas.vswap.foundation.handler.construct;


import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.TriTuple;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.api.Immutable;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.ship.helper.LazyShip;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.subproject.mstandardized.CategoryRegistry;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.lancas.vswap.util.*;
import edn.stratodonut.trackwork.tracks.blocks.TrackBaseBlock;
import edn.stratodonut.trackwork.tracks.blocks.WheelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBi;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ShipConstructHandler implements INBTSerializable<CompoundTag> {
    public ConcurrentHashMap<String, ConcurrentLinkedQueue<TriTuple.SavedBlockTuple>> toConstruct = new ConcurrentHashMap<>();
    protected long constructingShipId = -1;
    protected final Vector3d scale = new Vector3d();
    protected final Vector3d bottomCenterPivot = new Vector3d();
    protected final AABBi constructSize = new AABBi();



    protected LazyShip lazyConstructingShip = new LazyShip(
        (l, owner) ->
            ShipUtil.getShipByID(l, ((ShipConstructHandler)owner).constructingShipId)
    );

    public ShipConstructHandler(RRWChunkyShipSchemeData schemeData, Vector3d inBottomCenterPivot) {
        bottomCenterPivot.set(inBottomCenterPivot);
        scale.set(schemeData.getScale());
        constructSize.set(schemeData.getLocalAabbContainsShape());

        toConstruct.clear();
        //EzDebug.log("schemeData is Empty?:" + schemeData.isEmpty());
        schemeData.foreachBlockInLocal((bp, state, beNbt) -> {
            //EzDebug.log("schemeData has block:" + StrUtil.getBlockName(state));
            String categoryName = CategoryRegistry.getCategory(state.getBlock()).categoryName;
            toConstruct
                .computeIfAbsent(categoryName, k ->new ConcurrentLinkedQueue<>())
                .add(new TriTuple.SavedBlockTuple(bp, state, beNbt));
        });
    }
    public ShipConstructHandler(CompoundTag saved) {
        deserializeNBT(saved);
    }

    public boolean isCompleted() {
        if (toConstruct.isEmpty())
            return true;

        var qIt = toConstruct.values().iterator();
        while (qIt.hasNext()) {
            var q = qIt.next();

            if (!q.isEmpty())
                return false;

            //q is empty
            EzDebug.warn("there is a empty queue in toConstruct, which should already be removed");
            qIt.remove();
        }

        return true;
    }

    public double getScale() { return scale.x; }  //todo 3d scale?


    public ItemStack putMaterial(ServerLevel level, ItemStack material, boolean simulate) {
        //ItemStack inStack = stackImmutable.getIn();

        if (!(material.getItem() instanceof MaterialStandardizedItem ms)) {
            return material;
        }

        int toConsume = MaterialStandardizedItem.getCntByScale(getScale());
        int stackCnt = material.getCount();
        if (stackCnt < toConsume) {
            return material;
        }

        String categoryName = MaterialStandardizedItem.getCategoryName(material);
        if (putCategoryBlock(level, categoryName, simulate)) {
            return material.copyWithCount(stackCnt - toConsume);
        }

        return material;
    }
    public boolean creativePutMaterial(ServerLevel level) {
        String anyUnConstructedCateName = toConstruct.keySet().stream().findAny().orElse(null);
        if (anyUnConstructedCateName == null) return false;  //supposed construction finished

        putCategoryBlock(level, anyUnConstructedCateName, false);
        return true;
    }



    protected boolean putCategoryBlock(ServerLevel level, String categoryName, boolean simulate) {  //todo don't use string as category key but a custom class
        //CategoryRegister.getCategory(categoryName);  //todo refresh category every game restart.

        var q = toConstruct.get(categoryName);
        if (q == null) {
            //EzDebug.light("no such category to place:" + categoryName);
            //EzDebug.logs(toConstruct, (k, v) -> k);
            return false;  //do such category  //todo maybe category is changed?
        }
        if (q.isEmpty()) {
            EzDebug.warn("the queue of unConstructed category:" + categoryName + ", is empty, which should be already removed");
            toConstruct.remove(categoryName);
            return false;
        }

        if (simulate) {
            return true;
        }

        TriTuple.SavedBlockTuple constructTuple = q.poll();
        if (q.isEmpty()) {
            toConstruct.remove(categoryName);
        }

        ServerShip constructing = getConstructingShip(level);

        BlockPos origin = RRWChunkyShipSchemeData.getOriginInShipForScheme(level, constructing);

        CompoundTag toLoadBeNbt;
        if (constructTuple.getBeNbt() != null) {
            NbtBuilder toLoadBeNbtBuilder = NbtBuilder.copy(constructTuple.getBeNbt());
            //todo this is temp compact, to remove later
            if (ModList.get().isLoaded("trackwork")) {
                Block block = constructTuple.getBlockState().getBlock();
                if ((block instanceof WheelBlock) || (block instanceof TrackBaseBlock)) {
                    toLoadBeNbtBuilder
                        .putBoolean("Assembled", false)
                        .remove("trackBlockID");
                        //.putFloat("Speed", 0f);
                }
            }
            toLoadBeNbt = toLoadBeNbtBuilder.get();
        } else {
            toLoadBeNbt = null;
        }


        ShipBuilder.modify(level, constructing)
                .addBlockAtActual(
                    origin.offset(constructTuple.getBlockPos()),
                    constructTuple.getBlockState(),
                    toLoadBeNbt
                );

        /*BlockState prev = ship.getBlockCluster().getDataWriter().setBlock(JomlUtil.i(constructTuple.getBp()), constructTuple.getState());
        EzDebug.light("place blockState:" + constructTuple.getState());
        if (!prev.isAir()) {
            EzDebug.warn("the previous blockState of just put blockPos is not air, maybe put block at same pos");
        }*/
        return true;
    }

    public @NotNull ServerShip getConstructingShip(ServerLevel level) {
        ServerShip constructing = lazyConstructingShip.get(level, this);
        if (constructing == null) {
            AABBd constructedWorldSize = JomlUtil.scaleFromCenter(constructSize, scale, new AABBd());

            constructing = new ShipBuilder(BlockPos.ZERO, level, getScale(), false)
                .moveLocalPosToWorldPos(JomlUtil.dFaceCenter(constructedWorldSize, Direction.DOWN), bottomCenterPivot)
                .get();
            constructingShipId = constructing.getId();
            EzDebug.highlight("create constructing ship " + constructingShipId);

            //here lazyConstructingShip must get same ship
            if (lazyConstructingShip.get(level, this) != constructing) {
                EzDebug.error("lazyConstructingShip get ship is not constructingShip!");
            }
        }
        return constructing;
    }



    @Override
    public CompoundTag serializeNBT() {
        NbtBuilder builder = new NbtBuilder()
            .putMap("to_construct", toConstruct, (cateName, q) ->
                new NbtBuilder()
                    .putString("cateName", cateName)
                    .putEach("queue", q, TriTuple.SavedBlockTuple::serializeNBT)
                    .get()
            )
            .putVector3d("scale", scale)
            .putVector3d("pivot", bottomCenterPivot)
            .putLong("constructing_ship_id", constructingShipId);  //feel free to put constructingShipId, because if ship is null, id is -1 and will read -1, won't cause anything trouble

        return builder.get();
    }
    @Override
    public void deserializeNBT(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("to_construct", t -> {
                Dest<String> cateName = new Dest<>();
                ConcurrentLinkedQueue<TriTuple.SavedBlockTuple> q = new ConcurrentLinkedQueue<>();

                NbtBuilder.modify(t)
                    .readString("cateName", cateName)
                    .readEachCompoundOverwrite("queue", TriTuple.SavedBlockTuple::new, q);

                //EzDebug.log("schemeData has category:" + cateName.get());

                return new BiTuple<>(cateName.get(), q);
            }, toConstruct)
            .readVector3d("scale", scale)
            .readVector3d("pivot", bottomCenterPivot)
            .readLongDo("constructing_ship_id", v -> constructingShipId = v);

        CompoundTag t = tag;  //for debug breakpoint
    }
}
