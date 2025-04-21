package com.lancas.vs_wap.mixins;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.PosUtil;
import com.lancas.vs_wap.util.ShipUtil;
import kotlin.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.collision.ConvexPolygonc;
import org.valkyrienskies.core.apigame.collision.EntityPolygonCollider;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static java.lang.Math.max;

//todo disable entity drag
@Mixin(EntityShipCollisionUtils.class)
public abstract class ShipCollisionMixin {



    private static Method getShipPolygonsMethod;
    private static List<ConvexPolygonc> getShipPolygons(Entity entity, Vec3 vec, AABB entityBoundingBox, Level world) {
        try {
            if (getShipPolygonsMethod == null) {
                getShipPolygonsMethod = EntityShipCollisionUtils.class.getDeclaredMethod(  //todo reflect on init
                        "getShipPolygonsCollidingWithEntity",
                        Entity.class,
                        Vec3.class,
                        AABB.class,
                        Level.class
                );
                getShipPolygonsMethod.setAccessible(true);
            }

            return (List<ConvexPolygonc>)getShipPolygonsMethod.invoke(EntityShipCollisionUtils.INSTANCE, entity, vec, entityBoundingBox, world);
        } catch (Exception e) {
            EzDebug.log(e.toString());
        }
        return null;
    }

    private static EntityPolygonCollider collider;
    private static EntityPolygonCollider getCollider() {
        try {
            if (collider == null) {
                Field colliderField = EntityShipCollisionUtils.class.getDeclaredField("collider");
                colliderField.setAccessible(true);
                collider = (EntityPolygonCollider) colliderField.get(EntityShipCollisionUtils.INSTANCE);
            }
        } catch (Exception e) {
            EzDebug.log(e.toString());
        }

        return collider;
    }



    @Inject(
            method = "adjustEntityMovementForShipCollisions",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    public void noCollisionWithPlayer(Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, CallbackInfoReturnable<Vec3> cir) {
        //EzDebug.Log("try noCollisionWithPlayer");
        //System.out.println("----------------" + (world.isClientSide ? "Client" : "Server") + "------------");

        if (entity == null)
            return;

        if (!(entity instanceof ICanHoldShip icanHoldShip)) return;

        var newMovementAndShip = getNewMovementAndShip(entity, movement, entityBoundingBox, world);
        if (newMovementAndShip == null) {
            cir.setReturnValue(movement);
            return;
        }


        Ship ship =
            newMovementAndShip.component2() != null ?
                ShipUtil.getLoadedShipByID(world, newMovementAndShip.component2()) :
                null;

        if (ship != null) {
            if (icanHoldShip.isShipHolding(ship.getId())) {
                cir.setReturnValue(movement);
                return;
            }
            /*ItemStack stack = player.getMainHandItem();  //todo offhands?
            if (stack != null && stack.getItem() instanceof EinherjarWand) {
                Long holdingShipID = EinherjarWand.getShipID(stack);
                if (holdingShipID != null && holdingShipID.equals(ship.getId())) {
                    //System.out.println("should not collide, movement:" + movement);
                    cir.setReturnValue(movement);
                    return;
                }
            }*/
        }

        if (newMovementAndShip.component2() != null) {
            ((IEntityDraggingInformationProvider)entity).getDraggingInformation().setLastShipStoodOn(newMovementAndShip.component2());
        }

        //System.out.println("set new movement, ship:" + ship);
        cir.setReturnValue(JomlUtil.v3(newMovementAndShip.component1()));
        return;
    }

    private Pair<Vector3dc, Long> getNewMovementAndShip(Entity entity, Vec3 movement, AABB entityBoundingBox, Level world) {
        double inflation = 0.5;
        double stepHeight = entity.getStepHeight();

        var collidingShipPolygons = getShipPolygons(
                entity,
                new Vec3(movement.x(), movement.y() + max(stepHeight - inflation, 0.0), movement.z()),
                entityBoundingBox.inflate(inflation),
                world
        );

        if (collidingShipPolygons == null || collidingShipPolygons.isEmpty()) {
            return null;
        }

        var newMovementAndShip = getCollider().adjustEntityMovementForPolygonCollisions(PosUtil.toV3D(movement), JomlUtil.d(entityBoundingBox), stepHeight, collidingShipPolygons);
        return newMovementAndShip;
    }
}