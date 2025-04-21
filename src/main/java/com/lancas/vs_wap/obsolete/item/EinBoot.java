package com.lancas.vs_wap.obsolete.item;

import com.lancas.vs_wap.obsolete.ship.MountToPlayerTypes;
import com.lancas.vs_wap.ship.attachment.PlayerHoldingAttachment;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.obsolete.PlayerShipMgr;
import com.lancas.vs_wap.util.ConstraintUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static net.minecraft.world.InteractionResultHolder.consume;

public class EinBoot extends ArmorItem {

    public static String SHIP_ID_TAG = "ship_id";
    public static void setShipID(ItemStack stack, long id) { stack.getOrCreateTag().putLong(SHIP_ID_TAG, id); }
    public static long getShipID(ItemStack stack) { return stack.getOrCreateTag().getLong(SHIP_ID_TAG); }
    public static void clearShipID(ItemStack stack) { stack.getOrCreateTag().remove(SHIP_ID_TAG); }
    public static boolean hasShipID(ItemStack stack) {
        if (!stack.hasTag())
            return false;
        return stack.getTag().contains(SHIP_ID_TAG);
    }
    public static ServerShip getShip(ServerLevel level, ItemStack stack) {
        if (!hasShipID(stack)) return null;
        return VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(getShipID(stack));
    }


    // 自定义盔甲材质（需在后续注册）
    public static final ArmorMaterial EinBoot_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(Type type) { return 100; }
        @Override public int getDefenseForType(Type type) { return 5; }
        //@Override public int getDurabilityForSlot(EquipmentSlot slot) { return 500; }
        //@Override public int getDefenseForSlot(EquipmentSlot slot) { return 3; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_IRON; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.IRON_INGOT); }
        @Override public String getName() { return "ein_boot"; }
        @Override public float getToughness() { return 2.0F; }
        @Override public float getKnockbackResistance() { return 0.1F; }
    };

    public EinBoot() {
        //super(p_40386_, p_266831_, p_40388_);
        super(EinBoot_MATERIAL, Type.BOOTS, new Properties().stacksTo(1).durability(500));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        if (ctx.getLevel().isClientSide)
            return InteractionResult.PASS;

        ServerLevel level = (ServerLevel)ctx.getLevel();
        ServerPlayer player = (ServerPlayer)ctx.getPlayer();
        BlockPos interactPos = ctx.getClickedPos();


        if (level.getBlockState(interactPos).isAir()) return InteractionResult.CONSUME;

        ServerShip clickedShip = VSGameUtilsKt.getShipManagingPos(level, interactPos);
        if (clickedShip == null) return InteractionResult.CONSUME;

        releaseGrabed(level, stack);
        grabShip(player, stack, clickedShip);
        EzDebug.log("grab ship:" + clickedShip.getSlug());

        return InteractionResult.CONSUME;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return consume(stack);
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int ix, boolean selecting) {
        //todo do not called in every tick

        if (level.isClientSide) return;
        if (!(entity instanceof Player)) return;

        ServerShip ship = getShip((ServerLevel)level, stack);
        if (ship == null) return;

        /*PlayerHoldingAttachment holdingAtt = ship.getAttachment(PlayerHoldingAttachment.class);
        if (holdingAtt == null) return;

        holdingAtt.holding = ItemUtil.isInArmorSlots(ix);
        EzDebug.Log("set holding:" + holdingAtt.holding);*/
        /*super.inventoryTick(stack, level, entity, ix, selecting);
        if (level.isClientSide) return;
        if (!(entity instanceof Player)) return;
        if (!hasShipID(stack)) return;

        Boolean lastSelecting = stackLastSelectingDic.get(stack);
        if (lastSelecting == null)
            EzDebug.Log("new stack recorded");

        if (lastSelecting != null && lastSelecting.equals(selecting))
            return;

        Player player = (Player) entity;

        long shipID = getShipID(stack);
        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(shipID);

        PlayerHoldingAttachment playerHoldingAtt = ship.getAttachment(PlayerHoldingAttachment.class);
        if (playerHoldingAtt == null) return;

        if (!playerHoldingAtt.inited) {
            ship.setTransformProvider(new MountPlayerHandTP(
                playerHoldingAtt
            ));
            playerHoldingAtt.inited = true;
            playerHoldingAtt.playerUUID = player.getUUID();
        }

        playerHoldingAtt.holding = selecting;
        stackLastSelectingDic.put(stack, selecting);*/
    }


    private void releaseGrabed(ServerLevel level, ItemStack stack) {  //todo release on ground
        /*if (!hasShipID(stack)) return;

        long shipID = getShipID(stack);
        ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(shipID);
        if (ship == null) return;

        //todo reset so that it is possible to collide with player
        clearShipID(stack);
        ship.saveAttachment(PlayerHoldingAttachment.class, null);
        ship.setTransformProvider(null);*/
    }
    private void grabShip(ServerPlayer player, ItemStack stack, ServerShip ship) {
        //PlayerCollisionAttachment.getOrAdd(ship);
        //ship.setSlug(ship.getSlug() + "_player_no_collision");
        setShipID(stack, ship.getId());
        ServerLevel level = (ServerLevel)player.level();
        ServerShip playerShip = PlayerShipMgr.getOrCreateShip(level, player.getUUID());

        if (playerShip == null) return;

        ship.setSlug(ship.getSlug() + "_player_no_collision_");

        ConstraintUtil.Attach(level, playerShip, ship, new Vector3d(0, -2, 0));
        ConstraintUtil.FixedOrientation(level, playerShip, ship);

        PlayerHoldingAttachment holdingAtt = new PlayerHoldingAttachment(player.getUUID(), ship.getId(), MountToPlayerTypes.MountToMainHand);
        holdingAtt.addTo(ship);
        //ship.setTransformProvider(new MountPlayerFootTP(holdingAtt));
    }
}