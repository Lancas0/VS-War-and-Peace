package com.lancas.vswap.content.block.blocks.artillery.breech.helper;

/*
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.compact.vs.constraint.OrientationOnVsConstraint;
import com.lancas.vswap.subproject.sandbox.compact.vs.constraint.SliderOnVsConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.BiCompoundConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.OrientationConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.SliderConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.IOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

import static com.lancas.vswap.content.block.blocks.artillery.breech.IBreech.LOADED_MUNITION_DIRECTION;
import static com.lancas.vswap.content.block.blocks.artillery.breech.IBreech.LOADED_MUNITION_ORIGIN_D;

public class LoadedMunitionConstraint extends BiCompoundConstraint {
    private LoadedMunitionConstraint() { super(null, null, null); }
    public LoadedMunitionConstraint(UUID inSelfUuid, ISliderConstraint inC1, IOrientationConstraint inC2) {
        super(inSelfUuid, inC1, inC2);
    }
    public static LoadedMunitionConstraint onGround(ServerLevel level, UUID munitionUuid, BlockPos attachOn, Direction attachDir) {
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);

        SliderConstraint slider = new SliderConstraint(
            UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), munitionUuid,
            JomlUtil.dCenter(attachOn), LOADED_MUNITION_ORIGIN_D,
            JomlUtil.dNormal(attachDir)
        );
        slider.setFixedDistance(0.0);
        OrientationConstraint ori = new OrientationConstraint(
            UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), munitionUuid,
            HoldableAttachment.rotateForwardToDirection(attachDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
        );

        return new LoadedMunitionConstraint(UUID.randomUUID(), slider, ori);
    }
    public static LoadedMunitionConstraint onVsShip(ServerLevel level, long attachOnVsId, UUID munitionUuid, BlockPos attachOn, Direction attachDir) {
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);

        SliderOnVsConstraint slider = new SliderOnVsConstraint(
            UUID.randomUUID(), attachOnVsId, munitionUuid,
            JomlUtil.dCenter(attachOn), LOADED_MUNITION_ORIGIN_D,
            JomlUtil.dNormal(attachDir)
        );
        slider.setFixedDistance(0.0);
        OrientationOnVsConstraint ori = new OrientationOnVsConstraint(
            UUID.randomUUID(), attachOnVsId, munitionUuid,
            HoldableAttachment.rotateForwardToDirection(attachDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
        );

        return new LoadedMunitionConstraint(UUID.randomUUID(), slider, ori);
    }
    //public LoadedMunitionConstraint(UUID inSelfUuid, UUID attachOnUuid, UUID munitionUuid, )

    public ISliderConstraint getSliderConstraint() { return (ISliderConstraint)super.getFirst(); }
    public IOrientationConstraint getOrientationConstraint() { return (IOrientationConstraint)super.getSecond(); }
}*/