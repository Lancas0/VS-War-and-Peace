package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.lancas.vswap.content.WapSounds;
import com.lancas.vswap.content.block.blocks.artillery.breech.helper.BreechHelper;
import com.lancas.vswap.content.block.blocks.artillery.breech.helper.LoadedMunitionData;
import com.lancas.vswap.content.block.blocks.cartridge.primer.IPrimer;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.saved.vs_constraint.ConstraintsMgr;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.register.PlayerScreenShakeEvt;
import com.lancas.vswap.sandbox.ballistics.behaviour.BallisticBehaviour;
import com.lancas.vswap.sandbox.ballistics.data.AirDragSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticBarrelContextSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticInitialStateSubData;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.lancas.vswap.content.block.blocks.artillery.breech.IBreech.*;

public class EjectingBreechBe extends SmartBlockEntity implements IBreechBe {
    /*protected class ArtilleryShipCache {
        private ServerShip cache;
        public @Nullable ServerShip get() {
            if (!(level instanceof ServerLevel sLevel))
                return null;

            if (!worldPosition.equals(lastKnownPos)) {
                //lastKnownPos = worldPosition;
                cache = ShipUtil.getServerShipAt(sLevel, worldPosition);
                //setChanged();  //change should be in tick
            }
            return cache;
        }
    }*/

    //protected final List<LoadedMunitionData> loadedMunitionData = new ArrayList<>();
    protected BlockPos lastKnownPos = null;

    //not saved, server only
    //protected final ArtilleryShipCache artilleryShipCache = new ArtilleryShipCache();

    public EjectingBreechBe(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {
        list.add(new BreechMunitionHolderBehaviour(this));
    }

    private final LazyTicks lazyTicks = new LazyTicks(5);

    private void updateLastKnownPos() {
        if (!(level instanceof ServerLevel sLevel))
            return;
        BreechMunitionHolderBehaviour munitionHolder = getBehaviour(BreechMunitionHolderBehaviour.TYPE);
        if (munitionHolder == null) {
            EzDebug.warn("fail to get munition hold behaviour");
            return;
        }

        if (!worldPosition.equals(lastKnownPos)) {
            munitionHolder.updateConstraints();
            lastKnownPos = worldPosition;
            notifyUpdate();
        }
    }
    @Override
    public void tick() {
        if (!(level instanceof ServerLevel sLevel))
            return;

        boolean anyChange = false;

        if (!worldPosition.equals(lastKnownPos)) {
            updateLastKnownPos();
            anyChange = true;
        }

        //load around ships
        if (lazyTicks.shouldWork()) {
            ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, worldPosition);
            @Nullable Long artilleryShipId = artilleryShip == null ? null : artilleryShip.getId();

            Vector3d breechWorldCenter = WorldUtil.getWorldCenter(sLevel, worldPosition);
            AABBd breechInnerBound = JomlUtil.dCenterExtended(breechWorldCenter, 0.35);  //todo hell breech will reload multi times?
            for (Ship ship : VSGameUtilsKt.getShipsIntersecting(sLevel, breechInnerBound)) {
                ServerShip sShip = (ServerShip)ship;

                if (Objects.equals(ship.getId(), artilleryShipId) || ConstraintsMgr.anyLoadedConstraintWith(sLevel, ship.getId()) || ICanHoldShip.isShipHolden(sShip))
                    continue;

                HoldableAttachment holdable = sShip.getAttachment(HoldableAttachment.class);
                if (holdable == null)
                    continue;

                Vector3dc worldHoldPivotCenter = WorldUtil.getWorldCenter(sShip, holdable.holdPivotBpInShip.toBp());
                AABBd holdPivotBound = JomlUtil.dCenterExtended(worldHoldPivotCenter, 0.5);

                if (breechInnerBound.intersectsAABB(holdPivotBound)) {
                    if (loadShipMunition(holdable)) {
                        ShipUtil.deleteShip(sLevel, sShip);
                        anyChange = true;
                    }
                }
            }
        }

        if (anyChange)
            notifyUpdate();
    }

    @Override
    public boolean canArmLoadDockerNow(ItemStack stack) {
        if (!(level instanceof ServerLevel sLevel))
            return false;

        BreechMunitionHolderBehaviour munitionHolder = getBehaviour(BreechMunitionHolderBehaviour.TYPE);
        if (munitionHolder == null) {
            EzDebug.warn("fail to get munition hold behaviour");
            return false;
        }

        if (!munitionHolder.loadedMunitionData.isEmpty())  //arm can only load one
            return false;

        if (!(stack.getItem() instanceof Docker)) return false;
        return true;  //todo: further check if it's really a munition
    }

    @Override
    public boolean loadShipMunition(@NotNull HoldableAttachment holdable) {
        if (!(level instanceof ServerLevel sLevel))
            return false;
        BreechMunitionHolderBehaviour munitionHolder = getBehaviour(BreechMunitionHolderBehaviour.TYPE);
        if (munitionHolder == null) {
            EzDebug.warn("fail to get munition hold behaviour");
            return false;
        }

        ServerShip toLoadShip = ShipUtil.getServerShipAt(sLevel, holdable.getPivotBpInShip());
        if (toLoadShip == null) {
            EzDebug.warn("try load null ship!");
            return false;
        }
        ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, worldPosition);
        if (artilleryShip != null && artilleryShip.getId() == toLoadShip.getId()) {
            EzDebug.warn("try load self ship!");
            return false;
        }

        BlockClusterData munitionBlockData = new BlockClusterData();

        //todo foreach for primer
        BlockPos startPos = holdable.getPivotBpInShip();

        BlockState primerState = sLevel.getBlockState(startPos);
        if (!(primerState.getBlock() instanceof IPrimer)) {
            EzDebug.log("[should send msg]The hold pos must be primer");
            return false;
        }

        Vector3i munitionLocPos = new Vector3i(LOADED_MUNITION_ORIGIN);
        Direction primerDir = primerState.getValue(DirectionAdder.FACING);
        BlockPos.MutableBlockPos curPos = startPos.mutable();
        while (true) {
            BlockState state = level.getBlockState(curPos);
            if (state.isAir()) break;

            //Vector3i localPos = JomlUtil.i(curPos.subtract(startPos));
            munitionBlockData.setBlock(munitionLocPos, state.trySetValue(BlockStateProperties.FACING, LOADED_MUNITION_DIRECTION));
            curPos.move(primerDir);
            munitionLocPos.add(LOADED_MUNITION_FORWARD);
            //curPos.move(holdable.forwardInShip);
        }

        return munitionHolder.loadMunition(munitionBlockData);
    }
    @Override
    public boolean loadDockerMunition(ItemStack stack) {
        if (!(stack.getItem() instanceof Docker))
            return false;
        if (!(level instanceof ServerLevel sLevel))
            return false;
        BreechMunitionHolderBehaviour munitionHolder = getBehaviour(BreechMunitionHolderBehaviour.TYPE);
        if (munitionHolder == null) {
            EzDebug.warn("fail to get munition hold behaviour");
            return false;
        }

        BlockState breechState = getBlockState();
        if (!breechState.hasProperty(DirectionAdder.FACING)) {
            EzDebug.warn("breech must have dir");
            return false;
        }
        Direction breechBlockDir = breechState.getValue(DirectionAdder.FACING);

        //todo testing if shipData can be loaded
        RRWChunkyShipSchemeData shipData = Docker.getShipData(stack);
        if (shipData == null) {
            EzDebug.warn("beech load docker ship fail because get null shipData");
            return false;
        }

        //AtomicReference<HoldableAttachment> holdableAtt = new AtomicReference<>(null);
        AtomicReference<BlockPos> savedLocalPivotBp = new AtomicReference<>(null);
        shipData.withSavedAttachmentDo(HoldableAttachment.class, (att, localPoses) -> {
            Vector3ic savedLocalPivot = att.getSavedLocalPivot(localPoses);
            if (savedLocalPivot == null)
                return;

            savedLocalPivotBp.set(JomlUtil.bp(savedLocalPivot));
        });

        if (savedLocalPivotBp.get() == null)
            return false;

        BlockState primerState = shipData.getBlockStateByLocalPos(savedLocalPivotBp.get());
        if (!(primerState.getBlock() instanceof IPrimer primer))
            return false;

        BlockClusterData munitionBlockData = new BlockClusterData();
        Vector3i curPos = new Vector3i(LOADED_MUNITION_ORIGIN);
        BlockPos.MutableBlockPos savedCurBp = savedLocalPivotBp.get().mutable();
        Direction primerDir = primerState.getValue(DirectionAdder.FACING);
        while (true) {
            BlockState curSavedState = shipData.getBlockStateByLocalPos(savedCurBp);
            if (curSavedState.isAir())
                break;

            munitionBlockData.setBlock(curPos, curSavedState.trySetValue(DirectionAdder.FACING, LOADED_MUNITION_DIRECTION));

            curPos.add(LOADED_MUNITION_FORWARD);
            savedCurBp.move(primerDir);
        }

        return munitionHolder.loadMunition(munitionBlockData);
    }

    @Override
    public void unloadMunition() {
        if (!(level instanceof ServerLevel sLevel))
            return;
        BreechMunitionHolderBehaviour munitionHolder = getBehaviour(BreechMunitionHolderBehaviour.TYPE);
        if (munitionHolder == null) {
            EzDebug.warn("fail to get munition hold behaviour");
            return;
        }

        munitionHolder.unloadMunition();
    }

    @Override
    public void fire() {
        if (!(level instanceof ServerLevel sLevel))
            return;
        BreechMunitionHolderBehaviour munitionHolder = getBehaviour(BreechMunitionHolderBehaviour.TYPE);
        if (munitionHolder == null) {
            EzDebug.warn("fail to get munition hold behaviour");
            return;
        }

        if (munitionHolder.loadedMunitionData.isEmpty())
            return;

        LoadedMunitionData lastLoaded = munitionHolder.loadedMunitionData.get(munitionHolder.loadedMunitionData.size() - 1);
        lastLoaded.getShip(sLevel);

        BlockState primerState = lastLoaded.getShip(sLevel).getBlockCluster().getDataReader().getBlockState(IBreech.LOADED_MUNITION_ORIGIN);
        if (!(primerState.getBlock() instanceof IPrimer primer))
            return;

        Dest<Double> speDest = new Dest<>();
        List<ItemStack> munitionRemains = new ArrayList<>();
        BlockClusterData projectileBlockData = primer.fire(sLevel, munitionHolder.loadedMunitionData, speDest, munitionRemains);

        munitionHolder.loadedMunitionData.forEach(x -> SandBoxServerWorld.markShipDeleted(sLevel, x.shipUuid()));  //constraint auto remove
        munitionHolder.loadedMunitionData.clear();
        notifyUpdate();

        if (speDest.get() > 0) {
            //play sound
            sLevel.playSound(null, worldPosition, WapSounds.ARTILLERY_FIRE0.get(), SoundSource.BLOCKS);
            //shakes
            PlayerScreenShakeEvt.setShakeTicksNoLessThanDefaultTicks();
        }


        @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, worldPosition);

        BlockState breechState = level.getBlockState(worldPosition);
        Vector3d worldBreechPos = WorldUtil.getWorldCenter(artilleryShip, worldPosition);
        Vector3d worldLaunchDir = WorldUtil.getWorldDirection(artilleryShip, breechState.getValue(DirectionAdder.FACING));

        Vector3d throwDir = worldLaunchDir.negate(new Vector3d());
        Vector3d throwDeltaMove = throwDir.mul(0.2, new Vector3d());
        Vector3d spawnPos = worldBreechPos.add(throwDir, new Vector3d());
        //throw remain items FIXME now will throw even no propellant power, keep it or fix it : keep it, or don't do anything when no propellant power
        BreechHelper.ejectAllRemainMunition(sLevel, munitionRemains, () -> spawnPos, () -> throwDeltaMove);


        if (projectileBlockData != null) {
            RigidbodyData rigidbodyData = new RigidbodyData(
                new TransformPrimitive(
                    worldBreechPos,
                    JomlUtil.swingYXRotateTo(IBreech.LOADED_MUNITION_FORWARD_D, worldLaunchDir, new Quaterniond()),
                    new Vector3d(1, 1, 1)  //todo use scale
                )
            );

            SandBoxServerShip ship = new SandBoxServerShip(
                UUID.randomUUID(),
                rigidbodyData,
                projectileBlockData
            );
            ship.addBehaviour(new BallisticBehaviour(), new BallisticData(
                new BallisticInitialStateSubData(worldBreechPos, IBreech.LOADED_MUNITION_FORWARD, worldLaunchDir, speDest.get()),
                new BallisticBarrelContextSubData(),
                new AirDragSubData()
            ));
            SandBoxServerWorld.addShip(sLevel, ship, true);
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (clientPacket)
            return;

        NbtBuilder.modify(tag).putIfNonNull("last_known_pos", lastKnownPos, NbtBuilder::putBlockPos);
    }
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (clientPacket)
            return;

        NbtBuilder.modify(tag).readDoIfExist("last_known_pos", v -> lastKnownPos = v, NbtBuilder::getBlockPos);
    }
}
