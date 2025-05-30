package com.lancas.vswap.content.block.blocks.industry.dock;
/*
import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.hardcoded.ModelPart;
import com.jozufozu.flywheel.core.materials.FlatLit;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.lancas.vswap.content.WapPartialModels;
import com.lancas.vswap.debug.EzDebug;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DockInstance extends BlockEntityInstance<DockBe> {
    private LazyOptional<ModelData> edgeNInstance = LazyOptional.empty();
    private LazyOptional<ModelData> edgeSInstance = LazyOptional.empty();
    private LazyOptional<ModelData> edgeWInstance = LazyOptional.empty();
    private LazyOptional<ModelData> edgeEInstance = LazyOptional.empty();

    private LazyOptional<ModelData> cornerNWInstance = LazyOptional.empty();
    private LazyOptional<ModelData> cornerNEInstance = LazyOptional.empty();
    private LazyOptional<ModelData> cornerSWInstance = LazyOptional.empty();
    private LazyOptional<ModelData> cornerSEInstance = LazyOptional.empty();

    public DockInstance(MaterialManager materialManager, DockBe blockEntity) {
        super(materialManager, blockEntity);
    }
    private ModelData edgeInstance() { return getTransformMaterial().getModel(WapPartialModels.DOCK_PLATE_EDGE, this.blockState).createInstance(); } //todo use solid?
    private LazyOptional<ModelData> lazyEdgeInstance(Consumer<ModelData> transformer) { return LazyOptional.of(() -> {
        ModelData m = edgeInstance().loadIdentity();
        transformer.accept(m);
        return m;
    }); }
    private ModelData cornerInstance() { return getTransformMaterial().getModel(WapPartialModels.DOCK_PLATE_CORNER, this.blockState).createInstance(); } //todo use solid?
    private LazyOptional<ModelData> lazyCornerInstance(Consumer<ModelData> transformer) { return LazyOptional.of(() -> {
        ModelData m = cornerInstance().loadIdentity();
        transformer.accept(m);
        return m;
    }); }

    public double TRANSLATION = 7.0 / 16.0;
    public float HALF_PI = (float)(Math.PI / 2.0);
    @Override
    public void init() {
        super.init();
        boolean connectiveN = blockState.getValue(Dock.CONNECT_N);
        boolean connectiveS = blockState.getValue(Dock.CONNECT_S);
        boolean connectiveW = blockState.getValue(Dock.CONNECT_W);
        boolean connectiveE = blockState.getValue(Dock.CONNECT_E);

        if (connectiveN)
            edgeNInstance = lazyEdgeInstance(m -> m.translate(getInstancePosition()).translate(0, 0, -TRANSLATION));
        if (connectiveS)
            edgeSInstance = lazyEdgeInstance(m -> m.translate(getInstancePosition()).translate(0, 0, TRANSLATION));
        if (connectiveW)
            edgeWInstance = lazyEdgeInstance(m -> m.translate(getInstancePosition()).translate(-TRANSLATION, 0, 0).rotateCentered(Direction.UP, HALF_PI));
        if (connectiveE)
            edgeEInstance = lazyEdgeInstance(m -> m.translate(getInstancePosition()).translate(TRANSLATION, 0, 0).rotateCentered(Direction.UP, HALF_PI));

        if (connectiveN && connectiveW)
            cornerNWInstance = lazyCornerInstance(m -> m.translate(this.getInstancePosition()).translate(-TRANSLATION, 0, -TRANSLATION));
        if (connectiveN && connectiveE)
            cornerNEInstance = lazyCornerInstance(m -> m.translate(this.getInstancePosition()).translate(TRANSLATION, 0, -TRANSLATION));
        if (connectiveS && connectiveW)
            cornerSWInstance = lazyCornerInstance(m -> m.translate(this.getInstancePosition()).translate(-TRANSLATION, 0, TRANSLATION));
        if (connectiveS && connectiveE)
            cornerSEInstance = lazyCornerInstance(m -> m.translate(this.getInstancePosition()).translate(TRANSLATION, 0, TRANSLATION));

        foreachInstances(m -> {});  //call it and auto load
    }

    private void foreachInstances(NonNullConsumer<ModelData> consumer) {
        edgeNInstance.ifPresent(consumer);
        edgeSInstance.ifPresent(consumer);
        edgeWInstance.ifPresent(consumer);
        edgeEInstance.ifPresent(consumer);

        cornerNWInstance.ifPresent(consumer);
        cornerNEInstance.ifPresent(consumer);
        cornerSWInstance.ifPresent(consumer);
        cornerSEInstance.ifPresent(consumer);
    }

    /.*@Override  //don't know when update() is called, but I known when blockState change, init() will called
    public void update() {
        super.update();
        EzDebug.log("DockInstance update");
    }*./


    @Override
    protected void remove() {
        foreachInstances(ModelData::delete);
    }

    @Override
    public void updateLight() {
        List<ModelData> toLight = new ArrayList<>();
        foreachInstances(toLight::add);

        this.relight(this.getWorldPosition(), toLight.stream());
    }
}
*/