package com.lancas.vswap.content.block.blocks.cartridge.warhead;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.data.SavedBlockPos;
import com.lancas.vswap.foundation.math.WapBallisticMath;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.subproject.sandbox.thread.impl.server.SandBoxServerThread;
import com.lancas.vswap.subproject.sandbox.thread.schedule.experimental.IServerThreadScheduler;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

import java.util.*;

//无视装甲吸收率
//破坏能量线性衰减，不受阻挡方块的影响
public class ThermobaricWarhead extends BlockPlus implements ISandBoxBallisticBlock {
    public static float EXP_POWER = 5f;
    public static double THERM_RADIUS = 15;  //todo 20
    //private static double CENTER_SPE = 10;
    private static double TB_SPE = 0.4;

    public ThermobaricWarhead(Properties p_49795_) {
        super(p_49795_);
    }

    public static class ThermobaricScheduler implements IServerThreadScheduler {
        private static final int TICK_MAX_HANDLE_POS = 40;

        private final SavedBlockPos origin;
        private final double sqRadius;
        private final Queue<SavedBlockPos> open = new ArrayDeque<>();
        private final Set<SavedBlockPos> visited = new HashSet<>();

        private final Queue<SavedBlockPos> toDestroyPoses = new ArrayDeque<>();

        //private double lastRadius = 0;

        private ThermobaricScheduler() { this(null, 0); }
        public ThermobaricScheduler(BlockPos inCenter, double inRadius) {
            origin = new SavedBlockPos(inCenter);
            sqRadius = inRadius * inRadius;
        }

        @Override
        public boolean tick(ServerLevel level) {
            if (!visited.contains(origin)) {
                open.add(origin);
                visited.add(origin);

                //get nearby vs ship and add transformed origin
                Vector3dc originWorldCenter = origin.dCenter();
                AABBd worldRangeAABB = JomlUtil.dCenterExtended(originWorldCenter, Math.sqrt(sqRadius));
                VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getIntersecting(worldRangeAABB).forEach(s -> {
                    BlockPos inShipOrigin = JomlUtil.bpContaining(s.getWorldToShip().transformPosition(originWorldCenter, new Vector3d()));
                    SavedBlockPos savedInShipOri = new SavedBlockPos(inShipOrigin);
                    open.add(savedInShipOri);
                    visited.add(savedInShipOri);
                });
            }

            if (!open.isEmpty()) {
                Queue<SavedBlockPos> curOpen = new ArrayDeque<>(open);
                Vec3 originCenter = origin.center();
                open.clear();  //clear current open

                while (!curOpen.isEmpty()) {
                    SavedBlockPos cur = curOpen.poll();
                    for (Direction d : Direction.values()) {
                        SavedBlockPos next = cur.relative(d);
                        if (!visited.contains(next) && next.center().distanceToSqr(originCenter) <= sqRadius) {
                            open.add(next);
                            visited.add(next);
                        }
                    }

                    BlockState curArmour = level.getBlockState(cur.toBp());
                    if (!curArmour.isAir()) {
                        double rhae = WapBlockInfos.ArmourRhae.valueOrDefaultOf(curArmour);
                        if (TB_SPE >= rhae)
                            toDestroyPoses.add(cur);
                    }
                    //BlockHelper.destroyBlock(level, cur.toBp(), 1f);
                    //if (curSpe >= rhae)
                    //    BlockHelper.destroyBlock(level, cur.toBp(), 1f);
                }
            }

            for (int i = 0; i < TICK_MAX_HANDLE_POS && !toDestroyPoses.isEmpty(); ++i) {
                BlockPos dPos = toDestroyPoses.poll().toBp();
                BlockHelper.destroyBlock(level, dPos, 1f);
            }
            //discard when open is empty and destroyPoses is empty
            boolean discard = open.isEmpty() && toDestroyPoses.isEmpty();
            if (discard) {
                CompoundTag saved = new NbtBuilder().putSimpleJackson("saved", this).get();
                EzDebug.log("saved:" + saved);
            }
            return open.isEmpty() && toDestroyPoses.isEmpty();
        }
    }


    @Override
    public List<IBlockAdder> getAdders() {
        return ThermobaricWarhead.addersIfAbsent(ThermobaricWarhead.class, () -> List.of(
            new DefaultCartridgeAdder(true)
        ));
    }

    @Override
    public void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
        infos.stream()
            .map(i -> {
                if (i instanceof SandBoxTriggerInfo.ActivateTriggerInfo activateInfo)
                    return activateInfo;
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst()
            .ifPresent(info -> {
                var rigidReader = ship.getRigidbody().getDataReader();

                Vector3dc activatePos = info.activatePos;
                level.explode(null, activatePos.x(), activatePos.y(), activatePos.z(), EXP_POWER, Level.ExplosionInteraction.BLOCK);  //apply a small exp

                SandBoxServerThread sThread = SandBoxServerWorld.getOrCreate(level).getThread(SandBoxServerThread.class);
                if (sThread == null) {
                    EzDebug.warn("get null SandBoxServerThread, can't apply ThermobaricScheduler");
                } else {
                    sThread.addScheduler(new ThermobaricScheduler(JomlUtil.bpContaining(activatePos), THERM_RADIUS));
                }

                terminateByEffect.set(true);
                SandBoxServerWorld.markShipDeleted(level, ship.getUuid());
            });
    }
}
