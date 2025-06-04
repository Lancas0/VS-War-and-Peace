package com.lancas.vswap.content.block.blocks.cartridge.warhead.apfsds;

import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.AbstractApWarhead;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.ApCoreWarhead;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxExpireTicker;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.ExpireTickerData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.network.send.SendClientShipS2C;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.ItemUtil;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.RandUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.lang.Math;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class ApfsdsWarhead extends AbstractApWarhead {
    public static final BooleanProperty SHELLED = BooleanProperty.create("shelled");

    public static final Function<BlockState, BlockState> SHELLED_STATER = s -> s.setValue(SHELLED, true);
    public static final Function<BlockState, BlockState> UNSHELLED_STATER = s -> s.setValue(SHELLED, false);
    public static final Function<BlockState, BlockState> QUARTER_SHELL_STATER = s ->
        WapBlocks.Cartridge.Warhead.APFSDS_QUARTER_SHELL.getDefaultState().setValue(DirectionAdder.FACING, s.getValue(DirectionAdder.FACING));

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            ApfsdsWarhead.class,
            () -> List.of(
                new DefaultCartridgeAdder(),
                new PropertyAdder<>(SHELLED, true)
            )
        );
    }

    public ApfsdsWarhead(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public double getRaycastStep() { return 0.3; }

    @Override
    public double getRaycastPredictTime() { return 0.06; }

    @Override
    public AABBd getLocalBound(BlockState state) { return new AABBd(0.125, 0.125, 0.125, 0.875, 0.875, 0.875); }

    @Override
    public void onExitBarrel(ServerLevel level, SandBoxServerShip onShip, BallisticPos ballisticPos) {
        BlockState preState = onShip.getBlockCluster()
            .getDataReader()
            .getBlockState(ballisticPos.localPos());
        if (!(preState.getBlock() instanceof ApfsdsWarhead)) {
            EzDebug.warn("the block on ballstici ship at " + ballisticPos + " is not apfsds");
            return;
        }

        onShip.getBlockCluster()
            .getDataWriter()
            .setBlock(ballisticPos.localPos(), UNSHELLED_STATER.apply(preState));

        /*IRigidbodyDataReader onShipRigidReader = onShip.getRigidbody().getDataReader();
        Vector3dc onShipVel = onShipRigidReader.getVelocity();
        Vector3d omegaAxis = onShipVel.cross(new Vector3d(0, 1, 0), new Vector3d()).cross(onShipVel);
        Vector3dc onShipScale = onShipRigidReader.getScale();

        Direction warheadDir = preState.getValue(DirectionAdder.FACING);

        for (int i = 0; i < 4; ++i) {
            RigidbodyData rigidData = onShipRigidReader.getCopiedData();
            rigidData
                //.addPosition(JomlUtil.scaleVector3d(onShipScale, RandUtil.onSphere(1)))  //todo add pos may have something wrong
                .setVelocity(onShipVel.div(RandUtil.nextG(5, 0.5), new Vector3d()))
                .setRotation(new Quaterniond(new AxisAngle4d(Math.PI / 2.0 * i, onShipVel)).mul(onShipRigidReader.getRotation()))
                //.setOmega(omegaAxis.mul(Math.PI * RandUtil.nextG(0.3, 0.1), new Vector3d()))
                .setGravity(new Vector3d(0, -9.8, 0));  //make sure there is gravity, just for safe*./

            BlockClusterData blockData = BlockClusterData.BlockAtCenter(QUARTER_SHELL_STATER.apply(preState));

            SandBoxClientShip shellShip = new SandBoxClientShip(UUID.randomUUID(), rigidData, blockData);

            //Vector3d dropPos = JomlUtil.relativeFromCenter(onShipRigidReader.getPosition(), extractDir, 0.75);
            /.*Vector3d tickMovement = onShipVel.div(RandUtil.nextG(4, 0.5), new Vector3d()).mul(0.05);

            ItemStack shellStack = Docker.stackOfSa(level, shellShip);
            ItemUtil.dropNoRandom(level, shellStack, onShipRigidReader.getPosition(), e -> {
                e.setDeltaMovement(tickMovement.x, tickMovement.y, tickMovement.z);
            });*./
            /.*Optional.ofNullable(ItemUtil.dropNoRandom(level, shellStack, onShipRigidReader.getPosition()))
                .ifPresentOrElse(
                    e -> e.setDeltaMovement(tickMovement.x, tickMovement.y, tickMovement.z),
                    () -> EzDebug.warn("fail to spawn supporting sabot stack entity")
                );*./
            //itemHandler.setStackInSlot(finalAtSlot, ItemStack.EMPTY);


            shellShip.addBehaviour(new SandBoxExpireTicker(), new ExpireTickerData(100 * 3));  //after about 5s expire
            //SandBoxServerWorld.addShipAndSyncClient(level, shellShip);

            NetworkHandler.sendToAllPlayers(new SendClientShipS2C(
                VSGameUtilsKt.getDimensionId(level), shellShip
            ));
        }*/
    }
}
