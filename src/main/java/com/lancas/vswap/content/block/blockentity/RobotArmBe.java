package com.lancas.vswap.content.block.blockentity;

/*
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.sandbox.industry.ConstructingShipBehaviour;
import com.lancas.vswap.subproject.lostandfound.content.LostAndFoundBehaviour;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.StrUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.primitives.AABBd;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class RobotArmBe extends KineticBlockEntity implements IHaveGoggleInformation {
    public RobotArmBe(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    protected UUID armTargetBeUuid = null;
    //point mode = take : take item to assemble
    //point mode = des  : take assembled ship
    protected ArmInteractionPoint.Mode armPointMode = null;
    protected UUID targetConstructingShipUuid = null;  //assemble target or take target

    protected ArmBlockEntity.Phase phase = ArmBlockEntity.Phase.SEARCH_INPUTS;
    protected boolean redstoneLocked = false;
    protected ItemStack holdItem = ItemStack.EMPTY;
    protected float chasedPointProgress = 0;

    //not save
    protected ArmInteractionPoint armPoint = null;
    protected LerpedFloat baseAngle = LerpedFloat.angular();

    //todo smart be should be able to auto sync? or use first read nbt already
    /.*@Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide)
            notifyUpdate();  //server side initial update
    }*./

    /.*@Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (armTargetBeUuid != null)
            tag.putUUID("arm_target_uuid", armTargetBeUuid);
        if (armPointMode != null)
            NbtBuilder.modify(tag).putEnum("arm_point_mode", armPointMode);
    }
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.contains("arm_target_uuid"))
            armTargetBeUuid = tag.getUUID("arm_target_uuid");
        if (tag.contains("arm_point_mode"))
            armPointMode = NbtBuilder.modify(tag).getEnum("arm_point_mode", ArmInteractionPoint.Mode.class);
    }*./

    /.*public void setArmPoint(UUID armTargetUuid, ArmInteractionPoint.Mode mode) {
        armTargetBeUuid = armTargetUuid;
        armPointMode = mode;

        CompoundTag tag = new CompoundTag();
        write(tag, false);
        EzDebug.log("arm be tag:" + tag);
    }*./
    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        //AllBlockEntityTypes.MECHANICAL_ARM

        NbtBuilder.modify(tag)
            .putIfNonNull("arm_target_uuid", armTargetBeUuid, NbtBuilder::putUUID)
            .putIfNonNull("arm_point_mode", armPointMode, NbtBuilder::putEnum)
            .putIfNonNull("target_constructing_ship_uuid", targetConstructingShipUuid, NbtBuilder::putUUID)
            .putEnum("phase", phase)
            .putBoolean("redstone_locked", redstoneLocked)
            .putCompound("hold_item", holdItem.serializeNBT())
            .putFloat("chased_point_progress", chasedPointProgress);
    }
    //to override?
    /.*@Override
    public void writeSafe(CompoundTag compound) {
        super.writeSafe(compound);
        this.writeInteractionPoints(compound);
    }*./
    protected void read(CompoundTag tag, boolean clientPacket) {
        EzDebug.log("read tag:" +tag);
        NbtBuilder.modify(tag)
            .readDoIfExist("arm_target_uuid", v -> armTargetBeUuid = v, NbtBuilder::getUUID)
            .readDoIfExist("arm_point_mode", v -> armPointMode = v, (b, k) -> b.getEnum(k,  ArmInteractionPoint.Mode.class))
            .readDoIfExist("target_constructing_ship_uuid", v -> targetConstructingShipUuid = v, NbtBuilder::getUUID)
            .readEnumDo("phase", ArmBlockEntity.Phase.class, v -> phase = v)
            .readBooleanDo("redstone_locked", v -> redstoneLocked = v)
            .readCompoundDo("hold_item", t -> holdItem = ItemStack.of(t))
            .readFloatDo("chased_point_progress", v -> chasedPointProgress = v);

        /.*if (clientPacket) {
            if (hadGoggles != this.goggles) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> InstancedRenderDispatcher.enqueueUpdate(this));
            }

            boolean ceiling = this.isOnCeiling();
            if (interactionPointTagBefore == null || interactionPointTagBefore.size() != this.interactionPointTag.size()) {
                this.updateInteractionPoints = true;
            }

            if (previousIndex != this.chasedPointIndex || previousPhase != this.phase) {
                ArmInteractionPoint previousPoint = null;
                if (previousPhase == ArmBlockEntity.Phase.MOVE_TO_INPUT && previousIndex < this.inputs.size()) {
                    previousPoint = (ArmInteractionPoint)this.inputs.get(previousIndex);
                }

                if (previousPhase == ArmBlockEntity.Phase.MOVE_TO_OUTPUT && previousIndex < this.outputs.size()) {
                    previousPoint = (ArmInteractionPoint)this.outputs.get(previousIndex);
                }

                this.previousTarget = previousPoint == null ? ArmAngleTarget.NO_TARGET : previousPoint.getTargetAngles(this.worldPosition, ceiling);
                if (previousPoint != null) {
                    this.previousBaseAngle = this.previousTarget.baseAngle;
                }

                ArmInteractionPoint targetedPoint = this.getTargetedInteractionPoint();
                if (targetedPoint != null) {
                    targetedPoint.updateCachedState();
                }
            }
        }*./
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean result = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        if (level == null)
            return result;

        //EzDebug.log("add goggle tooltip side:" + EffectiveSide.get());
        RobotArm.showRange(level, worldPosition);
        //EzDebug.log("arm is null?:" + (armPoint == null));
        if (armPoint != null) {
            boolean armPointInRange = RobotArm.isInRange(level, RobotArm.getRange(level, worldPosition), armPoint.getPos());
            RobotArm.showArmPoint(armPoint, armPointInRange);
        }
        return result;
    }

    //private final LazyTicks lazy = new LazyTicks(10);
    //private final LazyTicks lazyArmPointUpdate = new LazyTicks(1);

    @Override
    public void lazyTick() {
        super.lazyTick();

        updateArmPoint();

        if (!(level instanceof ServerLevel sLevel)) return;
        AABBd range = RobotArm.getRange(sLevel, worldPosition);  //todo saved as be data?

        if (armPoint != null) {
            boolean armPointInRange = RobotArm.isInRange(level, range, armPoint.getPos());
            if (armPointInRange) {
                ItemStack extract = armPoint.extract(0, true);
                EzDebug.log("extract:" + extract);
            }
        }

        Stream<ConstructingShipBehaviour> constructingBehs = RobotArm.getInRangeConstructingBehaviour(sLevel, range);
        Iterator<ConstructingShipBehaviour> constructingBehIt = constructingBehs.iterator();

        while (constructingBehIt.hasNext()) {
            ConstructingShipBehaviour behaviour = constructingBehIt.next();

            behaviour.creativePutMaterial();
            EzDebug.highlight("successfully put a material");

            if (true) return;  //if successful then start place rountine
        }


    }


    protected void updateArmPoint() {
        if (level == null)
            return;

        if (armTargetBeUuid == null)
            return;

        BlockPos claimedBp = LostAndFoundBehaviour.latestClaimBp.get(armTargetBeUuid);
        if (claimedBp == null)
            return;

        BlockState claimedState = level.getBlockState(claimedBp);
        armPoint = ArmInteractionPoint.create(level, claimedBp, claimedState);//armPointType.createPoint(sLevel, claimedBp, claimedState);
        EzDebug.log("client?:" + level.isClientSide + ", at " + claimedBp.toShortString() + ", state:" + StrUtil.getBlockName(claimedState) + ", point:" + armPoint);

        if (armPoint != null) {
            if (armPointMode == null) {
                EzDebug.warn("don't except armPointMode be null when get non-null armPoint, will initial it.");
                armPointMode = armPoint.getMode();
            } else {
                if (armPoint.getMode() != armPointMode)
                    armPoint.cycleMode();
            }
        }
    }

    /.*private boolean tickMovementProgress() {
        boolean targetReachedPreviously = this.chasedPointProgress >= 1.0F;
        this.chasedPointProgress += Math.min(256.0F, Math.abs(this.getSpeed())) / 1024.0F;
        if (this.chasedPointProgress > 1.0F) {
            this.chasedPointProgress = 1.0F;
        }

        if (level == null || !level.isClientSide)
            return !targetReachedPreviously && this.chasedPointProgress >= 1f;


        ArmInteractionPoint targetedInteractionPoint = this.getTargetedInteractionPoint();
        ArmAngleTarget previousTarget = this.previousTarget;
        ArmAngleTarget target = targetedInteractionPoint == null ? ArmAngleTarget.NO_TARGET : targetedInteractionPoint.getTargetAngles(this.worldPosition, this.isOnCeiling());


        ArmAngleHelper.TargetAngles targetAngles = ArmAngleHelper.getTargetAngles(
            level, worldPosition, LostAndFoundBehaviour.latestClaimBp
            );


        this.baseAngle.setValue(AngleHelper.angleLerp(this.chasedPointProgress, (double)this.previousBaseAngle, target == ArmAngleTarget.NO_TARGET ? (double)this.previousBaseAngle : (double)target.baseAngle));
        new ArmAngleTarget(armPos, this.getInteractionPositionVector(), this.getInteractionDirection(), ceiling);


        if (this.chasedPointProgress < 0.5F) {
            target = ArmAngleTarget.NO_TARGET;
        } else {
            previousTarget = ArmAngleTarget.NO_TARGET;
        }

        float progress = this.chasedPointProgress == 1.0F ? 1.0F : this.chasedPointProgress % 0.5F * 2.0F;
        this.lowerArmAngle.setValue((double) Mth.lerp(progress, previousTarget.lowerArmAngle, target.lowerArmAngle));
        this.upperArmAngle.setValue((double)Mth.lerp(progress, previousTarget.upperArmAngle, target.upperArmAngle));
        this.headAngle.setValue((double)AngleHelper.angleLerp((double)progress, (double)(previousTarget.headAngle % 360.0F), (double)(target.headAngle % 360.0F)));
        return false;
    }
    private boolean tickMovementToAssembleShip() {
        if (level == null || targetConstructingShipUuid == null)
            return false;

        ISandBoxShip ship = ISandBoxWorld.fromLevel(level).getShip(targetConstructingShipUuid);
        if (ship == null) {
            EzDebug.warn("failed to find constructing ship");
            targetConstructingShipUuid = null;
            return false;
        }

        Vector3d targetPos = ship.getRigidbody().getDataReader().get;
    }*./

    protected boolean isOnCeiling() {
        BlockState state = this.getBlockState();
        return this.hasLevel() && state.getOptionalValue(ArmBlock.CEILING).orElse(false);
    }
}
*/