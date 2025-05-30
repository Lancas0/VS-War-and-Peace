package com.lancas.vswap.content.item.items.docker;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class DockerItemRenderProperty implements IClientItemExtensions {
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return DockerItemRenderer.INSTANCE;
    }
}