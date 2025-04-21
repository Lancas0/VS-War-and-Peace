package com.lancas.vs_wap.content.blocks.cartridge.warhead;

import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.blockentity.ApWarheadBlockEntity;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.ballistics.api.ICollisionTrigger;
import com.lancas.vs_wap.ship.ballistics.api.ITerminalEffector;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.ship.ballistics.collision.traverse.BlockTraverser;
import com.lancas.vs_wap.ship.ballistics.data.BallisticStateData;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.util.StrUtil;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.*;

public abstract class AbstractApWarhead extends BlockPlus implements ICollisionTrigger, ITerminalEffector, IBE<ApWarheadBlockEntity> {
    public AbstractApWarhead(Properties p_49795_) {
        super(p_49795_);
    }

    public abstract double getRaycastStep();
    public abstract double getRaycastPredictTime();
    //@Override
    //public CollisionDetectMethod getCollisionMethod(BlockState state) { return CollisionDetectMethod.NearestFirstUnlimited(0.2, 0.5); }

    @Override
    public void appendTriggerInfos(
        ServerLevel level, BlockPos pos, BlockState state, BallisticsShipData shipData, BallisticStateData stateData, List<TriggerInfo> dest
    ) {
        ServerShip projectile = shipData.getProjectileShip(level);
        if (projectile == null) {
            EzDebug.error("the projectile ship is null");
            return;
        }
        HashSet<Long> skipShips = new HashSet<>();
        skipShips.add(projectile.getId());
        skipShips.add(shipData.propellantShipId);
        if (shipData.artilleryShipId >= 0)
            skipShips.add(shipData.artilleryShipId);

        LinkedHashMap<BlockPos, BallisticsHitInfo> traverseResults = new LinkedHashMap<>();
        //List<TriggerInfo.CollisionTriggerInfo> appends = new ArrayList<>();

        Vector3dc velocity = projectile.getVelocity();
        Vector3d movement = velocity.mul(getRaycastPredictTime(), new Vector3d());
        var clips = BallisticsUtil.raycastPlaneForBlocks(movement, getWorldBounds(pos, state, projectile.getShipToWorld()), getRaycastStep());

        for (ClipContext clipCtx : clips) {
            BlockTraverser.Ballistics.traverseAllIncludeShipAppend(level, clipCtx, skipShips, traverseResults);
        }

        EzDebug.log("vel:" + StrUtil.F2(velocity) + ", movement:" + StrUtil.F2(movement) + ", appendCnt:" + traverseResults.size());

        //todo 可能使用插入排序会更快？(数据部分有序，而且当数据量小的时候其快)
        traverseResults.values().stream().sorted(
            Comparator.comparingDouble(a -> a.sqDist)
        ).forEach(hitInfo -> {
            dest.add(new TriggerInfo.CollisionTriggerInfo(projectile, pos, state, hitInfo));
        });

        //EzDebug.log("total ballistics count:" + traverseResults.size());

        /*var hitInfos = this.getHitInfos(level, pos, state, controlData, stateData);
        for (var hitInfo : hitInfos) {
            dest.add(new TriggerInfo.CollisionTriggerInfo(
                projectileShip, pos, state, hitInfo
            ));
        }*/
    }

    @Override
    public void appendDescription(Set<String> descSet) { descSet.add("penetration"); }

    @Override
    public boolean canAccept(ServerLevel level, BlockPos pos, BlockState state, TriggerInfo info) {
        if (!info.triggerBlockPos.equals(pos)) {  //only accept self-made triggerInfo
            return false;
        }
        if (!(info instanceof TriggerInfo.CollisionTriggerInfo)) {
            EzDebug.error("the self? made trigger info is not CollisionTriggerInfo!");
            return false;
        }

        ApWarheadBlockEntity be = (ApWarheadBlockEntity)level.getBlockEntity(pos);
        if (be == null) {
            EzDebug.error("ap has no block entity!");
            return false;
        }
        if (be.isBouncing()) {
            EzDebug.highlight("ap is bouncing, reject other infos.");
            return false;
        }

        return true;  //only accept self-made triggerInfo
    }

    @Override
    public void effect(ServerLevel level, BlockPos effectorBp, BlockState effectorState, TriggerInfo info) {
        if (!(info instanceof TriggerInfo.CollisionTriggerInfo collisionInfo)) {
            EzDebug.error("ap accept a non collision info"); return;
        }
        if (!(level.getBlockEntity(info.triggerBlockPos) instanceof ApWarheadBlockEntity apBe)) {
            EzDebug.error("ap has no block entity"); return;
        }
        if (apBe.isBouncing()) {
            EzDebug.warn("ap shouldn't accept trigger info after bouncing"); return;
        }

       // EzDebug.light("sqDist:" + collisionInfo.hitInfo.sqDist);

        apBe.onAcceptTriggerInfo(collisionInfo);
    }


    @Override
    public boolean shouldTerminateAfterEffecting(TriggerInfo info) {
        return false;
    }

    @Override
    public Class<ApWarheadBlockEntity> getBlockEntityClass() { return ApWarheadBlockEntity.class; }
    @Override
    public BlockEntityType<? extends ApWarheadBlockEntity> getBlockEntityType() { return WapBlockEntites.AP_BE.get(); }
    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return ((level, blockPos, state, be) -> ((ApWarheadBlockEntity) be).tickUpdate());
    }
}
