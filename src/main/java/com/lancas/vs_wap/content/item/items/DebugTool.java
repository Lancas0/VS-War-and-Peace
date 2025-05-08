package com.lancas.vs_wap.content.item.items;

import com.lancas.vs_wap.content.item.items.base.ShipInteractableItem;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.lang.reflect.Field;
import java.util.Arrays;

public class DebugTool extends ShipInteractableItem {
    public DebugTool(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        /*for (int i = 0; i < 10; ++i) {
            new ShipBuilder(player.getOnPos().offset(0, 10, 10), level, 1.0, false).addBlock(new BlockPos(0, 0, 0), Blocks.IRON_BLOCK.defaultBlockState());
        }*/
        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!(ship instanceof ServerShip sShip)) return InteractionResult.PASS;

        Matrix4dc ship2World = sShip.getTransform().getShipToWorld();
        Vector3dc shipPos = sShip.getTransform().getPositionInShip();

        EzDebug.log(
            "shipToWorld:\n" + StrUtil.toNormalString(ship2World) +
                "\nworldPos:" + StrUtil.toNormalString(sShip.getTransform().getPositionInWorld()) +
                "\nshipPos:" + StrUtil.toNormalString(shipPos) +
                "\ntransformed shipPos:" + StrUtil.toNormalString(ship2World.transformPosition(sShip.getTransform().getPositionInShip(), new Vector3d())) +
                "\nx:" + ship2World.m00() + "*" + shipPos.x() + "+" + ship2World.m10() + "*" + shipPos.y() + ship2World.m20() + "*" + shipPos.z() + "+" + ship2World.m30() + " = " + (ship2World.m00() * shipPos.x() + ship2World.m10() * shipPos.y() + ship2World.m20() * shipPos.z() + ship2World.m30()) +
                "\nonly translate x:" + shipPos.x() + " + " + ship2World.m30() + " = " + (shipPos.x() + ship2World.m30())
                //"\ny:" + shipPos.y() + " + " + ship2World.m31() + " = " + (shipPos.y() + ship2World.m31()) +
                //"\nz:" + shipPos.z() + " + " + ship2World.m32() + " = " + (shipPos.z() + ship2World.m32())
        );


        /*boolean hasDisabled = RetrievableDisabledCollisionMgr.hasDisabledCollision(sLevel, ship.getId());
        EzDebug.log("has disabled:" + hasDisabled);

        if (hasDisabled) {
            int ret = RetrievableDisabledCollisionMgr.retrieveAllCollisionsOf(sLevel, ship.getId());
            EzDebug.log("retrive:" + ret);
        } else {
            RetrievableDisabledCollisionMgr.disableCollisionBetween(sLevel, ship.getId(), ShipUtil.getGroundId(sLevel));
        }*/
        //EzDebug.log("Interia of ship:" + sShip.getInertiaData().getMomentOfInertiaTensor());



// 使用示例
        //setMixinField(player, "isModifyingTeleport", true);
        //setMixinField(player, "isModifyingSetPos", true);
        //IVSEntityAccessor vsPlayer = (IVSEntityAccessor) player;

        //EzDebug.log(vsPlayer.vs_getIsModifyingSetPos() + ", " + vsPlayer.vs_getIsModifyingTeleport());

        //vsPlayer.vs_setIsModifyingTeleport(true);
        //vsPlayer.vs_setIsModifyingSetPos(true);
        //setAsIfTeleportingAndSettingPos(player, true);

        /*Vector3d blockCenter = JomlUtil.dCenter(ctx.getClickedPos());
        if (JomlUtil.sqDist(player.position(), blockCenter) < 1000) {  //consider as in shipyard: teleport to world
            Vector3d worldPos = ship.getShipToWorld().transformPosition(blockCenter, new Vector3d());
            EzDebug.log("teleport to worldPos:" + StrUtil.F2(worldPos));
            player.teleportTo(worldPos.x, worldPos.y, worldPos.z);
        } else {  //in world: teleport to shipyard
            EzDebug.log("teleport to shipyard:" + StrUtil.poslike(ctx.getClickedPos()));
            player.teleportTo(ctx.getClickedPos().getX(), ctx.getClickedPos().getY() + 1, ctx.getClickedPos().getZ());
        }*/
        //EzDebug.log("locAABB:" + ship.getShipAABB());

        //setAsIfTeleportingAndSettingPos(player, false);

        //vsPlayer.vs_setIsModifyingTeleport(false);
        //vsPlayer.vs_setIsModifyingSetPos(false);


        //setMixinField(player, "isModifyingTeleport", false);
        //setMixinField(player, "isModifyingSetPos", false);

        return InteractionResult.CONSUME;
    }

    public static void setMixinField(Entity entity, String fieldName, boolean value) {
        try {
            // 获取 MixinEntity 类的 Class 对象
            Class<?> mixinClass = Class.forName("org.valkyrienskies.mod.mixin.feature.shipyard_entities.MixinEntity");

            // 获取字段
            Field field = mixinClass.getDeclaredField(fieldName);
            field.setAccessible(true);

            // 修改字段值
            field.setBoolean(entity, value);
        } catch (Exception e) {
            //.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static void setAsIfTeleportingAndSettingPos(Entity entity, boolean value) {
        try {
            EzDebug.logs(Arrays.stream(entity.getClass().getDeclaredFields()).toList(), f -> f.getName());

            // 获取 Entity 类的 "isModifyingTeleport" 字段
            Field field = Entity.class.getDeclaredField("isModifyingTeleport");

            field.setAccessible(true);
            field.setBoolean(entity, value);

            field = Entity.class.getDeclaredField("isModifyingSetPos");
            field.setAccessible(true);
            field.setBoolean(entity, value);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean lazy(ItemStack stack) {
        /*if (!stack.getOrCreateTag().contains("lazy")) {
            stack.getOrCreateTag().putInt("lazy", 5);
            return false;
        }

        int lazy = stack.getTag().getInt("lazy");
        if (lazy == 0) {
            stack.getTag().putInt("lazy", 5);
            return true;
        } else {
            stack.getTag().putInt("lazy", lazy - 1);
            return false;
        }*/
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean selecting) {
        /*if (true) return;

        if (!selecting) return;
        if (!level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        if (!lazy(stack)) return;

        //in client raycast debug
        Vec3 viewV = player.getViewVector(1f).scale(10f);
        Vec3 eyePos = player.getEyePosition();

        BlockHitResult hit = level.clip(new ClipContext(
            eyePos,
            eyePos.add(viewV),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            null
        ));

        if (hit.getType() == HitResult.Type.BLOCK) {
            EzDebug.log("hit block:" + level.getBlockState(hit.getBlockPos()).getBlock().getName().getString() + ", dir:" + hit.getDirection());
        }*/
    }
}
