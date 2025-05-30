package com.lancas.vswap.foundation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Iterator;
import java.util.Objects;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuadTuple<T, U, V, W> {
    static final class TupleIterator <TI, UI, VI, WI> implements Iterator<Object> {
        private int iterateCnt = 0;
        private final TI first;
        private final UI second;
        private final VI third;
        private final WI fourth;

        public TupleIterator(TI inFirst, UI inSecond, VI inThird, WI inFourth) { first = inFirst; second = inSecond; third = inThird; fourth = inFourth; }

        //@Override
        //public @NotNull Iterator<Object> iterator() { return this; }
        @Override
        public boolean hasNext() { return iterateCnt <= 3; } //cnt 0 for has next 1, 1 for next 2, 2 for next 3, 3 for no next

        @Override
        public Object next() {
            return switch (iterateCnt++) {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                case 3 -> fourth;
                default -> throw new RuntimeException("invalid iterateCnt:" + iterateCnt);
            };
        }
    }

    protected T first;
    protected U second;
    protected V third;
    protected W fourth;

    public QuadTuple() {}
    public QuadTuple(T inFirst, U inSecond, V inThird, W inFourth) {
        first = inFirst;
        second = inSecond;
        third = inThird;
        fourth = inFourth;
    }

    public T getFirst() {
        return first;
    }
    public U getSecond() {
        return second;
    }
    public V getThird() { return third; }
    public W getFourth() { return fourth; }
    public Object get(int component) {
        return switch (component) {
            case 0 -> getFirst();
            case 1 -> getSecond();
            case 2 -> getThird();
            case 3 -> getFourth();
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
    public void setFourth(W fourth) { this.fourth = fourth; }

    public Iterator<Object> iterator() { return new TupleIterator<T, U, V, W>(first, second, third, fourth); }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuadTuple<?, ?, ?, ?> other = (QuadTuple<?, ?, ?, ?>) o;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second) && Objects.equals(third, other.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }

    @Override
    public String toString() {
        return "TriTuple{" +
            "first=" + first +
            ", second=" + second +
            ", third=" + third +
            ", fourth=" + fourth +
            '}';
    }
}
