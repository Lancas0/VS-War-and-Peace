package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;

//todo sync
public class SandBoxRigidbody extends AbstractComponentBehaviour<SandBoxRigidbodyData> {
    private boolean massCenterDirty = true;
    private final Vector3d massCenter = new Vector3d();

    @Override
    protected SandBoxRigidbodyData makeData() {
        return new SandBoxRigidbodyData();
    }

    @Override
    public void loadData(SandBoxServerShip inShip, SandBoxRigidbodyData src) {
        ship = inShip;
        data.copyData(src);

        //I suppose it's no need to sync when init
        data.mass = 0;
        data.localPosMassMul.zero();
        for (var blockEntry : ship.getCluster().allBlocks()) {
            BlockPos localPos = blockEntry.getKey();
            BlockState state = blockEntry.getValue();
            double curMass = WapBlockInfos.mass.valueOrDefaultOf(state);

            data.mass += curMass;
            data.localPosMassMul.add(JomlUtil.dLowerCorner(localPos).mul(curMass));  //本地坐标实际为方块中心位置，需要dLowerCorner
        }

        massCenterDirty = true;
    }

    public Vector3dc getLocalMassCenter() {
        if (data.mass < 1E-10)  {
            //when mass is 0, massCenter is at (0, 0, 0)
            massCenterDirty = true;
            return massCenter.zero();
        }

        if (massCenterDirty) {
            data.localPosMassMul.div(data.mass, massCenter);
            massCenterDirty = false;
        }

        return massCenter;
    }
    //todo cache
    public Vector3d getWorldMassCenter() {
        return ship.getTransform().localToWorldPos(getLocalMassCenter(), new Vector3d());
    }


    @Override
    public void physTick() {
        //EzDebug.log("phys tick:" + ship.getUuid());
    }

    @Override
    public void serverTick() {
        //EzDebug.log("server tick:" + ship.getUuid());
        //EzDebug.log("local massC:" + StrUtil.F2(getLocalMassCenter()) + ", world massC:" + StrUtil.F2(getWorldMassCenter()));
    }

    @Override
    public void onBlockReplaced(BlockPos localPos, BlockState oldState, BlockState newState) {
        if (oldState == null && newState == null) return;  //for safe, in fact it's included by following codes.

        double oldStateMass = WapBlockInfos.mass.valueOrDefaultOf(oldState);  //it's safe for handle null or air
        double newStateMass = WapBlockInfos.mass.valueOrDefaultOf(newState);

        data.localPosMassMul.add(JomlUtil.dLowerCorner(localPos).mul(newStateMass - oldStateMass));
        data.mass += newStateMass - oldStateMass;

        massCenterDirty = true;
    }
}
