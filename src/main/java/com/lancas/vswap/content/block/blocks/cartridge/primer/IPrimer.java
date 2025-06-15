package com.lancas.vswap.content.block.blocks.cartridge.primer;

import com.lancas.vswap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vswap.content.block.blocks.artillery.breech.helper.LoadedMunitionData;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.IterateUtil;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.List;

public interface IPrimer {
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");

    //public int getPixelLength();
    public boolean isTriggered(BlockState state);
    public BlockState getTriggeredState(BlockState prevState);

    public @Nullable BlockClusterData fire(ServerLevel level, List<LoadedMunitionData> loaded, Dest<Double> speDest, List<ItemStack> munitionRemains);
    //public void fire(Stream<SandBoxServerShip> loaded, Dest<SandBoxServerShip> firedShip);
    //public boolean keepInProjectile();

    //public void setTriggered(BlockState state, boolean val);
    //return the power
    //public TriTuple<Double, Vector3dc, Long> trigger(ServerLevel level, long artilleryBreechId, BlockPos pos, BlockState state, Vector3i projectileStartPosDest);

    public static void foreachMunition(ServerLevel level, List<LoadedMunitionData> loaded, Class<? extends IPrimer> primerType, TriConsumer<SandBoxServerShip, Vector3ic, BlockState> consumer) {
        int loadedCnt = 0;
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);

        for (LoadedMunitionData data : IterateUtil.reverseListIterable(loaded)) {
            SandBoxServerShip ship = saWorld.getServerShip(data.shipUuid());
            if (ship == null) {
                EzDebug.warn("primer firing get null ship, skip it, not increase loadedCnt");
                continue;
            }

            IBlockClusterDataReader blockReader = ship.getBlockCluster().getDataReader();
            if (loadedCnt == 0) {
                BlockState primerState = blockReader.getBlockState(IBreech.LOADED_MUNITION_ORIGIN);
                if (!primerState.getBlock().getClass().equals(primerType)) {
                    EzDebug.warn("get primer not this but:" + StrUtil.getBlockName(primerState));
                    return;
                }
            }

            loadedCnt++;
            Vector3i curLocPos = new Vector3i(IBreech.LOADED_MUNITION_ORIGIN);
            while (true) {
                BlockState state = blockReader.getBlockState(curLocPos);
                if (state.isAir())
                    break;

                consumer.accept(ship, curLocPos, state);

                curLocPos.add(IBreech.LOADED_MUNITION_FORWARD);
            }
        }
    }
}
