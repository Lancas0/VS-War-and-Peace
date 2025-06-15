package com.lancas.vswap.content.block.blocks.industry.projector;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.core.Direction;
/*
public class VsProjectorInstance<T extends ICogWheel> extends SingleRotatingInstance<T> {
    public VsProjectorInstance(MaterialManager materialManager, T blockEntity) {
        super(materialManager, blockEntity);
    }

    protected Instancer<RotatingData> getModel() {
        //Direction dir = this.getShaftDirection();
        return this.getRotatingMaterial().getModel(AllPartialModels.SHAFTLESS_COGWHEEL, this.blockState, dir);
    }
}
*/