package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.lancas.vswap.content.block.blocks.artillery.breech.helper.BreechHelper;
import com.lancas.vswap.content.block.blocks.artillery.breech.helper.LoadedMunitionData;
import com.lancas.vswap.content.block.blocks.cartridge.primer.IPrimer;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.StrUtil;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.lancas.vswap.content.block.blocks.artillery.breech.IBreech.*;

public class BreechMunitionHolderBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<BreechMunitionHolderBehaviour> TYPE = new BehaviourType<BreechMunitionHolderBehaviour>();

    protected final List<LoadedMunitionData> loadedMunitionData = new ArrayList<>();

    public BreechMunitionHolderBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviourType<?> getType() { return TYPE; }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        if (clientPacket)
            return;

        NbtBuilder.modify(nbt).putEachSimpleJackson("loaded_ship_data", loadedMunitionData);
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        if (clientPacket)
            return;

        loadedMunitionData.clear();
        NbtBuilder.modify(nbt).readEachSimpleJackson("loaded_ship_data", LoadedMunitionData.class, loadedMunitionData);
    }

    @Override
    public void destroy() {
        super.destroy();
        unloadMunition();
    }

    public boolean loadMunition(/*todo transfer artillery ship as param?*/BlockClusterData munitionBlockData) {
        if (!(getWorld() instanceof ServerLevel level))
            return false;

        ServerShip artilleryShip = ShipUtil.getServerShipAt(level, getPos());

        int loadingLen = munitionBlockData.getBlockCnt();
        if (loadingLen <= 0) return false;

        BlockState breechState = blockEntity.getBlockState();
        if (!breechState.hasProperty(DirectionAdder.FACING)) {
            EzDebug.warn("the breech " + StrUtil.getBlockName(breechState) + " don't have direction property!");
            return false;
        }
        Direction breechBlockDir = breechState.getValue(DirectionAdder.FACING);

        //make sa ship
        RigidbodyData rigidbodyData = new RigidbodyData();
        SandBoxServerShip munitionShip = new SandBoxServerShip(UUID.randomUUID(), rigidbodyData, munitionBlockData);

        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        SandBoxServerWorld.addShip(level, munitionShip, true);

        ISliderOrientationConstraint newConstraint = BreechHelper.makeConstraint(level, artilleryShip, munitionShip, getPos(), breechBlockDir);
        saWorld.getConstraintSolver().addConstraint(newConstraint);

        for (int i = loadedMunitionData.size() - 1; i >= 0; --i) {
            ISliderOrientationConstraint c = loadedMunitionData.get(i).getConstraint(level);//saWorld.getConstraintSolver().getConstraint(loadedMunitionData.get(i).constraintUuid());
            if (c == null) {
                EzDebug.warn("the constraint is null, may the ship is already removed, will remove this loaded ship");
                loadedMunitionData.remove(i);
                continue;
            }

            c.addFixedDistance(loadingLen);
            //EzDebug.warn("add loading len:" + loadingLen);
        }

        loadedMunitionData.add(new LoadedMunitionData(munitionShip.getUuid(), newConstraint.getUuid()));
        blockEntity.notifyUpdate();
        //ShipUtil.deleteShip(sLevel, toLoadShip);
        return true;
    }

    public void unloadMunition() {
        if (!(getWorld() instanceof ServerLevel level))
            return;
        BlockState breechState = blockEntity.getBlockState();
        if (!breechState.hasProperty(DirectionAdder.FACING)) {
            EzDebug.error("breech block has no direction property");
            return;
        }

        ServerShip artilleryShip = ShipUtil.getServerShipAt(level, getPos());
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(artilleryShip, getPos());
        Vector3d worldLaunchDir = WorldUtil.getWorldDirection(artilleryShip, breechState.getValue(DirectionAdder.FACING));

        Vector3d throwDir = worldLaunchDir.negate(new Vector3d());
        Vector3d throwDeltaMove = throwDir.mul(0.2, new Vector3d());
        Vector3d spawnPos = worldBreechPos.add(throwDir, new Vector3d());

        BreechHelper.ejectAllMunition(level, loadedMunitionData, () -> spawnPos, () -> throwDeltaMove);
        loadedMunitionData.forEach(x -> SandBoxServerWorld.markShipDeleted(level, x.shipUuid()));  //constraint will auto removed
        loadedMunitionData.clear();

        blockEntity.notifyUpdate();
    }

    public void updateConstraints() {
        if (!(getWorld() instanceof ServerLevel level))
            return;

        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(level, getPos());

        BlockState breechState = blockEntity.getBlockState();
        if (!breechState.hasProperty(DirectionAdder.FACING)) {
            EzDebug.error("breech block has no direction property");
            return;
        }
        Direction breechBlockDir = breechState.getValue(DirectionAdder.FACING);

        //remake loadedMunitionData
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        List<LoadedMunitionData> oldLoadedMunitionData = new ArrayList<>(loadedMunitionData);
        loadedMunitionData.clear();
        for (int i = 0; i < oldLoadedMunitionData.size(); ++i) {
            var cur = oldLoadedMunitionData.get(i);
            SandBoxServerShip curMunition = cur.getShip(level);
            if (curMunition == null) {
                EzDebug.warn("fail to get loaded munition ship, may it's removed by command");
                continue;
            }

            //clear prev constraint
            saWorld.getConstraintSolver().markConstraintRemoved(cur.constraintUuid());
            //remake constraint
            ISliderOrientationConstraint newConstraint = BreechHelper.makeConstraint(level, artilleryShip, curMunition, getPos(), breechBlockDir);

            saWorld.getConstraintSolver().addConstraint(newConstraint);

            loadedMunitionData.add(new LoadedMunitionData(curMunition.getUuid(), newConstraint.getUuid()));
        }

        blockEntity.notifyUpdate();
    }
}
