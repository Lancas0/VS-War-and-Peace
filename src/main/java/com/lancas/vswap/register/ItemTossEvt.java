package com.lancas.vswap.register;

/*
import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.content.item.entity.DockerItemEntity;
import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ItemTossEvt {
    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        EzDebug.log("on item tossed");

        if (event.getEntity().getItem().is(WapItems.Docker.SHIP_DATA_DOCKER.get())) {
            // 取消原版实体
            event.getEntity().discard();
            EzDebug.highlight("spwan custom docker entity");

            Vec3 dv = event.getEntity().getDeltaMovement();

            // 生成自定义实体
            DockerItemEntity newEntity = new DockerItemEntity(
                event.getPlayer().level(),
                event.getPlayer().getX(), event.getPlayer().getY() + event.getPlayer().getEyeHeight(), event.getPlayer().getZ(),
                event.getEntity().getItem(),
                dv.x, dv.y, dv.z
            );
            // 设置初始参数
            //newEntity.setFixedYRot(event.getPlayer().getYRot());
            //newEntity.setRotationSpeed(45f); // 45度/秒旋转
            event.getPlayer().level().addFreshEntity(newEntity);
        }
    }
}

*/