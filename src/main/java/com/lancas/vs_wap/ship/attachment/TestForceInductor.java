package com.lancas.vs_wap.ship.attachment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.phys.PositionPID;
import com.lancas.vs_wap.register.ServerDataCollector;
import com.lancas.vs_wap.util.JomlUtil;
import kotlin.jvm.functions.Function1;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.UUID;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestForceInductor implements ShipForcesInducer {
    public static final Vector3d ANTI_GRAVITY = new Vector3d(0, 10, 0);

    public UUID playerUUID;
    public boolean playerFollowIt = false;
    private double mass;

    private final double kP = 5; // 位置比例增益
    private final double kD = 0.5; // 阻尼系数

    private final double angularKP = 5;  // 旋转比例增益（单位：1/s²）
    private final double angularKD = 1;  // 旋转阻尼增益（单位：1/s）

    private final double maxForce = 1e20;
    private final double maxTorque = 1e20;  //todo

    private PositionPID ppid = new PositionPID(18, 0, 8);

    public TestForceInductor() {
    }
    public TestForceInductor(UUID inPlayerUUID, double inMass) {
        playerUUID = inPlayerUUID;
        mass = inMass;
    }

    public static TestForceInductor getOrCreate(ServerShip ship, UUID setIfCreate){
        //return ship.getOrPutAttachment(AnchorForceInducer.class, AnchorForceInducer::new);
        var att = ship.getAttachment(TestForceInductor.class);
        if(att == null){
            att = new TestForceInductor(setIfCreate, ship.getInertiaData().getMass());
            ship.saveAttachment(TestForceInductor.class, att);
            EzDebug.log("save new attchment");
        } else {
            EzDebug.log("get force inducer");
        }
        return att;
    }
    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        //todo temp
        if (true) return;

        if (playerFollowIt) return;

        ServerPlayer player = ServerDataCollector.playerList.getPlayer(playerUUID);
        Vector3d force = ppid.calculateForce(mass, physShip.getTransform().getPositionInWorld(), JomlUtil.d(player.position().add(0, 1, 0)));
        try {

            physShip.applyInvariantForce(force);

            //EzDebug.Log("apply force: " + force);
            //physShip.applyInvariantForce(GRAVITY.mul(physShip.getMass(), new Vector3d()));
        } catch (Exception e) {
            //EzDebug.Log("fail to apply force:" + force);
        }

        //EzDebug.Log("inv gravity force:" + GRAVITY.mul(physShip.getMass(), new Vector3d()));
        //physShip.applyInvariantForce(GRAVITY.mul(physShip.getMass(), new Vector3d()));

        //if (!playerFollowIt) {
        //    ServerPlayer player = ServerDataCollector.playerList.getPlayer(playerUUID);
        //    if (player == null) return;



            /*ServerThreadBridge.AddIfNotExistUnique("shipForceInducer" + physShip.getId(), () -> {
                long shipId = physShip.getId();
                ServerLevel level = (ServerLevel)player.level();
                ServerShip serverShip = ShipUtil.getShipByID(level, shipId);
                VSGameUtilsKt.getShipObjectWorld(level).teleportShip(serverShip, new ShipTeleportDataImpl(
                    JomlUtil.d(player.position()),
                    new Quaterniond().rotateY(Math.toRadians(-player.getYRot())),
                    new Vector3d(0, 0, 0),
                    new Vector3d(0, 0, 0),
                    VSGameUtilsKt.getDimensionId(level),
                    serverShip.getTransform().getShipToWorldScaling().x()
                ));
            });
            */
            //applyForceToFollowPlayer(physShip, player);
            //applTorqueFollowPlayer(physShip, player);
        //}
    }

    public void applyForceToFollowPlayer(@NotNull PhysShip physShip, ServerPlayer player) {
        /*
        Vector3d targetPos = JomlUtil.d(player.position()).add(0, 1, 0, new Vector3d());

        //计算位置误差和速度
        Vector3d positionError = targetPos.sub(physShip.getTransform().getPositionInWorld(), new Vector3d());
        Vector3d velocityError = physShip.getVelocity().mul(-1, new Vector3d());

        //施加位置修正力（PD控制）
        // 位置控制（PD控制器）
        Vector3d desiredAcceleration = positionError.mul(kP).add(velocityError.mul(kD));
        double accelerationMag = desiredAcceleration.length() * physShip.getMass();
        accelerationMag = Math.min(accelerationMag, maxForce);
        Vector3d accelerationDir = desiredAcceleration.normalize(new Vector3d());

        Vector3d force = accelerationDir.mul(accelerationMag); // F = m*a
        physShip.applyInvariantForce(force);
        */
    }
    public void applTorqueFollowPlayer(@NotNull PhysShip physShip, ServerPlayer player) {
        /*Quaterniond targetRot = new Quaterniond()
            .rotateY(Math.toRadians(-player.getYRot()));

        Matrix3d inertia = new Matrix3d(physShip.getMomentOfInertia());
        // 旋转控制（四元数误差）
        Quaterniondc currentRot = physShip.getTransform().getShipToWorldRotation();
        Quaterniond errorRot = targetRot.difference(currentRot, new Quaterniond());
        Vector3d angularError = errorRot.getEulerAnglesXYZ(new Vector3d());
        Vector3d angularVelocityError = physShip.getOmega().mul(-1, new Vector3d());

        // 转换到主轴坐标系（简化处理）
        Vector3d principalInertia = new Vector3d(
            inertia.m00(), // I_xx
            inertia.m11(), // I_yy
            inertia.m22()  // I_zz
        );
        Vector3d desiredAngularAcceleration = angularError.mul(angularKP)
            .add(angularVelocityError.mul(angularKD));
        Vector3d torque =  desiredAngularAcceleration.mul(principalInertia); // τ = I*α
        physShip.applyInvariantTorque(torque);

         */
    }

    @Override
    public void applyForcesAndLookupPhysShips(@NotNull PhysShip physShip, @NotNull Function1<? super Long, ? extends PhysShip> lookupPhysShip) {
        //ShipForcesInducer.super.applyForcesAndLookupPhysShips(physShip, lookupPhysShip);
        //EzDebug.Log("apply force and look phy ships " + test);
        /*if (!inited) {
            physShip.applyInvariantForce(GRAVITY.mul(physShip.getMass(), new Vector3d()));
            inited = true;
        }*/
    }
}
