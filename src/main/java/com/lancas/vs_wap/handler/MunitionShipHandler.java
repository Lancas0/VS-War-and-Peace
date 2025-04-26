package com.lancas.vs_wap.handler;

import com.lancas.vs_wap.content.blocks.cartridge.propellant.IPropellant;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class MunitionShipHandler {
    /*public static void analyze(
        Vector3ic primerPos,
        Vector3ic primerDir,
        Function<Vector3ic, BlockState> stateGetter,
        @Nullable BiConsumer<Vector3ic, BlockState> propellantEmptySetter,
        Dest<Double> propellingEnergyDest
    ) {
        Dest<Vector3i> projectileStartDest = new Dest<>();
        foreachPropellant(primerPos, primerDir, stateGetter, propellantEmptySetter, propellingEnergyDest, projectileStartDest);



    }*/
    public static void foreachPropellant(
        Vector3ic primerPos,
        Vector3ic munitionDir,
        Function<Vector3ic, BlockState> stateGetter,
        @Nullable BiConsumer<Vector3ic, BlockState> propellantEmptySetter,
        @Nullable Dest<Double> propellingEnergyDest,
        @Nullable Dest<Vector3i> projectileStartDest
    ) {
        Vector3i propellantStartPos = primerPos.add(munitionDir, new Vector3i());
        Vector3i curPos = new Vector3i(propellantStartPos);

        EzDebug.log("propellantStartPos:" + StrUtil.poslike(propellantStartPos));

        double propellingEnergy = 0;

        while (true) {
            BlockState curState = stateGetter.apply(curPos);
            EzDebug.log("propellant: curPos:" + StrUtil.poslike(curPos) + ", state:" + StrUtil.getBlockName(curState));
            if (!(curState.getBlock() instanceof IPropellant propellant)) break;


            propellingEnergy += propellant.getEnergy(curState);
            if (propellantEmptySetter != null)
                propellantEmptySetter.accept(curPos, curState);

            curPos.add(munitionDir);
        }

        Dest.setIfExistDest(propellingEnergyDest, propellingEnergy);
        Dest.setIfExistDest(projectileStartDest, new Vector3i(curPos));
    }

    public static void foreachFromProjectileStart(
        Vector3ic projectileStart,
        Vector3ic munitionDir,
        Function<Vector3ic, BlockState> stateGetter,
        BiConsumer<Vector3ic, BlockState> foreacher
    ) {
        EzDebug.log("projectileStart:" + StrUtil.poslike(projectileStart));

        Vector3i curPos = new Vector3i(projectileStart);
        while (true) {
            BlockState curState = stateGetter.apply(curPos);
            EzDebug.log("proj: curPos:" + StrUtil.poslike(curPos) + ", state:" + StrUtil.getBlockName(curState));

            if (curState == null || curState.isAir()) break;

            foreacher.accept(curPos, curState);

            curPos.add(munitionDir);
        }
    }
}
