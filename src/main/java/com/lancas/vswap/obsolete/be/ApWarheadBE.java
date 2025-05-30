package com.lancas.vswap.obsolete.be;

/*
import com.lancas.einherjar.ship.phys.ballistics.ApBallistics;
import com.lancas.einherjar.util.JomlUtil;
import com.lancas.einherjar.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;

public class ApWarheadBE extends BlockEntity {
    private ApBallistics ballistics;

    public ApWarheadBE(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    private ServerShip projectileShip;
    private ServerShip propellantShip;
    public void setShips(ServerShip inProjectileShip, ServerShip inPropellantShip) {  //todo it do not effect sometimes
        projectileShip = inProjectileShip;
        propellantShip = inPropellantShip;
    }

    /.*protected Vector3d getWorldPos() {  //suppose ship is not null
        return ship.getShipToWorld().transformPosition(JomlUtil.dCenter(worldPosition));
    }*./

    protected ApBallistics getBallistics() {  //level
        if (ballistics != null) return ballistics;
        if (level == null || level.isClientSide) return null;

        /.*if (ship == null) {
            ship = ShipUtil.getShipAt((ServerLevel)level, worldPosition);  //todo do not check every tick
            if (ship == null) return null;
        }*./
        if (projectileShip == null || propellantShip == null)
            return null;

        return new ApBallistics(projectileShip, propellantShip, worldPosition, getBlockState().getShape(level, worldPosition));
    }


    public void tick() {
        if (level == null || level.isClientSide) return;
        if (ballistics == null) ballistics = getBallistics();
        if (ballistics == null) return;

        ballistics.tick((ServerLevel)level);



        /*Vector3d curVel = ship.getVelocity().get(new Vector3d());
        Vector3d impulse = new Vector3d();
        boolean anyDestroy = false;
        for (BlockPos pos : predictCollideBPs()) {
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                EzDebug.Log("set " + pos + " to air");

                double mass = ship.getInertiaData().getMass();
                //impulse.add(curVel.mul(-0.1 * mass));
                //EzDebug.Log("mass:" + mass + ", vel:" + curVel);
                anyDestroy = true;
            }
        }

        if (anyDestroy) {
            //todo sometimes crash: after use command /vs delete @v
            //java.lang.NullPointerException: Ticking block entity
            //        at org.valkyrienskies.core.impl.shadow.DM.teleportShip(Unknown Source) ~[valkyrienskies-120-forge-2.3.0-beta.5+6911bbe238_mapped_official_1.20.1.jar%23200!/:?] {re:classloading}
            //        at com.lancas.einheriar.content.blockentity.ArmorPierceWarheadBE.tick(ArmorPierceWarheadBE.java:96) ~[%23196!/:?] {re:classloading}
            //        at com.lancas.einheriar.content.blocks.warhead.ArmorPierceWarheadBlock.lambda$getTicker$0(ArmorPierceWarheadBlock.java:33) ~[%23196!/:?] {re:classloading}
            VSGameUtilsKt.getShipObjectWorld((ServerLevel)level).teleportShip(ship, new ShipTeleportDataImpl(
                ship.getTransform().getPositionInWorld(),
                ship.getTransform().getShipToWorldRotation(),
                curVel.mul(0.1),
                new Vector3d(),
                VSGameUtilsKt.getDimensionId(level),
                ship.getTransform().getShipToWorldScaling().x() //todo 3d scale
            ));
        }
            //ImpulseInducer.apply(ship, impulse);*/








        /*AABBd predictCollisionAABB = new AABBd();
        Vector3d worldPos = getWorldPos();



        //EzDebug.Log("be tick");`
        AABBd shipAABB = new AABBd(ship.getWorldAABB());
        shipAABB.union()

        Vector3dc velocity = ship.getVelocity();
        Vector3d velDir = velocity.normalize(new Vector3d());

        Vector3d checkVec =  velocity.mul(TICK_INTERVAL, new Vector3d()).add(velDir);  //check a local block size forward


        Vector3d aimingPos = worldPos.add(velDir);
        BlockPos aimingBP = BlockPos.containing(aimingPos.x, aimingPos.y, aimingPos.z);
        BlockState aimingState = level.getBlockState(aimingBP);

        EzDebug.Log("checking:" + aimingBP + ", state:" + aimingState);
        if (!aimingState.isAir())
            level.setBlock(aimingBP, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        //todo some method optimazed

        /.*double step = 0.3;
        Vector3d velStep = velDir.mul(step, new Vector3d());
        int checkTimes = (int)Math.ceil(checkVec.length() / step);
        EzDebug.Log("checkVec" + checkVec + ", checkTimes:" + checkTimes);
        Vector3d curCheckPos = worldPos.get(new Vector3d());
        for (int i = 0; i < checkTimes; ++i) {
            BlockPos curBP = BlockPos.containing(curCheckPos.x, curCheckPos.y, curCheckPos.z);
            BlockState curState = level.getBlockState(curBP);

            EzDebug.Log("checking:" + curBP + ", state:" + curState);

            if (!curState.isAir()) {  //todo apply reistance force by hardness
                //todo also destory block on another ship
                level.setBlock(curBP, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                //EzDebug.Log("destroy at " + curBP);
            }
            curCheckPos.add(velStep);
        }*./
    }

    private void penetration() {

    }
    private void ricochet() {

    }

}*/
