package com.lancas.vs_wap.content.blocks;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.obsolete.PlayerShipMgr;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint;
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import  org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint;

import java.lang.Math;

public class TestBlock extends DirectionalBlock {
    /*private final VoxelShape EAST_AABB = box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
    private final VoxelShape WEST_AABB = box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private final VoxelShape SOUTH_AABB = box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
    private final VoxelShape NORTH_AABB = box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
    private final VoxelShape UP_AABB =  box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final VoxelShape DOWN_AABB = box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    */
    public TestBlock(Properties p_49795_) { super(p_49795_); }

    private ServerShip getOnShip(ServerLevel level, BlockPos pos) {
        return ShipUtil.getServerShipAt(level, pos);
    }

    /*@Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_AABB;
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            case UP -> UP_AABB;
            default -> UP_AABB;
        };
    }*/

    @Override
    public void onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        //super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
        if (level.isClientSide) return;

        EzDebug.log("on test block placed");

        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) return;
        //TestForceInductor.getOrCreate(ship);

    }

    private static long lastSelectShipID = -1;

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_60508_) {
        //return super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
        if (level.isClientSide) return InteractionResult.PASS;
        if (true) return InteractionResult.PASS; //todo temp

        ServerShip playerShapeShip = PlayerShipMgr.getOrCreateShip((ServerLevel)level, player.getUUID());


        ServerLevel sLevel = (ServerLevel)level;

        ServerShip onShip = getOnShip(sLevel, pos);
        //if (onShip == null) return InteractionResult.PASS;

        // Create an empty ship
        ServerShip newShip = VSGameUtilsKt.getShipObjectWorld(sLevel).createNewShipAtBlock(
            JomlUtil.i(pos.above()),
            false,
            1.0,
            VSGameUtilsKt.getDimensionId(sLevel)
        );
        BlockPos shipCenterPos = new BlockPos(
            (int)Math.round(newShip.getTransform().getPositionInShip().x() - 0.5),
            (int)Math.round(newShip.getTransform().getPositionInShip().y() - 0.5),
            (int)Math.round(newShip.getTransform().getPositionInShip().z() - 0.5)
        );

        // Extra height added to the hinge to keep the top ship slightly above the bottom ship
        double extraHeight = 2.0;

        // The rotation we apply to different face values. The code below is set up to create Y-hinges by
        // default, and [rotationQuaternion] converts them to other types of hinges
        Quaterniondc rotationQuaternion = new Quaterniond();
        /*switch (state.getValue(FACING)) {
            case DOWN -> rotationQuaternion = new Quaterniond(new AxisAngle4d(Math.PI, new Vector3d(1.0, 0.0, 0.0)));
            case NORTH -> rotationQuaternion = new Quaterniond(
                new AxisAngle4d(Math.PI, new Vector3d(0.0, 1.0, 0.0))
            ).mul(new Quaterniond(new AxisAngle4d(Math.PI / 2.0, new Vector3d(1.0, 0.0, 0.0)))).normalize();

            case EAST -> rotationQuaternion = new Quaterniond(
                new AxisAngle4d(0.5 * Math.PI, new Vector3d(0.0, 1.0, 0.0))
            ).mul(new Quaterniond(new AxisAngle4d(Math.PI / 2.0, new Vector3d(1.0, 0.0, 0.0)))).normalize();

            case SOUTH -> rotationQuaternion = new Quaterniond(new AxisAngle4d(Math.PI / 2.0, new Vector3d(1.0, 0.0, 0.0))).normalize();

            case WEST -> rotationQuaternion = new Quaterniond(
                new AxisAngle4d(1.5 * Math.PI, new Vector3d(0.0, 1.0, 0.0))
            ).mul(new Quaterniond(new AxisAngle4d(Math.PI / 2.0, new Vector3d(1.0, 0.0, 0.0)))).normalize();

            case UP -> rotationQuaternion = new Quaterniond();

            default -> rotationQuaternion = new Quaterniond();
        }*/
        /*when (state.getValue(FACING)) {
            DOWN -> {
                rotationQuaternion = Quaterniond(AxisAngle4d(Math.PI, Vector3d(1.0, 0.0, 0.0)))
            }
            NORTH -> {
                rotationQuaternion = Quaterniond(AxisAngle4d(Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            }
            EAST -> {
                rotationQuaternion = Quaterniond(AxisAngle4d(0.5 * Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            }
            SOUTH -> {
                rotationQuaternion = Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0))).normalize()
            }
            WEST -> {
                rotationQuaternion = Quaterniond(AxisAngle4d(1.5 * Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            }
            UP -> {
                // Do nothing
                rotationQuaternion = Quaterniond()
            }
                    else -> {
                // This should be impossible, but have this here just in case
                rotationQuaternion = Quaterniond()
            }
        }*/

        // The positions the hinge attaches relative to the center of mass
        Vector3dc attachmentOffset0 = rotationQuaternion.transform(new Vector3d(0.0, 0.5 + extraHeight, 0.0));
        Vector3dc attachmentOffset1 = rotationQuaternion.transform(new Vector3d(0.0, -0.5, 0.0));

        Vector3dc attachmentLocalPos0 =
            new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).add(attachmentOffset0);
        Vector3dc attachmentLocalPos1 =
            new Vector3d(shipCenterPos.getX() + 0.5, shipCenterPos.getY() + 0.5, shipCenterPos.getZ() + 0.5).add(attachmentOffset1);

        // Move [ship] if we are on a ship
        if (onShip != null) {
            EzDebug.log(onShip.getClass().getName());

            // Put the new ship where the old ship is
            Vector3d newPos = onShip.getTransform().getShipToWorld().transformPosition(attachmentLocalPos0, new Vector3d());
            newPos.sub(onShip.getTransform().getShipToWorldRotation().transform(attachmentOffset1, new Vector3d()));
            ShipTransformImpl newTransform = new ShipTransformImpl(
                newPos,
                newShip.getTransform().getPositionInShip(),
                onShip.getTransform().getShipToWorldRotation(), // Copy source ship rotation
                newShip.getTransform().getShipToWorldScaling()
            );
            // Update the ship transform
            ((ShipDataCommon)newShip).setTransform(newTransform);
        } else {
            Vector3d newPos = new Vector3d(attachmentLocalPos0);
            newPos.sub(attachmentOffset1);
            ShipTransformImpl newTransform = new ShipTransformImpl(
                newPos,
                newShip.getTransform().getPositionInShip(),
                newShip.getTransform().getShipToWorldRotation(),
                newShip.getTransform().getShipToWorldScaling()
            );
            // Update the ship transform
            ((ShipDataCommon)newShip).setTransform(newTransform);
        }

        level.setBlockAndUpdate(shipCenterPos, Blocks.IRON_BLOCK.defaultBlockState());
        //blockEntity.get().otherHingePos = shipCenterPos

        long shipId0 = onShip != null ?
            onShip.getId() :
            VSGameUtilsKt.getShipObjectWorld(sLevel).getDimensionToGroundBodyIdImmutable().get(VSGameUtilsKt.getDimensionId(sLevel));
        long shipID1 = newShip.getId();

        // Attachment constraint
        //run {
            // I don't recommend setting compliance lower than 1e-10 because it tends to cause instability
            // TODO: Investigate why small compliance cause instability
            double attachmentCompliance = 1e-10;
            double attachmentMaxForce = 1e10;
            double attachmentFixedDistance = 0.0;
            VSAttachmentConstraint attachmentConstraint = new VSAttachmentConstraint(
                shipId0, shipID1, attachmentCompliance, attachmentLocalPos0, attachmentLocalPos1,
                attachmentMaxForce, attachmentFixedDistance
            );

            EzDebug.log("localP0:" + attachmentLocalPos0 + ", localP1:" + attachmentLocalPos1);
            //blockEntity.get().constraintId = level.shipObjectWorld.createNewConstraint(attachmentConstraint)
            VSGameUtilsKt.getShipObjectWorld(sLevel).createNewConstraint(attachmentConstraint);
        //}

        // Hinge constraints will attempt to align the X-axes of both bodies, so to align the Y axis we
        // apply this rotation to the X-axis
        var hingeOrientation = rotationQuaternion.mul(
            new Quaterniond(new AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)),
            new Quaterniond()
        ).normalize();

        // Hinge orientation constraint
        //run {
            // I don't recommend setting compliance lower than 1e-10 because it tends to cause instability
            double hingeOrientationCompliance = 1e-10;
            double hingeMaxTorque = 1e10;
            VSHingeOrientationConstraint hingeConstraint = new VSHingeOrientationConstraint(
                shipId0, shipID1, hingeOrientationCompliance, hingeOrientation, hingeOrientation, hingeMaxTorque
            );
            //blockEntity.get().constraintId = level.shipObjectWorld.createNewConstraint(hingeConstraint)
            VSGameUtilsKt.getShipObjectWorld(sLevel).createNewConstraint(hingeConstraint);
        //}


        return InteractionResult.CONSUME;
    }
}
