package com.lancas.vs_wap.sandbox.component.ballistic.data;

import com.lancas.vs_wap.subproject.sandbox.component.data.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;

public class BallisticBarrelContextSubData implements IComponentData<BallisticBarrelContextSubData>, IExposedComponentData<BallisticBarrelContextSubData> {
    public static final int ABSOLUTE_EXIT_BARREL_TICK = 4;

    public BallisticBarrelContextSubData copyData(BallisticBarrelContextSubData src) {
        exitedBarrelTicks = src.exitedBarrelTicks;
        alwaysInBarrelSinceLaunch = src.alwaysInBarrelSinceLaunch;
        return this;
    }

    public int exitedBarrelTicks = 0;              //determine if to apply anti gravity and start detect, > ABSOLUTE_EXIT_BARREL_TICK so exit barrel
    public boolean alwaysInBarrelSinceLaunch = true;  //determine if to apply propelling power


    public boolean isAbsoluteExitBarrel() { return exitedBarrelTicks >= ABSOLUTE_EXIT_BARREL_TICK; }

    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putNumber("exited_barrel_ticks", exitedBarrelTicks)
            .putBoolean("in_barrel_since_launch", alwaysInBarrelSinceLaunch)
            //.putBoolean("terminated", terminated)
            .get();
    }
    @Override
    public BallisticBarrelContextSubData load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readIntDo("exited_barrel_ticks", v -> exitedBarrelTicks = v)
            .readBooleanDo("in_barrel_since_launch", v -> alwaysInBarrelSinceLaunch = v);
            //.readBooleanDo("terminated", v -> terminated = v);

        return this;
    }
}
