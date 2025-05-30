package com.lancas.vswap.foundation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;


@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BiTuple<T, U> {
    public static class BlockTuple extends BiTuple<BlockPos, BlockState> {
        public BlockTuple(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        public BlockPos getBlockPos() { return super.getFirst(); }
        public BlockState getBlockState() { return super.getSecond(); }
    }
    public static class FluidTuple extends BiTuple<BlockPos, FluidState> {
        public FluidTuple(BlockPos pos, FluidState state) {
            super(pos, state);
        }

        public BlockPos getBlockPos() { return super.getFirst(); }
        public FluidState getFluidState() { return super.getSecond(); }
    }
    public static class RayTupleV3 extends BiTuple<Vec3, Vec3> {
        public RayTupleV3(Vec3 from, Vec3 to) {
            super(from, to);
        }

        public Vec3 getFrom() { return super.getFirst(); }
        public Vec3 getTo() { return super.getSecond(); }
    }
    public static class ChunkXZ extends BiTuple<Integer, Integer> {
        public ChunkXZ(int x, int z) {
            super(x, z);
        }
        public static ChunkXZ chunkBlockIn(BlockPos bp) { return new ChunkXZ(bp.getX() >> 4, bp.getZ() >> 4); }

        public int getX() { return super.getFirst(); }
        public int getZ() { return super.getSecond(); }
    }
    public static class XZ extends BiTuple<Integer, Integer> {
        public XZ(int x, int z) {
            super(x, z);
        }

        public int getX() { return super.getFirst(); }
        public int getZ() { return super.getSecond(); }
    }
    public static class MinMax<TT> extends BiTuple<TT, TT> {
        public MinMax(TT min, TT max) {
            super(min, max);
        }

        public TT min() { return super.getFirst(); }
        public TT max() { return super.getSecond(); }
    }

    protected T first;
    protected U second;

    public BiTuple() { first = null; second = null; }
    public BiTuple(T inFirst, U inSecond) {
        first = inFirst;
        second = inSecond;
    }

    public T getFirst() {
        return first;
    }
    public U getSecond() {
        return second;
    }
    public void setFirst(T inFirst) {
        first = inFirst;
    }
    public void setSecond(U inSecond) {
        second = inSecond;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BiTuple<?, ?> other = (BiTuple<?, ?>) o;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second);
    }
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
    @Override
    public String toString() {
        return "PairKey{" +
            "first=" + first +
            ", second=" + second +
            '}';
    }
}
