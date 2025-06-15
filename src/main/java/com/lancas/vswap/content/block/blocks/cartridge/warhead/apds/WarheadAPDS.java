package com.lancas.vswap.content.block.blocks.cartridge.warhead.apds;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.AbstractApWarhead;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.joml.primitives.AABBd;

import java.util.List;
import java.util.function.Function;

public class WarheadAPDS extends AbstractApWarhead {
    public static final BooleanProperty SHELLED = BooleanProperty.create("shelled");

    public static final Function<BlockState, BlockState> SHELLED_STATER = s -> s.setValue(SHELLED, true);
    public static final Function<BlockState, BlockState> UNSHELLED_STATER = s -> s.setValue(SHELLED, false);
    //public static final Function<BlockState, BlockState> QUARTER_SHELL_STATER = s ->
    //    WapBlocks.Cartridge.Warhead.APFSDS_QUARTER_SHELL.getDefaultState().setValue(DirectionAdder.FACING, s.getValue(DirectionAdder.FACING));

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            WarheadAPDS.class,
            () -> List.of(
                new DefaultCartridgeAdder(true),
                new PropertyAdder<>(SHELLED, true)
            )
        );
    }

    public WarheadAPDS(Properties p_49795_) {
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
        if (!(preState.getBlock() instanceof WarheadAPDS)) {
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
