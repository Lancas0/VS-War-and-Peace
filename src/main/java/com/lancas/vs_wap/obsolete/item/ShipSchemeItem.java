package com.lancas.vs_wap.obsolete.item;
/*
import com.lancas.einherjar.ship.attachment.MountToPlayerTypes;
import com.lancas.einherjar.ship.attachment.PlayerHoldingAttachment;
import com.lancas.einherjar.ship.data.ShipSchemeDataAsTag;
import com.lancas.einherjar.debug.EzDebug;
import com.lancas.einherjar.register.SchemeTranslateAndRotateRegister;
import com.lancas.einherjar.util.ItemUtil;
import com.lancas.einherjar.util.PosUtil;
import com.lancas.einherjar.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.valkyrienskies.core.api.ships.ServerShip;

public class ShipSchemeItem extends Item {
    public static final String SCHEME_DATA_TAG = "ship_scheme_data";
    public static final String FLAWLESS_SCHEME_DATA_TAG = "flawless_scheme_data";

    public ShipSchemeItem(Properties p_41383_) {
        super(p_41383_);
    }

    /.*public static boolean hasSchemeData(ItemStack itemStack) {
        return itemStack.getOrCreateTag().contains(SCHEME_DATA_TAG);
    }*./

    public static void setSchemeData(ItemStack itemStack, ShipSchemeDataAsTag shipSchemeData) {
        itemStack.getOrCreateTag().put(SCHEME_DATA_TAG, shipSchemeData.toTag());
    }
    public static void setFlawlessSchemeData(ItemStack itemStack, ShipSchemeDataAsTag flawlessShipSchemeData) {
        itemStack.getOrCreateTag().put(FLAWLESS_SCHEME_DATA_TAG, flawlessShipSchemeData.toTag());
    }
    public static void setFlawless(ItemStack itemStack) {
        CompoundTag flawlessTag = itemStack.getOrCreateTag().getCompound(FLAWLESS_SCHEME_DATA_TAG);
        itemStack.getOrCreateTag().put(SCHEME_DATA_TAG, flawlessTag);
    }
    public static CompoundTag getSchemeDataTag(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getCompound(SCHEME_DATA_TAG);
    }
    public static CompoundTag getFlawlessSchemeDataTag(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getCompound(FLAWLESS_SCHEME_DATA_TAG);
    }
    public static ShipSchemeDataAsTag getSchemeData(ItemStack itemStack) {
        CompoundTag tag = getSchemeDataTag(itemStack);
        return new ShipSchemeDataAsTag(tag);
    }
    public static ShipSchemeDataAsTag getFlawlessSchemeData(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag().getCompound(FLAWLESS_SCHEME_DATA_TAG);
        return new ShipSchemeDataAsTag(tag);
    }
    public static float getDurability01(ItemStack itemStack) {
        int currentBlockCnt = getSchemeData(itemStack).getBlockCnt();
        int flawlessBlockCnt = getFlawlessSchemeData(itemStack).getBlockCnt();
        return currentBlockCnt / (float)flawlessBlockCnt;
    }
    public static int getDamagedValue(ItemStack itemStack) {
        int currentBlockCnt = getSchemeData(itemStack).getBlockCnt();
        int flawlessBlockCnt = getFlawlessSchemeData(itemStack).getBlockCnt();
        return flawlessBlockCnt - currentBlockCnt;
    }




    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide)
            return super.use(level, player, hand);

        ServerLevel sLevel = (ServerLevel)level;
        ServerPlayer sPlayer = (ServerPlayer)player;
        ItemStack itemStack = sPlayer.getItemInHand(hand);
        ShipSchemeDataAsTag shipSchemeData = getSchemeData(itemStack);//new ShipSchemeDataAsTag(itemNbt.getCompound(SCHEME_DATA_TAG));

        if (shipSchemeData == null) {
            EzDebug.log("Has no scheme data");
            return super.use(level, player, hand);
        }

        //todo 重复代码(in TestRenderer)
        // 计算投影原点
        HitResult hit = player.pick(SchemeTranslateAndRotateRegister.distance, 0.0f, false);
        // 投影原点（世界坐标）
        BlockPos shipOrigin;
        if (hit.getType() == HitResult.Type.BLOCK) {
            shipOrigin = ((BlockHitResult) hit).getBlockPos().relative(((BlockHitResult) hit).getDirection());
        } else {
            shipOrigin = BlockPos.containing(player.getEyePosition().add(player.getViewVector(1.0f).scale(SchemeTranslateAndRotateRegister.distance)));
        }

        ServerShip ship = shipSchemeData.createShip(sLevel, PosUtil.toV3D(shipOrigin), SchemeTranslateAndRotateRegister.rotation);
        if (ship != null) {
            ship.setStatic(true);
            //PlayerCollisionAttachment.getOrAdd(ship);
            ship.setSlug(ship.getSlug() + "_player_no_collision"); //todo use attachment or something to replace this fool

            PlayerHoldingAttachment holdingAtt = new PlayerHoldingAttachment(player.getUUID(), ship.getId(), MountToPlayerTypes.MountToMainHand);
            holdingAtt.addTo(ship);
            //ship.setTransformProvider(new MountPlayerHandTP(holdingAtt));

            //todo decide whether to write tag on ship
            itemStack.shrink(1);  //使用后把物品删了(this method do not shrink in creative mode)

            ShipUtil.writeTag(ship, getFlawlessSchemeDataTag(itemStack));
            //EzDebug.Log("successfully spawn a ship");
        }

        return ship == null ? InteractionResultHolder.fail(itemStack) : InteractionResultHolder.success(itemStack);
    }


    @Override
    public boolean isBarVisible(ItemStack p_150899_) { return true; }
    // 自定义耐久条颜色
    @Override
    public int getBarColor(ItemStack stack) { return ItemUtil.barColorByDurability(getDurability01(stack), 1f); }
    // 耐久条宽度
    @Override
    public int getBarWidth(ItemStack stack) { return ItemUtil.barWidthByDurability(getDurability01(stack), 1f); }

    /.*
    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        //super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        //only do render job in client side
        if (!level.isClientSide) return;

        ShipSchemeDataAsTag shipSchemeData = getSchemeData(stack);
        if (shipSchemeData == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.translate(0, 0, -10); // todo 摄像机位置

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock();
    }*./
}
*/