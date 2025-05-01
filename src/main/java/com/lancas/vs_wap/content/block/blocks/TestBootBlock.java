package com.lancas.vs_wap.content.block.blocks;

import com.lancas.vs_wap.obsolete.PlayerShipMgr;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.valkyrienskies.core.api.ships.ServerShip;

public class TestBootBlock extends Block {
    public TestBootBlock(Properties p_49795_) { super(p_49795_); }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_60508_) {
        //return super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
        if (level.isClientSide) return InteractionResult.PASS;

        ServerShip playerShapeShip = PlayerShipMgr.getOrCreateShip((ServerLevel)level, player.getUUID());
        ServerLevel sLevel = (ServerLevel)level;

        /*ServerShip newShip = new ShipBuilder(
            pos.above(),
            sLevel,
            1.0
        ).addBlock(new BlockPos(0, 0, 0), Blocks.IRON_BLOCK.defaultBlockState())
            .attach(DebugPosAtt.class, new DebugPosAtt())
            .setNoCollisionWithPlayer()
            .get();*/

        // Extra height added to the hinge to keep the top ship slightly above the bottom ship
        /*double extraHeight = -1;

        // The rotation we apply to different face values. The code below is set up to create Y-hinges by
        // default, and [rotationQuaternion] converts them to other types of hinges
        Quaterniondc rotationQuaternion = new Quaterniond();

        // The positions the hinge attaches relative to the center of mass
        Vector3dc attachmentOffset0 = rotationQuaternion.transform(new Vector3d(0.0, extraHeight, 0.0));
        Vector3dc attachmentOffset1 = rotationQuaternion.transform(new Vector3d(0.0, 0, 0.0));

        Vector3dc attachmentLocalPos0 =
            playerShapeShip.getTransform().getPositionInShip().add(attachmentOffset0, new Vector3d());
        Vector3dc attachmentLocalPos1 =
            newShip.getTransform().getPositionInShip().add(attachmentOffset1, new Vector3d());

        // Move [ship] if we are on a ship
        if (playerShapeShip != null) {
            //EzDebug.Log(onShip.getClass().getName());

            // Put the new ship where the old ship is
            Vector3d newPos = playerShapeShip.getTransform().getShipToWorld().transformPosition(attachmentLocalPos0, new Vector3d());
            newPos.sub(playerShapeShip.getTransform().getShipToWorldRotation().transform(attachmentOffset1, new Vector3d()));
            ShipTransformImpl newTransform = new ShipTransformImpl(
                newPos,
                newShip.getTransform().getPositionInShip(),
                playerShapeShip.getTransform().getShipToWorldRotation(), // Copy source ship rotation
                newShip.getTransform().getShipToWorldScaling()
            );
            // Update the ship transform
            ((ShipDataCommon)newShip).setTransform(newTransform);
        }*//* else {
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
        //}*/

        //level.setBlockAndUpdate(shipCenterPos, Blocks.IRON_BLOCK.defaultBlockState());
        //blockEntity.get().otherHingePos = shipCenterPos

        /*long shipId0 = playerShapeShip.getId();
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

        EzDebug.Log("localP0:" + attachmentLocalPos0 + ", localP1:" + attachmentLocalPos1);
        //blockEntity.get().constraintId = level.shipObjectWorld.createNewConstraint(attachmentConstraint)
        VSGameUtilsKt.getShipObjectWorld(sLevel).createNewConstraint(attachmentConstraint);
        //}*/

        // Hinge constraints will attempt to align the X-axes of both bodies, so to align the Y axis we
        // apply this rotation to the X-axis
        /*var hingeOrientation = rotationQuaternion.mul(
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
        VSGameUtilsKt.getShipObjectWorld(sLevel).createNewConstraint(hingeConstraint);*/
        //}


        return InteractionResult.CONSUME;
    }
}
