package com.lancas.vswap.ship.ballistics.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.content.block.blocks.cartridge.modifier.IModifier;
import com.lancas.vswap.content.block.blocks.cartridge.ticker.ITicker;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.data.SavedBlockPos;
import com.lancas.vswap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vswap.ship.ballistics.api.IPhysicalBehaviourBlock;
import com.lancas.vswap.ship.ballistics.api.ITerminalEffector;
import com.lancas.vswap.ship.ballistics.api.ITrigger;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.StrUtil;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class BallisticsComponentData {
    @JsonIgnore
    private static final int TOTAL_SYSTEM_FAIL_TICK = 50;

    //maybe const a lot when serialize.
    //todo custom serialzier to make sure CopyOnWriteArrayList don't make lots of arrays
    private final List<SavedBlockPos> allBlocks = new CopyOnWriteArrayList<>();//new ArrayList<>();
    private final List<SavedBlockPos> modifierSbps = new CopyOnWriteArrayList<>();
    private final List<SavedBlockPos> tickerSbps = new CopyOnWriteArrayList<>();
    private final List<SavedBlockPos> triggerSbps = new CopyOnWriteArrayList<>();
    private final List<SavedBlockPos> effectorSbps = new CopyOnWriteArrayList<>();
    private final List<SavedBlockPos> physBehaviourAdderSbps = new CopyOnWriteArrayList<>();

    //private boolean anySystemFailed = false;
    //private int totalSystemFailTick = TOTAL_SYSTEM_FAIL_TICK;
    //public boolean isAnySystemFailed() { return anySystemFailed; }


    public BallisticsComponentData(ServerLevel level, ServerShip projectileShip) {
        List<SavedBlockPos> tempAllBlocks = new ArrayList<>();
        List<SavedBlockPos> tempModifiers = new ArrayList<>();
        List<SavedBlockPos> tempTickers = new ArrayList<>();
        List<SavedBlockPos> tempTriggers = new ArrayList<>();
        List<SavedBlockPos> tempEffectors = new ArrayList<>();
        List<SavedBlockPos> tempPhyBehaviours = new ArrayList<>();
        ShipUtil.foreachBlock(projectileShip, level, (bp, state, be) -> {
            //allBlocks.add(new SavedBlockPos(bp));
            tempAllBlocks.add(new SavedBlockPos());

            Block block = state.getBlock();
            if (block instanceof IModifier) {
                tempModifiers.add(new SavedBlockPos(bp));
            }
            if (block instanceof ITicker) {
                tempTickers.add(new SavedBlockPos(bp));
            }
            if (block instanceof ITrigger) {
                tempTriggers.add(new SavedBlockPos(bp));
            }
            if (block instanceof ITerminalEffector) {
                tempEffectors.add(new SavedBlockPos(bp));
            }
            if (block instanceof IPhysicalBehaviourBlock) {
                tempPhyBehaviours.add(new SavedBlockPos(bp));
            }
        });

        allBlocks.addAll(tempAllBlocks);
        modifierSbps.addAll(tempModifiers);
        tickerSbps.addAll(tempTickers);
        triggerSbps.addAll(tempTriggers);
        effectorSbps.addAll(tempEffectors);
        physBehaviourAdderSbps.addAll(tempPhyBehaviours);
    }

    /*public void tryAcceptComponent(BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof IModifier) {
            modifierSbps.add(new SavedBlockPos(pos));
        }
        if (block instanceof ITicker) {
            tickerSbps.add(new SavedBlockPos(pos));
        }
    }
    /*public BallisticsComponentData(ServerLevel level, ServerShip projectileShip) {
        ShipUtil.foreachBlock(projectileShip, level, (blockPos, state, be) -> {
            Block block = state.getBlock();
            /.*if (block instanceof ICollisionDetector cDetector) {
                collisionDetectorSbps.add(new SavedBlockPos(blockPos));
            }*./
            if (block instanceof IModifier) {
                modifierSbps.add(new SavedBlockPos(blockPos));
            }
            if (block instanceof ITicker) {
                tickerSbps.add(new SavedBlockPos(blockPos));
            }
        });
    }*/

    public void foreachTicker(ServerLevel level, TriConsumer<BlockPos, BlockState, ITicker> consumer) {
        if (consumer == null) {
            EzDebug.warn("It's nonsense to foreach collisionDetector with a null consumer. do nothing.");
            return;
        }

        for (SavedBlockPos tickerSbp : tickerSbps) {
            BlockPos tickerBp = tickerSbp.toBp();
            BlockState tickerState = level.getBlockState(tickerBp);

            if (!(tickerState.getBlock() instanceof ITicker ticker)) {
                //anySystemFailed = true;  //出于爆炸或其他原因，某个方块状态改变，设置systemFail
                EzDebug.warn("block is not ticker, skip it");
                continue;
            }

            consumer.accept(tickerBp, tickerState, ticker);
        }
    }
    /*public void forEachCollisionDetector(ServerLevel level, TriConsumer<BlockPos, BlockState, ICollisionDetector> consumer) {
        if (consumer == null) {
            EzDebug.warn("It's nonsense to foreach collisionDetector with a null consumer. do nothing.");
            return;
        }

        for (SavedBlockPos detectorSbp : collisionDetectorSbps) {
            BlockPos detectorBp = detectorSbp.toBp();
            BlockState detectorState = level.getBlockState(detectorBp);

            if (!(detectorState.getBlock() instanceof ICollisionDetector detector)) {
                anySystemFailed = true;  //出于爆炸或其他原因，某个方块状态改变，设置systemFail
                continue;
            }

            consumer.accept(detectorBp, detectorState, detector);
        }
    }*/
    public void foreachModifier(ServerLevel level, TriConsumer<BlockPos, BlockState, IModifier> consumer) {
        if (consumer == null) {
            EzDebug.warn("It's nonsense to foreach modifiers with a null consumer. do nothing.");
            return;
        }

        for (SavedBlockPos modifierSbp : modifierSbps) {
            BlockPos modifierBp = modifierSbp.toBp();
            BlockState modifierState = level.getBlockState(modifierBp);

            if (!(modifierState.getBlock() instanceof IModifier modifier)) {
                EzDebug.warn(StrUtil.getBlockName(modifierState) + " is not Modifier and skip it.");
                //anySystemFailed = true;  //出于爆炸或其他原因，某个方块状态改变，设置systemFail
                continue;
            }

            consumer.accept(modifierBp, modifierState, modifier);
        }
    }

    public void foreachTrigger(ServerLevel level, TriConsumer<BlockPos, BlockState, ITrigger> consumer) {
        foreachSbps(level, triggerSbps, (bp, state) -> {
            if (!(state.getBlock() instanceof ITrigger trigger)) {
                EzDebug.warn(StrUtil.getBlockName(state) + " is not ITrigger and skip it.");
                return;
            }
            consumer.accept(bp, state, trigger);
        });
    }
    public void foreachEffector(ServerLevel level, TriConsumer<BlockPos, BlockState, ITerminalEffector> consumer) {
        foreachSbps(level, effectorSbps, (bp, state) -> {
            if (!(state.getBlock() instanceof ITerminalEffector effector)) {
                EzDebug.warn(StrUtil.getBlockName(state) + " is not effector and skip it.");
                return;
            }
            consumer.accept(bp, state, effector);
        });
    }
    public void foreachBlock(Consumer<BlockPos> consumer) {
        allBlocks.forEach(savedBlockPos -> {
            consumer.accept(savedBlockPos.toBp());
        });
    }
    public void foreachPhysBehaviourAdder(ServerLevel level, TriConsumer<BlockPos, BlockState, IPhysicalBehaviourBlock> consumer) {
        foreachSbps(level, physBehaviourAdderSbps, (bp, state) -> {
            if (!(state.getBlock() instanceof IPhysicalBehaviourBlock adder)) {
                EzDebug.warn(StrUtil.getBlockName(state) + " is not IPhysicalBehaviourAdder and skip it.");
                return;
            }
            consumer.accept(bp, state, adder);
        });
    }

    private void foreachSbps(ServerLevel level, List<SavedBlockPos> sbps, BiConsumer<BlockPos, BlockState> consumer) {
        sbps.forEach(sbp -> {
            BlockPos bp = sbp.toBp();
            BlockState state = level.getBlockState(bp);
            consumer.accept(bp, state);
        });
    }
    /*public boolean tickTotalSystemFail() {
        if (totalSystemFailTick <= 0) {
            return true;
        }

        totalSystemFailTick--;
        return false;
    }*/

    public <T> void getPhysBehaviours(ServerLevel level, BiFunction<BlockPos, IPhysBehaviour, T> elementCreator, List<T> dest) {
        dest.clear();
        physBehaviourAdderSbps.forEach(sbp -> {
            BlockPos bp = sbp.toBp();
            BlockState state = level.getChunk(bp).getBlockState(bp);

            if (!(state.getBlock() instanceof IPhysicalBehaviourBlock pbBlock)) {
                EzDebug.warn("at " + bp.toShortString() + " " + StrUtil.getBlockName(state) + " is not phyBehaviourBlock");
                return;
            }

            dest.add(elementCreator.apply(bp, pbBlock.getPhysicalBehaviour(bp, state)));
        });
    }
    public <T> void getPhysBehaviours(ServerLevel level, List<SavedBlockPos> sbpDest, List<IPhysBehaviour> pbDest) {
        sbpDest.clear();
        pbDest.clear();
        physBehaviourAdderSbps.forEach(sbp -> {
            BlockPos bp = sbp.toBp();
            BlockState state = level.getChunk(bp).getBlockState(bp);

            if (!(state.getBlock() instanceof IPhysicalBehaviourBlock pbBlock)) {
                EzDebug.warn("at " + bp.toShortString() + " " + StrUtil.getBlockName(state) + " is not phyBehaviourBlock");
                return;
            }

            sbpDest.add(new SavedBlockPos(bp));
            pbDest.add(pbBlock.getPhysicalBehaviour(bp, state));
        });
    }
    public void getPhysBehaviours(ServerLevel level, Map<SavedBlockPos, IPhysBehaviour> dest) {
        dest.clear();
        physBehaviourAdderSbps.forEach(sbp -> {
            BlockPos bp = sbp.toBp();
            BlockState state = level.getChunk(bp).getBlockState(bp);

            if (!(state.getBlock() instanceof IPhysicalBehaviourBlock pbBlock)) {
                EzDebug.warn("at " + bp.toShortString() + " " + StrUtil.getBlockName(state) + " is not phyBehaviourBlock");
                return;
            }

            dest.put(new SavedBlockPos(bp), pbBlock.getPhysicalBehaviour(bp, state));
        });
    }
}
