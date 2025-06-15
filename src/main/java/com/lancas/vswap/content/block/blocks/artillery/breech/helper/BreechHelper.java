package com.lancas.vswap.content.block.blocks.artillery.breech.helper;

import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.compact.vs.constraint.SliderOrientationOnVsConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.SliderOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static com.lancas.vswap.content.block.blocks.artillery.breech.IBreech.*;

public class BreechHelper {

    public static void ejectAllMunition(ServerLevel level, List<LoadedMunitionData> loaded, Supplier<Vector3dc> randSpawnPosGetter, Supplier<Vector3dc> randDeltaMoveGetter/*, boolean clear*/) {
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        loaded.stream()
            .map(x -> saWorld.getShip(x.shipUuid()))
            .filter(Objects::nonNull)
            .forEach(s -> {
                if (s.getBlockCluster().getDataReader().getBlockCnt() <= 0) return;

                ItemStack shellStack = Docker.stackOfSa(level, s);

                Vector3dc spawnPos = randSpawnPosGetter.get();
                Vector3dc deltaMove = randDeltaMoveGetter.get();

                ItemEntity itemE = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), shellStack);
                itemE.setDeltaMovement(deltaMove.x(), deltaMove.y(), deltaMove.z());
                level.addFreshEntity(itemE);
            });
    }
    public static void ejectAllRemainMunition(ServerLevel level, List<ItemStack> remainMunition, Supplier<Vector3dc> randSpawnPosGetter, Supplier<Vector3dc> randDeltaMoveGetter/*, boolean clear*/) {
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        remainMunition.stream()
            //.map(x -> saWorld.getShip(x.shipUuid()))
            .filter(Objects::nonNull)
            .forEach(x -> {
                //if (s.getBlockCluster().getDataReader().getBlockCnt() <= 0) return;

                //ItemStack shellStack = Docker.stackOfSa(level, s);

                Vector3dc spawnPos = randSpawnPosGetter.get();
                Vector3dc deltaMove = randDeltaMoveGetter.get();

                ItemEntity itemE = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), x);
                itemE.setDeltaMovement(deltaMove.x(), deltaMove.y(), deltaMove.z());
                level.addFreshEntity(itemE);
            });
    }

    public static ISliderOrientationConstraint makeConstraint(ServerLevel level, @Nullable ServerShip artilleryShip, @NotNull SandBoxServerShip munitionShip, BlockPos breechBp, Direction breechBlockDir) {
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        UUID munitionUuid = munitionShip.getUuid();

        if (artilleryShip == null) {
            return new SliderOrientationConstraint(
                UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), munitionUuid,
                JomlUtil.dCenter(breechBp), LOADED_MUNITION_ORIGIN_D,
                HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION),
                JomlUtil.dNormal(breechBlockDir)
            );
        } else {
            return new SliderOrientationOnVsConstraint(
                UUID.randomUUID(), artilleryShip.getId(), munitionUuid,
                JomlUtil.dCenter(breechBp), LOADED_MUNITION_ORIGIN_D,
                HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION),
                JomlUtil.dNormal(breechBlockDir)
            );
        }
    }
}
