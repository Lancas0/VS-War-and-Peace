package com.lancas.vswap.content.block.blocks.cartridge.glider;

import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.debug.CreateLinePacketS2C;
import com.lancas.vswap.foundation.network.debug.CreateOutlinePacketS2C;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxHierarchyBehaviour;
import com.lancas.vswap.subproject.sandbox.component.behviour.ShipAdditionalSaver;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.HierarchyData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.FixedConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Glider extends BlockPlus implements ISandBoxBallisticBlock {
    public static BooleanProperty Gliding = BooleanProperty.create("gliding");

    public static Function<BlockState, BlockState> WING_STATER = s -> WapBlocks.Cartridge.GLIDER_WING.getDefaultState().setValue(DirectionAdder.FACING, s.getValue(DirectionAdder.FACING));

    public Glider(Properties p_49795_) {
        super(p_49795_);

    }
    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(Glider.class, () -> List.of(
            //new ShapeByStateAdder(s -> Shapes.block()),
            new DefaultCartridgeAdder(),
            new PropertyAdder<>(Gliding, false)
        ));
    }

    /*@Override
    public void onExitBarrel(ServerLevel level, SandBoxServerShip onShip, Vector3ic localPos) {
        //onShip.set
    }

    @Override
    public void physTick(SandBoxServerShip ship, BallisticData ballisticData) {

    }*/

    public static double LIFT_CONSTANT = 2;
    @Override
    public void modifyFlyingContext(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, BlockState state, BallisticFlyingContext ctx) {
        IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();
        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();
        /*if (!level.getFluidState(JomlUtil.bpContaining(rigidReader.getPosition())).isEmpty()) {
            //todo when in water, apply high drag

        }*/

        Vector3dc vel = rigidReader.getVelocity();
        if (vel.y() < 0) {
            if (!state.getValue(Gliding)) {  //set gliding if not
                state = state.setValue(Gliding, true);
                ship.getBlockCluster().getDataWriter().setBlock(ballisticPos.localPos(), state);
            }

            tickAnimation(level, ship, ballisticPos, state);

            //FIXME since localForwrad is always (0, 0, 1) now , I'm using (0, 1, 0)as local Y
            Vector3d lift = rigidReader.localToWorldNoScaleDir(new Vector3d(0, 1, 0)).mul(LIFT_CONSTANT * vel.y() * vel.y());
            ctx.gravity.add(lift);
            ctx.displacementIntensity /= 2;
        }

        Vector3dc worldPos = rigidReader.getPosition();
        Vector3dc x = rigidReader.localToWorldNoScaleDir(new Vector3d(1, 0, 0));
        Vector3dc y = rigidReader.localToWorldNoScaleDir(new Vector3d(0, 1, 0));
        Vector3dc z = rigidReader.localToWorldNoScaleDir(new Vector3d(0, 0, 1));

        NetworkHandler.sendToAllPlayers(new CreateLinePacketS2C("glider_x", worldPos, x.mul(4, new Vector3d()).add(worldPos)));
        NetworkHandler.sendToAllPlayers(new CreateLinePacketS2C("glider_y", worldPos, y.mul(4, new Vector3d()).add(worldPos)));
        NetworkHandler.sendToAllPlayers(new CreateLinePacketS2C("glider_z", worldPos, z.mul(4, new Vector3d()).add(worldPos)));


    }

    private static final double MAX_ANIMATE_TICK = 30;
    private static final int MAX_SCALE = 3;
    private static void tickAnimation(ServerLevel level, SandBoxServerShip ship, BallisticPos gliderPos, BlockState state) {
        var savedData = ShipAdditionalSaver.getOrCreate(ship).getData();
        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);

        SandBoxHierarchyBehaviour hierarchy = ship.getBehaviour(SandBoxHierarchyBehaviour.class);
        if (hierarchy == null) {
            hierarchy = new SandBoxHierarchyBehaviour();
            ship.addBehaviour(hierarchy, new HierarchyData());
        }


        //use pos as key
        SandBoxHierarchyBehaviour finalHierarchy = hierarchy;

        CompoundTag tag = savedData.computeIfAbsent(gliderPos.localPos().toString(), k -> {
            RigidbodyData rigid = new RigidbodyData();
            rigid.transform.position.set(ship.getRigidbody().getDataReader().localIToWorldPos(gliderPos.localPos()));//initial position
                //.setScale(new Vector3d(0, 0, 0)); //initial position
            BlockClusterData blockData = BlockClusterData.BlockAtCenter(WING_STATER.apply(state));

            SandBoxServerShip glider1AnimateShip = new SandBoxServerShip(UUID.randomUUID(), rigid, blockData);
            SandBoxServerWorld.addShip(level, glider1AnimateShip, true);

            SandBoxServerShip glider2AnimateShip = new SandBoxServerShip(UUID.randomUUID(), rigid, blockData);  //data will be copied, it's fine to use one data
            SandBoxServerWorld.addShip(level, glider2AnimateShip, true);

            //SliderConstraint constraint = new SliderConstraint(UUID.randomUUID(), ship.getUuid(), gliderAnimateShip.getUuid(), JomlUtil.d(gliderPos.localPos()), new Vector3d(0, 0, 0), new Vector3d(0, 0,  1));
            //constraint.setFixedDistance(0.0);
            FixedConstraint constraint1 = new FixedConstraint(
                UUID.randomUUID(), ship.getUuid(), glider1AnimateShip.getUuid(),
                JomlUtil.d(gliderPos.localPos()), new Vector3d(0, 0, 0),
                new Quaterniond(), new Quaterniond()
            );
            saWorld.getConstraintSolver().addConstraint(constraint1);

            FixedConstraint constraint2 = new FixedConstraint(
                UUID.randomUUID(), ship.getUuid(), glider2AnimateShip.getUuid(),
                JomlUtil.d(gliderPos.localPos()), new Vector3d(0, 0, 0),
                new Quaterniond(), new Quaterniond()
            );
            saWorld.getConstraintSolver().addConstraint(constraint2);

            finalHierarchy.addChild(glider1AnimateShip, constraint1);
            finalHierarchy.addChild(glider2AnimateShip, constraint2);

            return new NbtBuilder()
                .putUUID("animate_ship1_uuid", glider1AnimateShip.getUuid())
                .putUUID("animate_ship2_uuid", glider2AnimateShip.getUuid())
                .putInt("ticks", 0)
                .get();
        });

        int ticks = tag.getInt("ticks");

        double animeProgress = Math.min(ticks / MAX_ANIMATE_TICK, 1.0);

        BiConsumer<Boolean, @Nullable UUID> animateTickConstraint = (isReverse, childConstraintUuid) -> {
            Optional.ofNullable(childConstraintUuid)
                .map(x -> (IConstraint)saWorld.getConstraintSolver().getConstraint(x))
                .map(c -> {
                    if (c instanceof FixedConstraint fixed)
                        return fixed;
                    return null;
                })
                .ifPresentOrElse(
                    c -> c.setLocalAttAPos(JomlUtil.d(gliderPos.localPos()).add(animeProgress * (isReverse ? -0.5 : 0.5) , 0, 0)),
                    () ->  EzDebug.warn("fail to get constraint, animation will not update")
                );
        };

        animateTickConstraint.accept(false, finalHierarchy.getConstraintUuidOfChild(tag.getUUID("animate_ship1_uuid")));
        animateTickConstraint.accept(true, finalHierarchy.getConstraintUuidOfChild(tag.getUUID("animate_ship2_uuid")));

        tag.putInt("ticks", ticks + 1);
        savedData.put(gliderPos.localPos().toString(), tag);
    }

}
