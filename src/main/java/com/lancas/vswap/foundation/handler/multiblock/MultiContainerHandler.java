package com.lancas.vswap.foundation.handler.multiblock;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.handler.multiblock.alog.HorizonMaxRectSolver;
import com.lancas.vswap.foundation.handler.multiblock.alog.MonotonicStack;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.joml.primitives.Rectanglei;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class MultiContainerHandler {
    protected final ServerLevel level;
    protected final Thread mainThread;
    protected final Executor mainThreadProcessor;

    public MultiContainerHandler(ServerLevel inLevel) {
        level = inLevel;
        mainThread = Thread.currentThread();
        mainThreadProcessor = new MainThreadExecutor(inLevel);

        //containerType = inType;
    }

    protected class MultiContainerCache {
        protected int minY;
        protected int maxY;
        protected HashMap<Integer, LayerCache> eachYCache = new HashMap<>();

        protected final IMultiContainerType containerType;

        public MultiContainerCache(IMultiContainerType inType) {
            minY = Integer.MAX_VALUE;
            maxY = Integer.MIN_VALUE;
            containerType = inType;
        }
        public boolean isValid() { return minY <= maxY; }

        public boolean cacheIfValid(BlockPos bp) {
            BlockEntity be = level.getBlockEntity(bp);
            if (be == null)
                return false;
            if (be.isRemoved()) {
                EzDebug.warn("at " + bp + " is removed");
                return false;
            }

            if (be instanceof IMultiContainerBE multiBE && multiBE.isPartOf(containerType)) {
                minY = Math.min(minY, bp.getY());
                maxY = Math.max(maxY, bp.getY());

                eachYCache.computeIfAbsent(
                    bp.getY(),
                    k -> new LayerCache()
                ).cache(bp, multiBE);

                //if (resetBE)
                //    multiBE.reset();
                //the multiContainer will be reset
                multiBE.onMultiContainerReset();

                return true;
            }
            return false;
        }
        public @Nullable IMultiContainerBE getCache(BlockPos at) {
            return Optional.ofNullable(eachYCache.get(at.getY()))
                .map(c -> c.getCache(at))
                .orElse(null);
        }

        public void clear() {
            minY = Integer.MAX_VALUE;
            maxY = Integer.MIN_VALUE;
            eachYCache.clear();
        }
        public Stream<IMultiContainerBE> allBE() {
            return eachYCache.values().stream()
                .flatMap(c -> c.cache.values().stream());
        }
        public Stream<BlockPos> allBlockPos() {
            return eachYCache.values().stream()
                .flatMap(c -> c.cache.keySet().stream());
        }
        public void foreach(BiConsumer<BlockPos, IMultiContainerBE> consumer) {
            eachYCache.values().stream()
                .flatMap(c -> c.cache.entrySet().stream())
                .forEach(e -> consumer.accept(e.getKey(), e.getValue()));
        }

        public @Nullable LayerCache getLayer(int y) {
            return eachYCache.get(y);
        }


        protected class LayerCache {
            protected int minX, minZ;
            protected int maxX, maxZ;

            protected HashMap<BlockPos, IMultiContainerBE> cache = new HashMap<>();

            public LayerCache() {
                minX = minZ = Integer.MAX_VALUE;
                maxX = maxZ = Integer.MIN_VALUE;
            }

            //public boolean isValid() { return minX <= maxX && minZ <= maxZ; }

            public void cacheIfValid(BlockPos bp) {  //suppose the Y is this layer's Y
                if (cache.containsKey(bp)) {
                    EzDebug.warn("cache with a existed key. will override it. prev val:" + cache.get(bp));
                }

                if (level.getBlockEntity(bp) instanceof IMultiContainerBE be && be.isPartOf(containerType)) {
                    cache(bp, be);
                } /*else {  //cache is one-calculate and used-all time, don't push null value
                cache(bp, null);
            }*/
            }
            public void cache(BlockPos bp, IMultiContainerBE be) {
                minX = Math.min(minX, bp.getX());
                minZ = Math.min(minZ, bp.getZ());
                maxX = Math.max(maxX, bp.getX());
                maxZ = Math.max(maxZ, bp.getZ());

                cache.put(bp, be);
            }
            public @Nullable IMultiContainerBE getCache(BlockPos at) { return cache.get(at); }
        }
    }
    protected static class FormingMap {
        protected AABBi range = new AABBi();
        protected HashMap<Integer, LayerFormingMap> eachLayerMap = new HashMap<>();


        public void setInfoAt(int x, int y, int z, int continuousH, int continuousZ) {
            eachLayerMap
                .computeIfAbsent(y, k -> new LayerFormingMap())
                .setInfoAt(x, z, continuousH, continuousZ);
            range.union(x, y, z);
            //EzDebug.log("union " + StrUtil.poslike(x, y, z) + ", " + range);
        }
        public void setZoneFormed(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
            for (int y = yMin; y <= yMax; ++y) {
                LayerFormingMap layer = eachLayerMap.get(y);

                for (int x = xMin; x <= xMax; ++x)
                    for (int z = zMin; z <= zMax; ++z) {
                        //layer.setInfoAt(x, z, 0, 0);
                        layer.removeAt(x, z);
                    }

                for (int x = xMin; x <= xMax; ++x)
                    for (int z = zMin - 1; z >= range.minZ; --z) {  //update all Zlower's continuousZ
                        LayerFormingMap.Info preInfo = layer.getInfo(x, z);
                        if (preInfo.isValid()) {  //continuous valid, must decrease continuousZ
                            layer.decreaseContinuousZ(x, z, zMax - zMin + 1);
                        } else {  //there is a gap (info is empty), won't set anyLonger
                            break;
                        }
                    }
            }
        }
        public @Nullable LayerFormingMap getLayer(int y) { return eachLayerMap.get(y); }

        protected static class LayerFormingMap {
            //value is continuous height from bottom
            protected static record Info(int continuousH, int continuousZ) {
                public static Info createEmpty() { return new Info(0, 0); }
                public boolean isValid() { return continuousH > 0 && continuousZ > 0; }
            }

            protected HashMap<BiTuple.XZ, Info> map = new HashMap<>();
            private final Rectanglei range = new Rectanglei(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            private boolean rangeDirty = false;

            public Rectanglei getRange() {
                if (rangeDirty) {
                    range.setMin(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    range.setMax(Integer.MIN_VALUE, Integer.MIN_VALUE);
                    map.keySet().forEach(k -> range.union(k.getX(), k.getZ()));
                    rangeDirty = false;
                }
                return range;
            }

            public void setInfoAt(int x, int z, int continuousH, int continuousZ) {
                if (continuousH <= 0 || continuousZ <= 0) {
                    //EzDebug.warn("try put empty info into forming map layer. will not put into map");
                    map.remove(new BiTuple.XZ(x, z));
                    return;
                }
                putImpl(x, z, continuousH, continuousZ);
            }
            public void decreaseContinuousZ(int x, int z, int decrease) {
                BiTuple.XZ key = new BiTuple.XZ(x, z);
                if (!map.containsKey(key))
                    return;

                Info preInfo = map.get(key);
                //Info postInfo = new Info(preInfo.continuousH, preInfo.continuousZ - decrease);
                if (preInfo.continuousZ - decrease <= 0)
                    //map.remove(key);
                    removeImpl(x, z);
                else
                    putImpl(x, z, preInfo.continuousH, preInfo.continuousZ - decrease);
                    //map.put(key, postInfo);
            }
            public void removeAt(int x, int z) {
                //map.remove(new BiTuple.XZ(x, z));
                removeImpl(x, z);
            }
            public boolean isEmpty() {
                return map.isEmpty();  //will not contain (?0, ?0), simply return if empty
            }

            private void putImpl(int x, int z, int continuousH, int continuousZ) {
                map.put(new BiTuple.XZ(x, z), new Info(continuousH, continuousZ));
                range.union(x, z);
            }
            private void removeImpl(int x, int z) {
                BiTuple.XZ key = new BiTuple.XZ(x, z);
                if (!map.containsKey(key))
                    return;

                map.remove(key);
                rangeDirty = true;
            }
            /*public int getContinuousHeight(int x, int z) {
                BiTuple.XZ xz = new BiTuple.XZ(x, z);

                if (map.containsKey(new BiTuple.XZ(x, z)))
                    return map.get(xz);
                return 0;
            }*/
            public @NotNull Info getInfo(int x, int z) { return Optional.ofNullable(map.get(new BiTuple.XZ(x, z))).orElse(Info.createEmpty()); }

        }
    }

    public void handleFrom(IMultiContainerType type, BlockPos bp) {
        MultiContainerCache cache = getCache(type, bp);
        if (!cache.isValid()) {
            //empty cache
            return;
        }

        FormingMap formingMap = getFormingMap(cache);
        AABBic range = formingMap.range;

        for (int y = range.minY(); y <= range.maxY(); ++y) {
            FormingMap.LayerFormingMap layerMap = formingMap.getLayer(y);
            MultiContainerCache.LayerCache layerCache = cache.getLayer(y);
            if (layerMap == null || layerCache == null)
                continue;

            while (!layerMap.isEmpty()) {
                Rectanglei layerFormingRange = layerMap.getRange();
                Vector2ic lower = new Vector2i(layerFormingRange.minX, layerFormingRange.minY);
                boolean[][] layer01Matrix = layerTo01Matrix(layerMap);
                Rectanglei rect = new HorizonMaxRectSolver().getMaxRect(layer01Matrix);
                if (rect == null)
                    return;

                Direction.Axis lenAxis;
                int formingMaxOffsetX, formingMaxOffsetZ;
                if (rect.maxX - rect.minX > rect.maxY - rect.minY) {
                    lenAxis = Direction.Axis.X;
                    formingMaxOffsetX = Math.min(rect.maxX, rect.minX + type.getMaxLength() - 1);
                    formingMaxOffsetZ = Math.min(rect.maxY, rect.minY + type.getMaxWidth() - 1);
                } else {
                    lenAxis = Direction.Axis.Z;
                    formingMaxOffsetX = Math.min(rect.maxX, rect.minX + type.getMaxWidth() - 1);
                    formingMaxOffsetZ = Math.min(rect.maxY, rect.minY + type.getMaxLength() - 1);
                }

                int height = type.getMaxHeight();
                for (int ox = rect.minX; ox <= formingMaxOffsetX; ++ox)
                    for (int oz = rect.minY; oz <= formingMaxOffsetZ; ++oz) {
                        //BlockPos curPos = controller.offset(lower.x() + ox, 0, lower.y() + oz);
                        int curHeight = layerMap.getInfo(lower.x() + ox, lower.y() + oz).continuousH;

                        if (curHeight <= 0) {
                            EzDebug.warn("get height " + curHeight + " in forming controller zone");
                            continue;
                        }

                        height = Math.min(height, curHeight);
                    }


                BlockPos controller = new BlockPos(lower.x() + rect.minX, y, lower.y() + rect.minY);
                IMultiContainerBE controllerBE = null;
                for (int ox = rect.minX; ox <= formingMaxOffsetX; ++ox)
                    for (int oz = rect.minY; oz <= formingMaxOffsetZ; ++oz)
                        for (int oy = 0; oy <= height - 1; ++oy) {
                            BlockPos curPos = new BlockPos(lower.x() + ox, y + oy, lower.y() + oz);

                            IMultiContainerBE containerBE = cache.getCache(curPos);
                            if (containerBE == null) {
                                EzDebug.warn("fail to get multiContainerBe at " + controller.offset(ox, 0, oz).toShortString());
                                continue;
                            }

                            if (ox == rect.minX && oy == 0 && oz == rect.minY)
                                controllerBE = containerBE;

                            containerBE.setController(controller);
                            //containerBE.setSize(formingMaxOffsetX - rect.minX + 1, formingMaxOffsetZ - rect.minY + 1, lenAxis, height);
                            int xSize = formingMaxOffsetX - rect.minX + 1;
                            int zSize =  formingMaxOffsetZ - rect.minY + 1;
                            containerBE.setSize(Math.max(xSize, zSize), Math.min(xSize, zSize), lenAxis, height);
                            containerBE.setDirty();

                            //controller must be already setted
                            if (controllerBE == null) {
                                EzDebug.warn("controllerBE is not setted when try to call controller's onIncludePart");
                                continue;
                            }
                            controllerBE.onIncludePart(curPos, containerBE);
                        }

                if (controllerBE == null) {
                    EzDebug.warn("controllerBE is null, fail to set Dirty at last");
                } else {
                    controllerBE.setDirty();  //set dirty at last, make sure that after onIncludePart, dirty is also set
                }

                formingMap.setZoneFormed(
                    lower.x() + rect.minX, y, lower.y() + rect.minY,
                    lower.x() + /*rect.maxX*/formingMaxOffsetX, y + height - 1, lower.y() + formingMaxOffsetZ//rect.maxY
                );

                int xSize = (formingMaxOffsetX - rect.minX + 1);
                int ySize = (formingMaxOffsetZ - rect.minY + 1);
                EzDebug.log("form size:" + xSize + ", " + ySize + ", area:" + (xSize * ySize));
            }
        }



    }

    /*public void handleFrom(IMultiContainerType type, BlockPos bp) {
        MultiContainerCache cache = getCache(type, bp);
        if (!cache.isValid()) {
            //empty cache
            return;
        }

        FormingMap formingMap = getFormingMap(cache);
        AABBic range = formingMap.range;
        //final int[] maxArea = {0};
        //final AABBi[] boundWhenMaxArea = {new AABBi()};


        for (int y = range.minY(); y <= range.maxY(); ++y) {
            FormingMap.LayerFormingMap layerMap = formingMap.getLayer(y);
            MultiContainerCache.LayerCache layerCache = cache.getLayer(y);
            if (layerMap == null || layerCache == null)
                continue;

            //until layer is all formed
            while (!layerMap.isEmpty()) {
                //record this time maxArea and bound

                final AABBi[] boundWhenMaxArea = { new AABBi() };

                EzDebug.log("layerMap is not empty, size:" + layerMap.isEmpty());

                for (int z = range.minZ(); z <= range.maxZ(); ++z) {
                    int curZ = z;
                    MonotonicStack<Node> stack = new MonotonicStack<>(Comparator.comparingInt(o -> o.continuousZ));

                    for (int x = range.minX(); x <= range.maxX(); ++x) {
                        var info = layerMap.getInfo(x, z);
                        if (info.continuousH <= 0 || info.continuousZ <= 0)
                            continue;

                        int curX = x;
                        AtomicInteger curLowerXLength = new AtomicInteger(0);
                        Node toPush = new Node(info.continuousZ, 0, x);  //set lowerXLength later
                        stack.pushWithPrePushPostPopCallback(toPush, pop -> {
                            curLowerXLength.addAndGet(pop.lowerXLength);
                            int newArea = pop.continuousZ * curLowerXLength.get();
                            if (newArea > maxArea[0]) {
                                maxArea[0] = newArea;
                                boundWhenMaxArea[0].setMin(pop.atX, 0, curZ);  //set y later
                                boundWhenMaxArea[0].setMax(curX - 1, 0, curZ + pop.continuousZ - 1);
                                EzDebug.log(boundWhenMaxArea[0].toString());
                            }

                            //int xLen = stack.isEmpty() ? (curX - range.minX()) : (curX - stack.top().atX - 1);//(pop.atX - range.minX() + 1) : (pop.atX - stack.top().atX + 1);
                            //update maxArea and bound when pop
                            /.*int newArea = pop.continuousZ * xLen;//(curX - pop.atX);
                            if (newArea > maxArea[0]) {
                                maxArea[0] = newArea;
                                boundWhenMaxArea[0].setMin(pop.atX, 0, curZ);  //set y later
                                boundWhenMaxArea[0].setMax(curX - 1, 0, curZ + pop.continuousZ - 1);
                                EzDebug.log(boundWhenMaxArea[0].toString());
                            }*./
                        });
                        toPush.lowerXLength = curLowerXLength.get() + 1;
                    }

                    //pop all element in stack and calculate area
                    int sumLowerXLength = 0;
                    while (!stack.isEmpty()) {
                        Node pop = stack.pop();
                        sumLowerXLength += pop.lowerXLength;
                        int newArea = pop.continuousZ * sumLowerXLength;
                        if (newArea > maxArea[0]) {
                            maxArea[0] = newArea;
                            boundWhenMaxArea[0].setMin(pop.atX, 0, curZ);  //set y later
                            boundWhenMaxArea[0].setMax(range.maxX(), 0, curZ + pop.continuousZ - 1);
                            EzDebug.log(boundWhenMaxArea[0].toString());
                        }
                    }
                    /.*while (!stack.isEmpty()) {
                        Node pop = stack.pop();
                        int xLen = stack.isEmpty() ? (range.maxX() - range.minX() + 1) : (range.maxX() - stack.top().atX);//stack.isEmpty() ? (pop.atX - range.minX() + 1) : (pop.atX - stack.top().atX + 1);
                        int newArea = pop.continuousZ * xLen;//(range.maxX() + 1 - pop.atX);  //curX treated as maxX() + 1, curZ maxZ() + 1
                        if (newArea > maxArea[0]) {
                            maxArea[0] = newArea;
                            boundWhenMaxArea[0].setMin(pop.atX, 0, curZ);  //set y later
                            boundWhenMaxArea[0].setMax(range.maxX(), 0, curZ + pop.continuousZ - 1);
                            EzDebug.log(boundWhenMaxArea[0].toString());
                        }
                    }*./
                }
                //got maxArea(Hor), get Y and set as formed
                if (maxArea[0] <= 0) {
                    EzDebug.error("maxArea is " + maxArea[0] + ", which is impossible");
                    return;
                }

                //get topY
                Integer topY = null;  //Integer.MAX_VALUE  //topY is minY among boundWhenMaxArea
                for (int x = boundWhenMaxArea[0].minX(); x <= boundWhenMaxArea[0].maxX(); ++x)
                    for (int z = boundWhenMaxArea[0].minZ(); z <= boundWhenMaxArea[0].maxZ(); ++z) {
                        IMultiContainerBE cachedBE = layerCache.getCache(new BlockPos(x, y, z));
                        if (cachedBE == null) {  //sure that won't include a empty block in boundWhenMaxArea
                            EzDebug.error("at " + StrUtil.poslike(x, y, z) + " has null cache");
                            //cache.eachYCache.values().forEach(c -> EzDebug.logs(c.cache.keySet(), null));
                            continue;
                        }

                        int newY = y + cachedBE.getContinuousHeight() - 1;
                        topY = topY == null ?
                            newY :
                            Math.min(topY, y + cachedBE.getContinuousHeight() - 1);
                    }

                if (topY == null) {
                    EzDebug.error("get topY null");
                    return;
                }

                formingMap.setZoneFormed(boundWhenMaxArea[0].minX, y, boundWhenMaxArea[0].minZ,
                    boundWhenMaxArea[0].maxX, topY, boundWhenMaxArea[0].maxZ);

                //set be data
                BlockPos controller = new BlockPos(boundWhenMaxArea[0].minX(), y, boundWhenMaxArea[0].minZ());
                int xLen = boundWhenMaxArea[0].maxX() - boundWhenMaxArea[0].minX() + 1;
                int zLen = boundWhenMaxArea[0].maxZ() - boundWhenMaxArea[0].minZ() + 1;
                for (int fx = boundWhenMaxArea[0].minX(); fx <= boundWhenMaxArea[0].maxX(); ++fx)
                    for (int fz = boundWhenMaxArea[0].minZ(); fz <= boundWhenMaxArea[0].maxZ(); ++fz)
                        for (int fy = y; fy <= topY; ++fy) {
                            BlockPos fbp = new BlockPos(fx, fy, fz);
                            IMultiContainerBE fbe = cache.getCache(fbp);
                            if (fbe == null) {
                                EzDebug.warn("inside boundWhenMaxArea get null BE cache!");
                            } else {
                                fbe.setController(controller);
                                fbe.setSize(xLen, zLen, topY - y + 1, Direction.Axis.X);
                            }
                        }
            }
        }

    }*/
    protected Rectanglei getMaxRect(FormingMap.LayerFormingMap layerMap) {
        //final int[] maxArea = { 0 };
        AtomicInteger maxArea = new AtomicInteger(0);
        Rectanglei result = new Rectanglei();
        Rectanglei range = layerMap.getRange();

        class Node {
            protected int continuousZ;
            protected int lowerXLength;
            protected int atX;
            public Node(int cz, int l, int x) { continuousZ = cz; lowerXLength = l; atX = x; }
        }


        for (int z = range.minY; z <= range.maxY; ++z) {
            MonotonicStack<Node> stack = new MonotonicStack<>(Comparator.comparingInt(o -> o.continuousZ));

            for (int x = range.minX; x <= range.maxX; ++x) {
                var info = layerMap.getInfo(x, z);
                if (info.continuousH <= 0 || info.continuousZ <= 0)
                    continue;

                AtomicInteger curLowerXLength = new AtomicInteger(0);
                Node toPush = new Node(info.continuousZ, 0, x);  //set lowerXLength later
                stack.pushWithPrePushPostPopCallback(toPush, pop -> {
                    curLowerXLength.addAndGet(pop.lowerXLength);
                    int newArea = pop.continuousZ * curLowerXLength.get();
                    if (newArea > maxArea.get()) {
                        maxArea.set(newArea);
                        //result.setMin(pop.atX, curZ);
                        //result.setMax(curX - 1, curZ + pop.continuousZ - 1);
                    }
                });
                toPush.lowerXLength = curLowerXLength.get() + 1;
            }

            //pop all element in stack and calculate area
            int sumLowerXLength = 0;
            while (!stack.isEmpty()) {
                Node pop = stack.pop();
                sumLowerXLength += pop.lowerXLength;
                int newArea = pop.continuousZ * sumLowerXLength;
                if (newArea > maxArea.get()) {
                    maxArea.set(newArea);
                    //to set result
                    /*boundWhenMaxArea[0].setMin(pop.atX, 0, curZ);  //set y later
                    boundWhenMaxArea[0].setMax(range.maxX(), 0, curZ + pop.continuousZ - 1);
                    EzDebug.log(boundWhenMaxArea[0].toString());*/
                }
            }
        }

        return result;//no set now
    }

    protected boolean[][] layerTo01Matrix(FormingMap.LayerFormingMap layerMap) {
        Rectanglei range = layerMap.getRange();
        int xLen = range.maxX - range.minX + 1;
        int zLen = range.maxY - range.minY + 1;
        boolean[][] matrix = new boolean[zLen][xLen];


        for (int z = range.minY; z <= range.maxY; ++z)
            for (int x = range.minX; x <= range.maxX; ++x) {
                matrix[z - range.minY][x - range.minX] = layerMap.getInfo(x, z).continuousH > 0;
            }

        return matrix;
    }


    protected MultiContainerCache getCache(IMultiContainerType type, BlockPos from) {
        //DFS get cache
        MultiContainerCache cache = new MultiContainerCache(type);

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> open = new ArrayDeque<>();
        open.add(from);
        cache.cacheIfValid(from);

        while (!open.isEmpty()) {
            BlockPos cur = open.poll();
            visited.add(cur);

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = cur.relative(dir);
                if (visited.contains(neighbor))
                    continue;

                if (cache.cacheIfValid(neighbor)) {
                    open.add(neighbor);
                    EzDebug.log("add cache at " + neighbor.toShortString());
                }

                visited.add(neighbor);  //avoid duplication in open queue
            }
        }
        return cache;
    }
    protected FormingMap getFormingMap(MultiContainerCache cache) {
        FormingMap map = new FormingMap();
        //todo parallel?
        cache.foreach((bp, be) -> {
            map.setInfoAt(bp.getX(), bp.getY(), bp.getZ(), be.getContinuousHeight(), be.getContinuousZLen());
        });
        return map;
    }


    private void asyncFormMultiContainer(BlockPos controller, int length, int width, int height, Direction.Axis lengthAxis) {
        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < length; ++i)
                for (int j = 0; j < width; ++j)
                    for (int k = 0; k < height; ++k) {
                        BlockPos curPos = switch (lengthAxis) {
                            case X -> controller.offset(i, k, j);
                            case Z -> controller.offset(j, k, i);
                            case Y -> throw new RuntimeException("lengthAxis should never be Axis.Y");
                        };

                        if (!(level.getBlockEntity(curPos) instanceof IMultiContainerBE multiBE)/* || !multiBE.isPartOf()*/) {
                            EzDebug.error("when set controller, fail to get IMultiContainerBE");
                            continue;
                        }

                        multiBE.setController(controller);
                        multiBE.setSize(length, width, lengthAxis, height);
                    }
        }, mainThreadProcessor
        );
    }

    protected final class MainThreadExecutor extends BlockableEventLoop<Runnable> {
        MainThreadExecutor(Level level) {
            super("MultiContainerHandler async level executor" + level.dimension().location());
        }
        @Override
        protected @NotNull Runnable wrapRunnable(@NotNull Runnable p_8506_) {
            return p_8506_;
        }
        @Override
        protected boolean shouldRun(@NotNull Runnable p_8504_) {
            return true;
        }
        @Override
        protected boolean scheduleExecutables() {
            return true;
        }
        @Override
        protected @NotNull Thread getRunningThread() { return MultiContainerHandler.this.mainThread; }

        /*protected void doRunTask(Runnable p_8502_) {
            MultiContainerHandler.this.level.getProfiler().incrementCounter("runTask");
            super.doRunTask(p_8502_);
        }*/
        /*@Override
        public boolean pollTask() {
            super.pollTask();
            if (MultiContainerHandler.this.runDistanceManagerUpdates()) {
                return true;
            } else {
                MultiContainerHandler.this.lightEngine.tryScheduleUpdate();
                return super.pollTask();
            }
        }*/
    }
}