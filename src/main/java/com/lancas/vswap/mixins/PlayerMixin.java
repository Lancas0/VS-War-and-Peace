package com.lancas.vswap.mixins;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.EventMgr;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.ship.feature.hold.*;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.server2client.HoldShipSyncPacketS2C;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.Hashtable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ICanHoldShip {

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    public @NotNull Level level() { return super.level(); }
    @Override
    public Vec3 position() { return super.position(); }
    @Unique
    public Vector3d dPosition() { return JomlUtil.d(position()); }


    //@Unique private long holdingShipId = -1;
    //@Unique private long carryingShipId = -1;
    @Unique private final Hashtable<String, Long> eachSlotHoldShipId = new Hashtable<>();

    @Override
    public void getHoldingShipId(ShipHoldSlot slot, @Nullable Dest<Long> holdingShipIdDest) {
        Long id = eachSlotHoldShipId.get(slot.slotName());
        if (id != null && id >= 0)
            Dest.setIfExistDest(holdingShipIdDest, id);
    }
    @Override
    public boolean isHoldingShip(ShipHoldSlot slot) {
        Dest<Long> holdingIdDest = new Dest<>();
        getHoldingShipId(slot, holdingIdDest);
        return holdingIdDest.hasValue();
    }
    @Override
    public boolean isShipHolding(long shipId) {
        return eachSlotHoldShipId.containsValue(shipId);
    }
    @Override
    public boolean tryHoldInServer(ShipHoldSlot slot, long shipId, boolean syncClient) {
        if (!(level() instanceof ServerLevel sLevel)) return false;

        ServerPlayer thisPlayer = (ServerPlayer)(Object)this;
        ServerShip shipToHold = ShipUtil.getLoadedServerByID(sLevel, shipId);
        if (shipToHold == null) {
            EzDebug.warn("it's no sense to hold a null ship, do nothing.");
            return false;
        }

        var holdable = shipToHold.getAttachment(HoldableAttachment.class);
        if (holdable == null) {
            EzDebug.log("the ship has no holdable att, so can't be holded");
            return false;  //the ship can not be hold
        }

        //long prevSlotHoldShipId = getHoldingShipId(slot);
        //can hold
        //if (prevSlotHoldShipId >= 0)
        if (isHoldingShip(slot))
            unholdShipInServer(slot, false, null);  //will sync at the end, avoid sending more package

        shipToHold.setTransformProvider(new ServerHoldTransformProvider(slot, holdable, thisPlayer.getUUID()));
        shipToHold.setStatic(true);
        HoldingServerTickAttachment.apply(sLevel, shipToHold, thisPlayer.getUUID());
        eachSlotHoldShipId.put(slot.slotName(), shipId);

        if (syncClient) {
            NetworkHandler.sendToClientPlayer(
                thisPlayer,
                new HoldShipSyncPacketS2C(slot.slotName(), shipId, holdable.holdPivotBpInShip.toBp(), holdable.forwardInShip)
            );
        }

        EventMgr.Server.holdShipEvent.invokeAll(sLevel, thisPlayer, shipId);
        return true;
    }

    @Override
    public void unholdShipInServer(ShipHoldSlot slot, boolean syncClient, @Nullable Dest<Long> prevHoldShipIdDest) {
        if (!(level() instanceof ServerLevel sLevel)) return;

        //not holding ship, directly return
        Dest<Long> prevHoldShipId = new Dest<>();
        getHoldingShipId(slot, prevHoldShipId);
        if (!prevHoldShipId.hasValue()) return;

        ServerPlayer thisPlayer = (ServerPlayer)(Object)this;

        ServerShip holdenShip = ShipUtil.getServerShipByID(sLevel, prevHoldShipId.get());
        if (holdenShip == null) {
            EzDebug.warn("fail to unhold ship because holdingId>0 but the ship is null");
            Dest.setIfExistDest(prevHoldShipIdDest, null);
            eachSlotHoldShipId.remove(slot.slotName());
            return;
        }
        HoldableAttachment holdable = holdenShip.getAttachment(HoldableAttachment.class);
        if (holdable == null) {
            EzDebug.fatal("the holden ship has no holdable att");
            Dest.setIfExistDest(prevHoldShipIdDest, null);
            eachSlotHoldShipId.remove(slot.slotName());
            return;
        }

        holdenShip.setTransformProvider(null);
        holdenShip.setStatic(false);
        HoldingServerTickAttachment.disable(sLevel, holdenShip);
        EzDebug.log("should successfully unhold in server");

        eachSlotHoldShipId.remove(slot.slotName());

        if (syncClient) {
            NetworkHandler.sendToClientPlayer(
                thisPlayer,
                new HoldShipSyncPacketS2C(slot.slotName(), -1, holdable.holdPivotBpInShip.toBp(), holdable.forwardInShip)
            );
        }

        Dest.setIfExistDest(prevHoldShipIdDest, prevHoldShipId.get());
    }
    @Override
    public void syncHoldShipInClient(ShipHoldSlot slot, long newHoldShipId, BlockPos holdBpInShip, Direction forwardInShip) {
        if (!(level() instanceof ClientLevel cLevel)) return;
        //newHoldShipId < 0 for unhold

        Dest<Long> prevHoldShipId = new Dest<>();
        getHoldingShipId(slot, prevHoldShipId);
        ClientShip prevHoldShip = ShipUtil.getClientShipByID(cLevel, prevHoldShipId.get());
        if (prevHoldShip != null) {
            prevHoldShip.setTransformProvider(null);
            EzDebug.log("[syncHoldShipInClient]setPrevHoldShip null client transform");
        }

        ClientShip newHoldShip = ShipUtil.getClientShipByID(cLevel, newHoldShipId);
        if (newHoldShip != null) {
            newHoldShip.setTransformProvider(new ClientHoldTransformProvider(slot, holdBpInShip, forwardInShip));
        }

        if (newHoldShipId >= 0)
            eachSlotHoldShipId.put(slot.slotName(), newHoldShipId);
        else
            eachSlotHoldShipId.remove(slot.slotName());
    }

    /*
    @Override
    public Vector3d getHoldPos(BlockPos holdBpInShip, Direction forwardInShip, Matrix4dc shipToWorld, Vector3dc shipPosInWorld) {
        Player thisPlayer = (Player)(Object)this;

        Vector3d holdingWorldPos = dPosition()
            .add(0.0, thisPlayer.getEyeHeight() - 0.4, 0.0)  //get main hand here. maybe
            .add(JomlUtil.d(thisPlayer.getLookAngle().scale(2)))
            .add(JomlUtil.d(thisPlayer.getUpVector(1).scale(-0.5)));

        Vector3d anchorPosInWorld = JomlUtil.dWorldCenter(shipToWorld, holdBpInShip);

        Vector3d movement = holdingWorldPos.sub(anchorPosInWorld, new Vector3d());
        Vector3d newPosInWorld = shipPosInWorld.add(movement, new Vector3d());
        return newPosInWorld;
    }
    @Override
    public Quaterniond getHoldRotation(Matrix4dc shipToWorld, Direction forwardInShip) {
        Player thisPlayer = (Player)(Object)this;

        double xRotRad = JomlUtil.xRotRad(thisPlayer);
        double yRotRad = JomlUtil.yRotRad(thisPlayer);

        return switch (forwardInShip) {
            case SOUTH -> JomlUtil.rotateYRad(-yRotRad).rotateX(xRotRad) .rotateZ(0);                    //ok
            case NORTH -> JomlUtil.rotateYDeg(180)     .rotateY(-yRotRad).rotateX(-xRotRad).rotateZ(-0); //ok
            case UP ->    JomlUtil.rotateXDeg(90)      .rotateZ(yRotRad) .rotateX(xRotRad) .rotateZ(0);  //ok
            case DOWN ->  JomlUtil.rotateXDeg(-90)     .rotateZ(-yRotRad).rotateX(xRotRad) .rotateZ(0);  //ok
            case EAST ->  JomlUtil.rotateYDeg(-90)     .rotateY(-yRotRad).rotateZ(-xRotRad).rotateX(0);  //ok
            case WEST ->  JomlUtil.rotateYDeg(90)      .rotateY(-yRotRad).rotateZ(xRotRad).rotateX(-0);  //ok
        };
        //return HoldableAttachment.rotateDirectionToward(shipToWorld, forwardInShip, JomlUtil.dLookDir(thisPlayer));
    }




    public long getCarryingShipId() { return carryingShipId; }

    public boolean tryCarryInServer(long shipId, boolean syncClient) {
        if (!(level() instanceof ServerLevel sLevel)) return false;

        ServerPlayer thisPlayer = (ServerPlayer)(Object)this;
        ServerShip shipToCarry = ShipUtil.getLoadedServerByID(sLevel, shipId);
        if (shipToCarry == null) {
            EzDebug.warn("it's no sense to hold a null ship, do nothing.");
            return false;
        }

        var holdable = shipToCarry.getAttachment(HoldableAttachment.class);
        if (holdable == null) {
            EzDebug.log("the ship has no holdable att, so can't be holded");
            return false;  //the ship can not be hold
        }

        //can hold
        //unhold if already hold a ship
        if (carryingShipId >= 0)
            uncarryShipInServer(false);  //will sync at the end, avoid sending more package

        //server carry
        shipToCarry.setTransformProvider(new ServerCarryTransformProvider(holdable, thisPlayer.getUUID()));
        shipToCarry.setStatic(true);
        carryingShipId = shipId;

        if (syncClient) {
            NetworkHandler.sendToClientPlayer(thisPlayer, new CarryShipSyncPacketS2C(shipId, holdable.holdPivotBpInShip.toBp(), holdable.forwardInShip));
        }

        return true;
    }
    public long uncarryShipInServer(boolean syncClient) {
        if (!(level() instanceof ServerLevel sLevel))
            return -1;

        //not holding ship, directly return
        if (carryingShipId < 0)
            return carryingShipId;

        ServerPlayer thisPlayer = (ServerPlayer)(Object)this;
        long prevCarryShipId = carryingShipId;

        ServerShip carryShip = ShipUtil.getServerShipByID(sLevel, carryingShipId);
        HoldableAttachment holdable = carryShip.getAttachment(HoldableAttachment.class);
        if (carryShip == null || holdable == null) {
            EzDebug.fatal("fail to unhold ship because holdingId>0 but the ship is null");
            return -1;
        }

        carryShip.setTransformProvider(null);
        carryShip.setStatic(false);

        carryingShipId = -1;

        if (syncClient) {
            NetworkHandler.sendToClientPlayer(thisPlayer, new HoldShipSyncPacketS2C(-1, holdable.holdPivotBpInShip.toBp(), holdable.forwardInShip));
        }

        return prevCarryShipId;
    }

    public void syncCarryShipInClient(long newHoldShipId, BlockPos holdBpInShip, Direction forwardInShip) {

    }

    @Override
    public Vector3d getCarryPos(BlockPos holdBpInShip, Direction forwardInShip, Matrix4dc shipToWorld, Vector3dc shipPosInWorld) {
        Player thisPlayer = (Player)(Object)this;

        double yRotRad = JomlUtil.yRotRad(thisPlayer);

        Vector3d rotationVec = new Quaterniond().rotateY(yRotRad).transform(new Vector3d(0, 0, 1)));

        Vector3d carryWorldPos = dPosition()
            .add(0.0, thisPlayer.getEyeHeight() - 0.4, 0.0)  //get main hand here. maybe
            .add(rotationVec.mul(-2));
            //.add(JomlUtil.d(thisPlayer.getUpVector(1).scale(-0.5)));


        Vector3d anchorPosInWorld = JomlUtil.dWorldCenter(shipToWorld, holdBpInShip);

        Vector3d movement = carryWorldPos.sub(anchorPosInWorld, new Vector3d());
        Vector3d newPosInWorld = shipPosInWorld.add(movement, new Vector3d());
        return newPosInWorld;
    }
    @Override
    public Quaterniond getCarryRotation(Direction forwardInShip) {
        return HoldableAttachment.rotateDirectionToUp(forwardInShip);
    }*/

    /*@ModifyVariable(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), ordinal = 0)
    private Vec3 moveByShip(Vec3 v) {
        //EzDebug.Log("move by ship is called");
        Player player = (Player) (Object) this;
        if (!(player instanceof ServerPlayer)) return new Vec3(0, 0, 0);

        ItemStack boot = player.getItemBySlot(EquipmentSlot.FEET);
        if (boot != null && (boot.getItem() instanceof EinBoot)) {
            ServerShip playerShip = PlayerShipMgr.getOrCreateShip((ServerLevel)player.level(), player.getUUID());
            //ServerShip playerShip = PlayerShipMgr.getOrCreateShip((ServerLevel)player.level(), player.getUUID());
            if (playerShip == null) return v;

            playerShip.getAttachment(TestForceInductor.class).playerFollowIt = true;
            //EzDebug.Log("return " + JomlUtil.mc(playerShip.getTransform().getPositionInWorld()).subtract(player.position()).toString());
            Vector3d playerMovement =
                playerShip.getTransform().getPositionInWorld().sub(JomlUtil.d(player.position()), new Vector3d());

            return JomlUtil.mc(playerShip.getTransform().getPositionInWorld()).subtract(player.position());
        }

        return v;
    }*/
    /*@Inject(
        method = "travel", // 目标方法：处理玩家移动
        at = @At("HEAD"),  // 在方法开始处注入
        cancellable = true // 允许取消原方法执行
    )
    public void onTravel(Vec3 travelVec, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        ItemStack boot = player.getItemBySlot(EquipmentSlot.FEET);
        if (boot != null && (boot.getItem() instanceof EinBoot)) {
            //ci.cancel();
            return;
        }

        if (!(player instanceof ServerPlayer)) return;

        ServerShip playerShip = PlayerShipMgr.getOrCreateShip((ServerLevel)player.level(), player.getUUID());
        if (playerShip == null) return;
    }*/
    /*@Inject(
        method = "travel", // 目标方法：处理玩家移动
        at = @At("HEAD"),  // 在方法开始处注入
        cancellable = true // 允许取消原方法执行
    )
    public void onTravel(Vec3 travelVec, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        ItemStack boot = player.getItemBySlot(EquipmentSlot.FEET);
        if (boot != null && (boot.getItem() instanceof EinBoot)) {

            if (player instanceof ServerPlayer sPlayer) {
                ServerShip playerShip = PlayerShipMgr.getOrCreateShip((ServerLevel)sPlayer.level(), sPlayer.getUUID());
                playerShip.getAttachment(TestForceInductor.class).playerFollowIt = true;
            }

            //NetworkHandler.channel.sendTo(, );

            ci.cancel();
            return;
        }
        /.*if ((player instanceof ServerPlayer)) {
            ci.cancel();
            return;
        }*./



        //ServerShip playerShip = PlayerShipMgr.getOrCreateShip((ServerLevel)player.level(), player.getUUID());
        //if (playerShip == null) return;
    }*/

}