package com.lancas.vswap.content.block.blocks.industry.shredder;

/*
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PulverizerInstance extends SingleRotatingInstance<PulverizerBe> {
    public PulverizerInstance(MaterialManager materialManager, PulverizerBe blockEntity) {
        super(materialManager, blockEntity);
    }

    protected Instancer<RotatingData> getModel() {
        /.*if ((this.blockState.getValue(BlockStateProperties.FACING)).getAxis().isHorizontal()) {
            BlockState referenceState = this.blockState.rotate(blockEntity.getLevel(), blockEntity.getBlockPos(), Rotation.CLOCKWISE_180);
            Direction facing = referenceState.getValue(BlockStateProperties.FACING);
            return this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, referenceState, facing);
        } else {
            return this.getRotatingMaterial().getModel(this.shaft());
        }*./
        return getRotatingMaterial().getModel(shaft());
    }
}
*/