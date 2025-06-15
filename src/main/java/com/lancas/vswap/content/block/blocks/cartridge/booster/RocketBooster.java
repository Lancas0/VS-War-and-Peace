package com.lancas.vswap.content.block.blocks.cartridge.booster;

import com.lancas.vswap.content.block.blocks.artillery.breech.helper.LoadedMunitionData;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.block.blocks.cartridge.primer.IPrimer;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.behaviour.PropellingForceHandler;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.component.behviour.ShipAdditionalSaver;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.ShipAdditionalSavedData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.List;

import static com.lancas.vswap.content.block.blocks.cartridge.primer.TorpedoPrimer.TorpedoPrimerLikeFire;

//todo trailer
public class RocketBooster extends BlockPlus implements ISandBoxBallisticBlock, IPrimer {
    public static final double ROCKET_BOOSTER_PRIMER_SPE = 0.4;

    public RocketBooster(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() { return BlockPlus.addersIfAbsent(BlockPlus.class, () -> List.of(
        new DefaultCartridgeAdder(false)
    ));}

    //public static final int BOOST_DELAY = 35;
    public static final double BOOST_SPE = 4.0;
    public static final int BOOST_TICKS = 20;
    public void modifyFlyingContext(ServerLevel level, SandBoxServerShip ship, BallisticData ballisticData, BallisticPos ballisticPos, BlockState state, BallisticFlyingContext ctx) {
        if (ballisticPos.fromTail() != 0) {  //must be tail, or all flows flame Throughable
            var blockReader = ship.getBlockCluster().getDataReader();

            int thisFromTail = ballisticPos.fromTail();
            boolean flameIsBlocked = false;
            for (int i = thisFromTail - 1; i >= 0; --i) {
                BallisticPos bPos = ballisticData.initialStateData.getPosFromTail(i);
                if (bPos == null) {
                    EzDebug.warn("ballistic from tail " + i + " shouldn't be null!");
                    continue;
                }

                if (!(blockReader.getBlockState(bPos.localPos()).getBlock() instanceof IFlameThroughable)) {
                    flameIsBlocked = true;
                    break;
                }
            }

            if (flameIsBlocked)
                return;
        }

        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();
        IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();

        Vector3dc vel = rigidReader.getVelocity();
        if (/*vel.y() > 0 || */vel.lengthSquared() < 1E-14)
            return;  //must have vel, and ship should be dropping
        if (vel.lengthSquared() < 1E-14)
            return;
        if (vel.y() - 9.8 * 0.05 > 0)  //if next tick ship will drop, start boosting
            return;

        Vector3dc pos = ship.getRigidbody().getDataReader().getPosition();

        ShipAdditionalSaver saver = ShipAdditionalSaver.getOrCreate(ship);
        ShipAdditionalSavedData savedData = saver.getData();
        CompoundTag savedTag = savedData.computeIfAbsent(ballisticPos.localPos().toString(),
            k -> new NbtBuilder()
                //.putInt("delayed_ticks", 0)
                .putInt("boosted_ticks", 0)
                .get()
        );

        //int delayedTicks = savedTag.getInt("delayed_ticks");
        int boostedTicks = savedTag.getInt("boosted_ticks");

        /*if (delayedTicks < BOOST_DELAY) {
            savedTag.putInt("delayed_ticks", ++delayedTicks);
            savedData.put(ballisticPos.localPos().toString(), savedTag);
        } else {
            if (boostedTicks < BOOST_TIME) {
                savedTag.putInt("boosted_ticks", ++boostedTicks);
                ctx.gravity.add(vel.normalize(PropellingForceHandler.STD_PROPELLANT_ENERGY * 0.0167, new Vector3d()));
                savedData.put(ballisticPos.localPos().toString(), savedTag);

                level.addParticle(ParticleTypes.POOF);
            }
        }*/
        if (boostedTicks < BOOST_TICKS) {
            double totalEnergy = BOOST_SPE * PropellingForceHandler.STD_PROPELLANT_ENERGY;
            //double tickEnergy = BOOST_SPE * PropellingForceHandler.STD_PROPELLANT_ENERGY * 0.05;
            double postVelLen = Math.sqrt(totalEnergy * 2 / rigidReader.getMass() + vel.lengthSquared());
            Vector3dc force = vel.normalize(Math.abs(vel.length() - postVelLen) / (BOOST_TICKS * 0.05), new Vector3d());

            ctx.gravity.add(force);
            /*rigidWriter.updateVelocity(v -> {
                double postEng = tickEnergy * 2 / rigidReader.getMass() + v.lengthSquared();
                Vector3d newV = v.normalize(Math.sqrt(postEng), new Vector3d());
                return newV;
            });*/
            //rigidWriter.applyWork(tickEnergy);
            /*double targetVelLength = Math.sqrt(vel.lengthSquared() + 2 * tickEnergy / rigidReader.getMass());

            if (!Double.isFinite(targetVelLength)) {
                EzDebug.warn("target vel is not valid, fail to apply rocket force");
            }
            double totalTime = BOOST_TIME * 0.05;
            double totalTravel = vel.length() * totalTime;
            Vector3d force = vel.normalize(totalEnergy / totalTravel, new Vector3d());
            EzDebug.warn("force:" + StrUtil.F2(force));*/

            savedTag.putInt("boosted_ticks", ++boostedTicks);
            //ctx.gravity.add(force);
            savedData.put(ballisticPos.localPos().toString(), savedTag);

            level.sendParticles(ParticleTypes.POOF, pos.x(), pos.y(), pos.z(), 1, 0.0, 0.0, 0.0, 0.0);
        }

    }



    @Override
    public boolean isTriggered(BlockState state) {
        return false;  //todo have triggered state?
    }
    @Override
    public BlockState getTriggeredState(BlockState prevState) {
        return prevState;  //todo have triggered state?
    }

    @Override
    public @Nullable BlockClusterData fire(ServerLevel level, List<LoadedMunitionData> loaded, Dest<Double> speDest, List<ItemStack> munitionRemains) {
        speDest.set(0.0);
        BlockClusterData blockData = TorpedoPrimerLikeFire.apply(level, RocketBooster.class, loaded, munitionRemains);

        if (blockData != null)
            speDest.set(ROCKET_BOOSTER_PRIMER_SPE);

        return blockData;

    }
}
