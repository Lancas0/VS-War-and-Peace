package com.lancas.vs_wap.sandbox.component.ballistic.data;

import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.subproject.sandbox.INbtSavedObject;
import com.lancas.vs_wap.subproject.sandbox.component.data.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3d;
import org.joml.primitives.AABBic;

import java.util.concurrent.atomic.AtomicReference;

public class AirDragSubData implements IComponentData<AirDragSubData>, IExposedComponentData<AirDragSubData> {
    public final Vector3d localAirDragCenter = new Vector3d();
    public double localYzArea = 0;
    public double localXzArea = 0;
    public double localXyArea = 0;



    public AirDragSubData copyData(AirDragSubData src) {
        localAirDragCenter.set(src.localAirDragCenter);
        return this;
    }
    public AirDragSubData overwriteDataByShip(SandBoxServerShip ship) {
        localAirDragCenter.zero();

        AtomicReference<Double> totalDragFactor = new AtomicReference<>(0.0);
        ship.getCluster().foreach((localPos, state) -> {
            double dragFactor = WapBlockInfos.drag_factor.valueOrDefaultOf(state);
            localAirDragCenter.add(JomlUtil.d(localPos).mul(dragFactor));
            totalDragFactor.updateAndGet(v -> v + dragFactor);
        });

        if (totalDragFactor.get() >= 1E-10) {  //must have block or have a non-zero-dragFactor block
            localAirDragCenter.div(totalDragFactor.get());
        }

        localYzArea = localXzArea = localXyArea = 0;
        AABBic localAABB = ship.getLocalAABB();
        if (localAABB != null) {  //must have localAABB - at least one block
            localYzArea = JomlUtil.sideArea(ship.getLocalAABB(), Direction.EAST);
            localXzArea = JomlUtil.sideArea(ship.getLocalAABB(), Direction.UP);
            localXyArea = JomlUtil.sideArea(ship.getLocalAABB(), Direction.SOUTH);
        }
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
