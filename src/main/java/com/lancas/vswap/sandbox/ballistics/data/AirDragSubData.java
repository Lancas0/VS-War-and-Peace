package com.lancas.vswap.sandbox.ballistics.data;

import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3d;
import org.joml.primitives.AABBdc;

import java.util.concurrent.atomic.AtomicReference;

public class AirDragSubData implements IComponentData<AirDragSubData>, IComponentDataReader<AirDragSubData> {
    public final Vector3d localAirDragCenter = new Vector3d();
    public double localYzArea = 0;
    public double localXzArea = 0;
    public double localXyArea = 0;



    public AirDragSubData copyData(AirDragSubData src) {
        localAirDragCenter.set(src.localAirDragCenter);
        return this;
    }
    @Override
    public AirDragSubData overwriteDataByShip(ISandBoxShip ship) {
        localAirDragCenter.zero();

        AtomicReference<Double> totalDragFactor = new AtomicReference<>(0.0);
        ship.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
            double dragFactor = WapBlockInfos.drag_factor.valueOrDefaultOf(state);
            localAirDragCenter.add(JomlUtil.d(localPos).mul(dragFactor));
            totalDragFactor.updateAndGet(v -> v + dragFactor);
        });

        if (totalDragFactor.get() >= 1E-10) {  //must have block or have a non-zero-dragFactor block
            localAirDragCenter.div(totalDragFactor.get());
        }

        localYzArea = localXzArea = localXyArea = 0;
        AABBdc localAABB = ship.getBlockCluster().getDataReader().getLocalAABB();
        localYzArea = JomlUtil.sideArea(localAABB, Direction.EAST);
        localXzArea = JomlUtil.sideArea(localAABB, Direction.UP);
        localXyArea = JomlUtil.sideArea(localAABB, Direction.SOUTH);
        return this;
    }

    @Override
    public CompoundTag saved() {
        /*return new NbtBuilder()
            .putCompound("loc_air_drag_center", NbtBuilder.tagOfVector3(localAirDragCenter))
            .putNumber("yz_area", localYzArea)
            .putNumber("xz_area", localXzArea)
            .putNumber("xy_area", localXyArea)
            .get();*/
        return new CompoundTag();  //not save/load anything, because all field are eventually overwrite by ship
    }
    @Override
    public AirDragSubData load(CompoundTag tag) {
        /*NbtBuilder.modify(tag)
            .readVector3d("loc_air_drag_center", localAirDragCenter)
            .readDoubleDo("yz_area", v -> localYzArea = v)
            .readDoubleDo("xz_area", v -> localXzArea = v)
            .readDoubleDo("xy_area", v -> localXyArea = v);
        return this;*/
        return this;
    }
}
