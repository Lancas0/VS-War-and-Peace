package com.lancas.vswap.foundation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TriTuple<T, U, V> {
    public static class TriCollection<TT> extends TriTuple<TT, TT, TT> {
        public TriCollection(TT inFirst, TT inSecond, TT inThird) {
            super(inFirst, inSecond, inThird);
        }
        public TriCollection() {}

        public <UU> TriCollection<UU> map(@NotNull Function<TT, UU> mapper) {
            return new TriCollection<>(
                mapper.apply(getFirst()),
                mapper.apply(getSecond()),
                mapper.apply(getThird())
                );
        }
    }
    static final class TupleIterator <TI, UI, VI> implements Iterator<Object> {
        private int iterateCnt = 0;
        private final TI first;
        private final UI second;
        private final VI third;

        public TupleIterator(TI inFirst, UI inSecond, VI inThird) { first = inFirst; second = inSecond; third = inThird; }

        //@Override
        //public @NotNull Iterator<Object> iterator() { return this; }
        @Override
        public boolean hasNext() { return iterateCnt <= 2; } //cnt 0 for has next 1, 1 for next 2, 2 for next 3, 3 for no next

        @Override
        public Object next() {
            return switch (iterateCnt++) {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                default -> throw new RuntimeException("invalid iterateCnt:" + iterateCnt);
            };
        }
    }
    public static class BlockTuple extends TriTuple<BlockPos, BlockState, BlockEntity> {
        public BlockTuple(BlockPos pos, BlockState state, BlockEntity be) {
            super(pos, state, be);
        }

        public BlockPos getBlockPos() { return super.getFirst(); }
        public BlockState getBlockState() { return super.getSecond(); }
        public BlockEntity getBlockEntity() { return super.getThird(); }
    }
    public static class SavedBlockTuple extends TriTuple<BlockPos, BlockState, CompoundTag> implements INBTSerializable<CompoundTag> {
        public SavedBlockTuple(BlockPos pos, BlockState state, CompoundTag beNbt) {
            super(pos, state, beNbt);
        }
        public SavedBlockTuple(CompoundTag tag) { deserializeNBT(tag); }

        public BlockPos getBlockPos() { return super.getFirst(); }
        public BlockState getBlockState() { return super.getSecond(); }
        public CompoundTag getBeNbt() { return super.getThird(); }

        @Override
        public CompoundTag serializeNBT() {
            return NbtBuilder.tagOfBlock(first, second, third);
        }
        @Override
        public void deserializeNBT(CompoundTag tag) {
            var tuple = NbtBuilder.blockOf(tag);
            first = tuple.first;
            second = tuple.second;
            third = tuple.third;
        }
    }
    public static class ChunkXZOffsetTuple extends TriTuple<Integer, Integer, BlockPos> {
        private ChunkXZOffsetTuple() {}
        public ChunkXZOffsetTuple(int chunkX, int chunkZ, BlockPos offset) { super(chunkX, chunkZ, offset); }
        public int getChunkX() { return getFirst(); }
        public int getChunkZ() { return getSecond(); }
        public BlockPos getOffset() { return getThird(); }

        public BlockPos toRealBp() {
            return new BlockPos(
                (getChunkX() << 4) + getOffset().getX(),
                getOffset().getY(),
                (getChunkZ() << 4) + getOffset().getZ()
            );
        }
    }

    protected T first;
    protected U second;
    protected V third;

    public TriTuple() {}
    public TriTuple(T inFirst, U inSecond, V inThird) {
        first = inFirst;
        second = inSecond;
        third = inThird;
    }

    public T getFirst() {
        return first;
    }
    public U getSecond() {
        return second;
    }
    public V getThird() { return third; }
    public Object get(int component) {
        return switch (component) {
            case 0 -> getFirst();
            case 1 -> getSecond();
            case 2 -> getThird();
            default -> null;
        };
    }

    public void setFirst(T inFirst) {
        first = inFirst;
    }
    public void setSecond(U inSecond) {
        second = inSecond;
    }
    public void setThird(V third) { this.third = third; }

    public Iterator<Object> iterator() { return new TupleIterator<T, U, V>(first, second, third); }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TriTuple<?, ?, ?> triTuple = (TriTuple<?, ?, ?>) o;
        return Objects.equals(first, triTuple.first) && Objects.equals(second, triTuple.second) && Objects.equals(third, triTuple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "TriTuple{" +
            "first=" + first +
            ", second=" + second +
            ", third=" + third +
            '}';
    }
}
