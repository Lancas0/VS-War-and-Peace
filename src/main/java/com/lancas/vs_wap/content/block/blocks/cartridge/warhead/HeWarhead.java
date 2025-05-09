package com.lancas.vs_wap.content.block.blocks.cartridge.warhead;

import com.lancas.vs_wap.content.explosion.CustomExplosion;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vs_wap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.ship.ballistics.api.ITerminalEffector;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.List;
import java.util.Set;

//todo add TRIGGERED avoid trigger multitimes
public class HeWarhead extends BlockPlus implements ITerminalEffector, ISandBoxBallisticBlock {
    public static final float EXPLOSIVE_POWER = 5;
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            HeWarhead.class,
            () -> List.of(
                new DefaultCartridgeAdder()
                //, EinherjarBlockInfos.mass.getOrCreateExplicit(HeWarhead.class, state -> 45.0)
            )
        );
    }

    public HeWarhead(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void appendDescription(Set<String> descSet) {
        descSet.add("explosive");
    }


    @Override
    public boolean canAccept(ServerLevel level, BlockPos pos, BlockState state, TriggerInfo info) {
        return (info instanceof TriggerInfo.ActivateTriggerInfo);
    }
    @Override
    public void effect(ServerLevel level, BlockPos effectorBp, BlockState effectorState, TriggerInfo info) {
        if (level.isClientSide) return;

        TriggerInfo.ActivateTriggerInfo activateInfo = (TriggerInfo.ActivateTriggerInfo)info;
        level.explode(null, activateInfo.targetPos.x, activateInfo.targetPos.y, activateInfo.targetPos.z, EXPLOSIVE_POWER, Level.ExplosionInteraction.BLOCK);

        //EzDebug.highlight("explode at pos:" + StrUtil.F2(activateInfo.targetPos));
    }
    @Override
    public boolean shouldTerminateAfterEffecting(TriggerInfo info) {
        return (info instanceof TriggerInfo.ActivateTriggerInfo);
    }

    @Override
    public void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, Vector3ic localPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
        infos.forEach(info -> {
            if (!(info instanceof SandBoxTriggerInfo.ActivateTriggerInfo activateInfo)) return;
            Vector3dc targetPos = activateInfo.targetPos;
            level.explode(null, activateInfo.targetPos.x, activateInfo.targetPos.y, activateInfo.targetPos.z, EXPLOSIVE_POWER, Level.ExplosionInteraction.BLOCK);
            //EzDebug.highlight("explode at pos:" + StrUtil.F2(activateInfo.targetPos));
            CustomExplosion exp = new CustomExplosion(
                level,
                null,
                targetPos.x(), targetPos.y(), targetPos.z(),
                ship.getRigidbody().getDataReader().getVelocity(),
                EXPLOSIVE_POWER,
                false,
                Explosion.BlockInteraction.DESTROY
            );
            exp.explode();
            exp.finalizeExplosion(true);

            terminateByEffect.set(true);
        });
    }
}
