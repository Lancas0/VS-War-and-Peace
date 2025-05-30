package com.lancas.vswap.subproject.blockplusapi.util;

import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.api.Dest;
import net.minecraft.world.InteractionResult;

import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Action<TV, TR> {
    //public static Action<?, ?> EMPTY = new Action<Object, Object>() { };
    //public static Action<TTV, TTR>

    /*public default TR sandwichInvoke(TV ctx, TR soFar, Function<TV, TR> mid) {
        Dest<Boolean> cancel = new Dest<>(false);

        soFar = combineResult(soFar, pre(ctx, soFar, cancel));
        if (cancel.get())
            return soFar;

        soFar = combineResult(soFar, mid.apply(ctx));
        soFar = combineResult(soFar, post(ctx, soFar, cancel));
        return soFar;
    }*/
    public static <TTV, TTR> TTR multiSandwichInvoke(Supplier<Stream<Action<TTV, TTR>>> actionsGetter, TTV ctx, TTR initialRet, Function<TTV, TTR> mid) {
        AtomicReference<Action<TTV, TTR>> first = new AtomicReference<>();

        Dest<Boolean> cancel = new Dest<>(false);
        AtomicReference<TTR> soFar = new AtomicReference<>(initialRet);

        actionsGetter.get().filter(Objects::nonNull).forEach(a -> {
            if (first.get() == null)
                first.set(a);

            if (cancel.get())
                return;
            soFar.set(a.combineResult(soFar.get(), a.pre(ctx, soFar.get(), cancel)));
        });

        if (cancel.get())
            return soFar.get();

        if (first.get() == null) {
            return mid.apply(ctx);
        }

        soFar.set(first.get().combineResult(soFar.get(), mid.apply(ctx)));

        actionsGetter.get().filter(Objects::nonNull).forEach(a -> {
            if (cancel.get())
                return;
            soFar.set(a.combineResult(soFar.get(), a.post(ctx, soFar.get(), cancel)));
        });

        return soFar.get();
    }
    //static Hashtable<BiTuple<?, ?>, Action<?, ?>> cachedEmpty = new Hashtable<>();
    /*public static <TTV, TTR> Action<TTV, TTR> getEmpty(Class<TTV> valType, Class<TTR> retType) {
        Action<TT> cached = (Action<TT>)cachedEmpty.get(new BiTuple<>(valType, ));
        if (cached != null)
            return cached;

        Action<TT> newEmpty = new Action<TT>() {};
        cachedEmpty.put(type, newEmpty);
        return newEmpty;
    }*/

    @FunctionalInterface
    public static interface Pre<TTV, TTR> extends Action<TTV, TTR> {
        @Override
        public abstract TTR pre(TTV ctx, TTR soFar, Dest<Boolean> cancel);
    }
    @FunctionalInterface
    public static interface Post<TTV, TTR> extends Action<TTV, TTR> {
        @Override
        public abstract TTR post(TTV ctx, TTR soFar, Dest<Boolean> cancel);
    }

    public default TR pre(TV ctx, TR soFar, Dest<Boolean> cancel) { return null; }
    public default TR post(TV ctx, TR soFar, Dest<Boolean> cancel) { return null; }
    public default TR combineResult(TR first, TR second) { return second; }


    public static interface InteractionAction<T> extends Action<T, InteractionResult> {
        @Override
        public default InteractionResult pre(T ctx, InteractionResult soFar, Dest<Boolean> cancel) { return InteractionResult.PASS; }
        @Override
        public default InteractionResult post(T ctx, InteractionResult soFar, Dest<Boolean> cancel) { return InteractionResult.PASS; }

        @Override
        public default InteractionResult combineResult(InteractionResult first, InteractionResult second) {
            if (first == InteractionResult.FAIL || second == InteractionResult.FAIL)
                return InteractionResult.FAIL;
            if (first.consumesAction() || second.consumesAction())
                return InteractionResult.CONSUME;
            return InteractionResult.FAIL;
        }
    }
}