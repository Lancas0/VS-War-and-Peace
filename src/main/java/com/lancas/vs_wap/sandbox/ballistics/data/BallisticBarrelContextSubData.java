package com.lancas.vs_wap.sandbox.ballistics.data;

import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;

public class BallisticBarrelContextSubData implements IComponentData<BallisticBarrelContextSubData>, IComponentDataReader<BallisticBarrelContextSubData> {
    public static final int ABSOLUTE_EXIT_BARREL_TICK = 2;


    public BallisticBarrelContextSubData copyData(BallisticBarrelContextSubData src) {
        exitedBarrelTicks = src.exitedBarrelTicks;
        alwaysInBarrelSinceLaunch = src.alwaysInBarrelSinceLaunch;
        return this;
    }

    public int exitedBarrelTicks = 0;              //determine if to apply anti gravity and start detect, > ABSOLUTE_EXIT_BARREL_TICK so exit barrel
    public boolean alwaysInBarrelSinceLaunch = true;  //determine if to apply propelling power
    public boolean appliedHighPressureStage = false;

    public boolean isAbsoluteExitBarrel() { return exitedBarrelTicks >= ABSOLUTE_EXIT_BARREL_TICK; }

    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putNumber("exited_barrel_ticks", exitedBarrelTicks)
            .putBoolean("in_barrel_since_launch", alwaysInBarrelSinceLaunch)
            .putBoolean("applied_high_pressure_stage", appliedHighPressureStage)
            //.putBoolean("terminated", terminated)
            .get();
    }
    @Override
    public BallisticBarrelContextSubData load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readIntDo("exited_barrel_ticks", v -> exitedBarrelTicks = v)
            .readBooleanDo("in_barrel_since_launch", v -> alwaysInBarrelSinceLaunch = v)
            .readBooleanDo("applied_high_pressure_stage", v -> appliedHighPressureStage = v);
            //.readBooleanDo("terminated", v -> terminated = v);

        return this;
    }
}
