package com.lancas.vs_wap.sandbox.ballistics.data;

import com.lancas.vs_wap.subproject.sandbox.component.data.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;

public class BallisticData implements IComponentData<BallisticData>, IExposedComponentData<BallisticData> {
    public static final double TIME_OUT_SECONDS = 60;
    public static final double RANGE_OUT_LOWER_Y = -100;

    public final BallisticInitialStateSubData initialStateData = BallisticInitialStateSubData.createDefault();
    public final BallisticBarrelContextSubData barrelCtx = new BallisticBarrelContextSubData();
    public final AirDragSubData airDragData = new AirDragSubData();

    public double elapsedTime = 0;
    public boolean terminated = false;


    private BallisticData() {}
    public BallisticData(BallisticInitialStateSubData inInitialStateData, BallisticBarrelContextSubData inBarrelCtx, AirDragSubData inAirDragData) {
        initialStateData.copyData(inInitialStateData);
        barrelCtx.copyData(inBarrelCtx);
        airDragData.copyData(inAirDragData);
    }
    public static BallisticData makeDefault() { return new BallisticData(); }


    @Override
    public BallisticData copyData(BallisticData src) {
        initialStateData.copyData(src.initialStateData);
        barrelCtx.copyData(src.barrelCtx);
        airDragData.copyData(src.airDragData);
        terminated = src.terminated;
        return this;
    }
    @Override
    public BallisticData overwriteDataByShip(SandBoxServerShip ship) {
        initialStateData.overwriteDataByShip(ship);
        barrelCtx.overwriteDataByShip(ship);
        airDragData.overwriteDataByShip(ship);
        return this;
    }
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putCompound("initial_state_data", initialStateData.saved())
            .putCompound("state_data", barrelCtx.saved())
            .putCompound("air_drag_data", airDragData.saved())
            .putBoolean("terminated", terminated)
            .get();
    }
    @Override
    public IComponentData<BallisticData> load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readCompoundDo("initial_state_data", initialStateData::load)
            .readCompoundDo("state_data", barrelCtx::load)
            .readCompoundDo("air_drag_data", airDragData::load)
            .readBooleanDo("terminated", v -> terminated = v);
        return this;
    }
}
