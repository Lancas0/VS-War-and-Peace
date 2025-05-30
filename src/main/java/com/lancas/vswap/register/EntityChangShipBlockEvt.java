package com.lancas.vswap.register;

import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blocks.industry.projector.ProjectionCenter;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.attachment.ProjectingShipAtt;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.ServerShip;

@Mod.EventBusSubscriber
public class EntityChangShipBlockEvt {


    //@SubscribeEvent
    //public static void onPlaceOnProjectingShip(BlockEvent.EntityPlaceEvent event) {
        /*if (!(event.getLevel() instanceof ServerLevel level)) return;
        ServerShip ship = ShipUtil.getServerShipAt(level, event.getPos());
        ProjectingShipAtt att = ProjectingShipAtt.getFrom(ship);
        if (att == null) return;*/

        /*EzDebug.log("place aginst:" + StrUtil.getBlockName(event.getPlacedAgainst()));
        if (event.getPlacedAgainst().getBlock() instanceof ProjectCenter) {
            level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            event.setCanceled(true);
            return;
        }*/

        //event.setCanceled(true);
        //BlockState placedBlock = event.getPlacedBlock();
        //att.onUpdateBlock(level, event.getPos());


        //EzDebug.log("placed block:" + StrUtil.getBlockName(event.getPlacedBlock()) + ", level block:" + StrUtil.getBlockName(event.getLevel().getBlockState(event.getPos())));
        //set cancel然后放方块，这样模拟放置方块但不消耗物品 todo或许不设置玩家放置/摧毁事件也挺好？

        //level.setBlockAndUpdate(event.getPos(), event.getPlacedBlock());

        /*if (event.getEntity() instanceof Player player/.* && !player.isCreative()*./) {
            ItemStack handStack = player.getItemInHand(player.getUsedItemHand());

            EzDebug.log("handStack:" + handStack.getItem().getName(handStack) + ", count:" + handStack.getCount());

            if (!handStack.isEmpty()) {
                EzDebug.log("before grow:" + player.getItemInHand(player.getUsedItemHand()).getCount());
                handStack.grow(1);
                player.setItemInHand(player.getUsedItemHand(), handStack);
                EzDebug.log("before grow:" + player.getItemInHand(player.getUsedItemHand()).getCount());
            }
        }*/
    //}
    /*@SubscribeEvent
    public static void onBlockPlace(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();

        //todo设置玩家无法交互投影船上的东西，或者使用SandBox
        //event.setCanceled(true);
        //event.setCancellationResult(InteractionResult.SUCCESS);

        /*BlockState placedState = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
        level.setBlock(pos.relative(event.getFace()), placedState, Block.UPDATE_ALL);

        if () {
            // 播放放置音效
            //todo level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }*./
    }*/


    /*@SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ServerShip ship = ShipUtil.getServerShipAt(level, event.getPos());

        ProjectingShipAtt att = ProjectingShipAtt.getFrom(ship);
        if (att == null) return;

        att.onUpdateBlock(level, event.getPos());
    }*/



    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ServerShip ship = ShipUtil.getServerShipAt(level, event.getPos());
        //EzDebug.log("event state:" + StrUtil.getBlockName(event.getState()) + ", isAir:" + event.getState().isAir());
        ProjectingShipAtt att = ProjectingShipAtt.getFrom(ship);
        if (att == null) {
            //EzDebug.warn("att is null");
            return;
        }

        //EzDebug.log("before set:" + StrUtil.getBlockName(level.getBlockState(event.getPos())));
        //event.getState().getBlock().playerDestroy(level, event.getPlayer(), event.getPos(), false);  //就先不设置玩家放置了，这样不会掉东西，以后找更好的办法todo或许不设置玩家放置/摧毁事件也挺好？

        //EzDebug.log("after set:" + StrUtil.getBlockName(level.getBlockState(event.getPos())));

        //it's impossible to remove project center in a projecting ship
        // EzDebug.log("it's project center?: " + (event.getState().getBlock() instanceof ProjectCenter));
        if (event.getState().getBlock() instanceof ProjectionCenter) {
            //level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            event.setCanceled(true);
            EzDebug.highlight("cancel the event");
            return;
        }

        ShipBuilder builder = ShipBuilder.modify(level, ship);
        int prevBreakCnt = builder.getBlockCnt();

        if (prevBreakCnt == 1) {
            event.setCanceled(true);
            level.setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());  //must cancel and removeAndUpdate or the ship will disappear
            level.setBlockAndUpdate(builder.getInitialBP(), WapBlocks.Industrial.Projector.PROJECTION_CENTER.getDefaultState());  //todo place at really center
        }

        //EzDebug.highlight("update projecting ship");
        //level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        //att.onUpdateBlock(level, event.getPos());

        //level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);


        //event.setCanceled(true); // 取消原版掉落
        //event.setExpToDrop(0);
    }

}
