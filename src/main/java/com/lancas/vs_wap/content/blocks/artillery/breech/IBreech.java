package com.lancas.vs_wap.content.blocks.artillery.breech;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.blocks.cartridge.PrimerBlock;
import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public interface IBreech {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BreechRecord implements IBlockRecord {
        private int maxColdDown = 0;
        private int coldDown = 0;

        private BreechRecord() { }
        public BreechRecord(int inMaxColdDown) { maxColdDown = inMaxColdDown; }

        public boolean isCold() { return coldDown <= 0; }
        public void startColdDown() { coldDown = maxColdDown; }

        @Override
        public boolean shouldTick() { return true; }
        @Override
        public void onTick(BlockPos bp) {
            if (coldDown > 0) {
                coldDown--;
            }
        }
    }


    public boolean getLoadedMunitionData(Level level, BlockPos breechBp, Dest<Ship> munitionShip, Dest<Boolean> isTriggered, Dest<Direction> munitionDirInShip);
    public boolean isDockerLoadable(Level level, BlockPos breechBp, ItemStack stack);
    public void loadMunition(Level level, BlockPos breechBp, BlockState state, Dest<Vector3d> placePos, Dest<Vector3d> placeDir);

    //public void ejectShell(Level level, BlockPos breechBp);
    //public Set<BlockPos> findBarrelWithBreechPoses(Level level, BlockPos breechPos, BlockState breechState);

    //todo make sure eject shell is success
    /*public static void ejectShell(ServerLevel level, ServerShip munitionShip, Direction munitionDirInShip, Vector3dc breechWorldPos) {
        //eject the shell
        //Direction primerBackward = primerDir.getOpposite();
        //Vector3d worldPrimerBackward = primerShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(primerBackward));

        ShipBuilder.modify(level, munitionShip)
            .moveFaceTo(munitionDirInShip, breechWorldPos)
            .setLocalVelocity(JomlUtil.dNormal(munitionDirInShip, -20));
    }*/
    public void unloadShell(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp);


    public static InteractableBlockAdder breechInteraction() {
        return new InteractableBlockAdder((level, player, breechBp, breechState) -> {
            //todo sometime(in face always) repeat invoke
            if (!(level instanceof ServerLevel sLevel)) return;

            ICanHoldShip icanHoldShip = (ICanHoldShip)player;
            Dest<Long> holdingShipId = new Dest<>();
            icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand, holdingShipId);

            ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId.get());
            if (holdingShip == null) return;  //not holding ship
            var munitionHoldable = holdingShip.getAttachment(HoldableAttachment.class);
            if (munitionHoldable == null) return;  //no holdable

            AtomicBoolean shouldLoad = new AtomicBoolean(false);
            AtomicReference<BlockPos> primerBp = new AtomicReference<>(null);
            //todo not foreach ship
            ShipBuilder.modify(sLevel, holdingShip).foreachBlock((posInShip, stateInShip, blockEntity) -> {
                if (shouldLoad.get()) return;

                if (stateInShip.getBlock() instanceof PrimerBlock primer) {  //todo not support other primer now
                    if (!primer.isTriggered(stateInShip)) {
                        shouldLoad.set(true);
                        primerBp.set(posInShip);
                    }
                }
            });
            if (!shouldLoad.get()) return;

            @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, breechBp);

            Direction breechDir = DirectionAdder.getDirection(breechState);  //todo use IBreech to get breech dir?
            if (breechDir == null) {
                EzDebug.fatal("can not get direction of breech");
                return;
            }

            PrimerBlock.createConstraints(sLevel, primerBp.get(), artilleryShip, holdingShip, breechBp, breechDir, munitionHoldable);
            icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
        });
    }
}
