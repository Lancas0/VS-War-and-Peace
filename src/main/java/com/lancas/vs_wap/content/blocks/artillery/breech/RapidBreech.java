package com.lancas.vs_wap.content.blocks.artillery.breech;

/*
import com.lancas.vs_wap.content.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vs_wap.content.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.content.items.docker.DockerItem;
import com.lancas.vs_wap.content.items.docker.IDocker;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.RedstonePoweredAdder;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShapeBuilder;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.List;

public class RapidBreech extends BlockPlus implements IBreech {
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(RapidBreech.class,
            () -> List.of(
                new DirectionAdder(false, true, ShapeBuilder.ofCubicRing(0, 0, 0, 2, 16).get()),
                /*new RedstonePoweredAdder((level, breechBp, state, hasSignal) -> {
                    if (!hasSignal || level.isClientSide) return;


                    ///find primer around and try to trigger it
                    Dest<IPrimer> primerDest = new Dest<>();
                    Dest<BlockPos> primerBpDest = new Dest<>();
                    Dest<Ship> primerShipDest = new Dest<>();

                    if (findPrimerAround(level, breechBp, primerDest, primerBpDest, primerShipDest)) {
                        fire((ServerLevel)level, breechBp, (ServerShip)primerShipDest.get(), primerDest.get(), primerBpDest.get());
                        return;
                    }

                }, true),*./ //no need for redstone power
                //IBreech.breechInteraction(),  //not interactable, or todo: fire immedate interact
                new RefreshBlockRecordAdder(() -> new IBreech.BreechRecord(0))
            )
        );
    }
    protected RapidBreech(Properties p_49795_) {
        super(p_49795_);
    }



    @Override
    public boolean getLoadedMunitionData(Level level, BlockPos breechBp, Dest<Ship> munitionShip, Dest<Boolean> isTriggered, Dest<Direction> munitionDirInShip) {
        return false;  //there don't exist loaded munition for rapid breech
    }
    @Override
    public boolean isDockerLoadable(Level level, BlockPos breechBp, ItemStack stack) {
        return stack.getItem() instanceof DockerItem;  //todo
    }
    /*@Override
    public void loadMunition(Level level, BlockPos breechBp, BlockState breechState, ItemStack stack) { //todo remain itemStack?
        //directly fire
        var shipScheme = DockerItem.getShipSchemeData(stack);
        if (shipScheme == null) {
            EzDebug.warn("the item has no shipSchemeData!");
            return;
        }

        //shipScheme.foreachBlock();
    }*./
    @Override
    public void unloadShell(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp) {
        Ship artilleryShip = ShipUtil.getShipAt(level, breechBp);

        ItemStack shellStack = DockerItem.stackOfShip(level, shellShip);
        ShipPool.getOrCreatePool(level).returnShipAndSetEmpty(shellShip, ShipPool.ResetAndSet.farawayAndNoConstraint);

        BlockEntity belowEntity = level.getBlockEntity(breechBp.below());
        if (belowEntity == null && artilleryShip != null)
            belowEntity = level.getBlockEntity(JomlUtil.worldBp(artilleryShip.getShipToWorld(), breechBp.below()));

        Vector3dc worldBelowPos = WorldUtil.getWorldCenter(level, breechBp.below());

        if (belowEntity != null) {
            var cap = belowEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
            cap.ifPresent(handler -> {
                ItemStack remainStack = ItemHandlerHelper.insertItem(handler, shellStack.copy(), true);
                if (remainStack.isEmpty()) {  //actually insert
                    ItemHandlerHelper.insertItem(handler, shellStack, false);
                    return;
                }
                //should spawn item entity
                ItemEntity itemE = new ItemEntity(level, worldBelowPos.x(), worldBelowPos.y(), worldBelowPos.z(), shellStack);
                itemE.setDeltaMovement(0, -0.1, 0);
                level.addFreshEntity(itemE);
            });

            return;
        }

        ItemEntity itemE = new ItemEntity(level, worldBelowPos.x(), worldBelowPos.y(), worldBelowPos.z(), shellStack);
        itemE.setDeltaMovement(0, -0.1, 0);
        level.addFreshEntity(itemE);
    }
}
*/