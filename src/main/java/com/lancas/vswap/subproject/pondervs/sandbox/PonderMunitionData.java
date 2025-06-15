package com.lancas.vswap.subproject.pondervs.sandbox;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxPonderShip;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PonderMunitionData implements IComponentData<PonderMunitionData> {
    public PonderScene ponderScene;
    public final Vector3i localForward = new Vector3i();
    public final Vector3i localTip = new Vector3i();
    public final List<WorldSectionElement> breakableSections = new ArrayList<>();
    public int remainDestroyCnt = 0;
    public Function<SandBoxPonderShip, Double> destroyRadiusGetter = s -> 0.0;
    public Consumer<BlockState> breakCallback = s -> {};


    protected PonderMunitionData() { }
    public PonderMunitionData(PonderScene inPonderScene, Vector3ic inLocalForward, Vector3ic inLocalTip) {
        ponderScene = inPonderScene;
        localForward.set(inLocalForward);
        localTip.set(inLocalTip);
    }

    public PonderMunitionData addBreakableSection(WorldSectionElement section) {
        breakableSections.add(section);
        return this;
    }
    public PonderMunitionData withRemainDestroyCnt(int x) {
        remainDestroyCnt = x;
        return this;
    }
    public PonderMunitionData withDestroyRadius(Function<SandBoxPonderShip, Double> r) {
        destroyRadiusGetter = r;
        return this;
    }
    public PonderMunitionData withBreakCallback(Consumer<BlockState> callback) {
        breakCallback = callback;
        return this;
    }

    @Override
    public PonderMunitionData copyData(PonderMunitionData src) {
        ponderScene = src.ponderScene;
        localForward.set(src.localForward);
        localTip.set(src.localTip);
        breakableSections.clear(); breakableSections.addAll(src.breakableSections);
        remainDestroyCnt = src.remainDestroyCnt;
        destroyRadiusGetter = src.destroyRadiusGetter;
        breakCallback = src.breakCallback;
        return this;
    }

    @Override
    public CompoundTag saved() {
        //return null;
        return new CompoundTag();
    }

    @Override
    public PonderMunitionData load(CompoundTag tag) {
        //return null;
        return this;
    }
}
