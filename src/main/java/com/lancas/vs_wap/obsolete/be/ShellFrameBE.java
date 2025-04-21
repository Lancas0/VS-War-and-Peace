package com.lancas.vs_wap.obsolete.be;

/*
import com.lancas.vs_wap.content.saved.ConstraintsMgr;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.EventMgr;
import com.lancas.vs_wap.foundation.LazyTicks;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;

public class ShellFrameBE extends BlockEntity {
    private static final int COLD_DOWN_TICKS = 10;

    public ShellFrameBE(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public String attConstraintKey = "";
    public String oriConstraintKey = "";
    public long lockingShipId = -1;
    private int coldDown = 0;
    private TriConsumer<ServerLevel, ServerPlayer, Long> unholdListener = null;

    //not save
    private final LazyTicks lazy = new LazyTicks(20);

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.attConstraintKey = tag.getString("attachment_id");
        this.oriConstraintKey = tag.getString("orientation_id");
        this.lockingShipId = tag.getLong("related_ship_id");
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putString("attachment_id", attConstraintKey);
        tag.putString("orientation_id", oriConstraintKey);
        tag.putLong("related_ship_id", lockingShipId);
        super.saveAdditional(tag);
    }

    public boolean isCold() {
        return coldDown <= 0;
    }
    public void resetColdDown() {
        coldDown = COLD_DOWN_TICKS;
    }

    public void tickColdDown() {
        if (level == null || level.isClientSide) return;

        if (coldDown > 0) {
            coldDown--;
        }
    }




    /.*
    public void tick() {
        if (!(level instanceof ServerLevel sLevel)) return;
        if (relatedShipId < 0) return;
        if (!lazy.shouldWork())
            return;

        //this be functional only in server
        var toRemoveIt = toRemoveConstraintWith.iterator();
        while (toRemoveIt.hasNext()) {
            long curToRemove = toRemoveIt.next();

            if (curToRemove == relatedShipId) {
                toRemoveIt.remove();

                if (attConstraintKey == null || attConstraintKey.isEmpty() || oriConstraintKey == null || oriConstraintKey.isEmpty()) {
                    EzDebug.fatal("constraint key shouldn't be empty when has relatedShipId");
                    break;
                }

                ConstraintsMgr.removeConstraint(sLevel, attConstraintKey);
                ConstraintsMgr.removeConstraint(sLevel, oriConstraintKey);
            }
        }
    }*./

}*/