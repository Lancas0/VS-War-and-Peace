package com.lancas.vswap.mixins.create;

import com.lancas.vswap.content.block.blocks.industry.create.IPowerfulSailBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BearingContraption.class)
public class BearingContraptionMixin {

    @Shadow(remap = false)
    protected int sailBlocks;


    @Inject(
        method = "addBlock",
        at = @At("HEAD"),
        remap = false
    )
    public void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair, CallbackInfo ci) {
        if (pair.getKey().state().getBlock() instanceof IPowerfulSailBlock powerfulSail) {
            sailBlocks += powerfulSail.getSailPower();  //Don't abs: sometimes I will spring strange ideas: a sail decrease power?
        }
    }

}
