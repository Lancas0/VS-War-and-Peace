package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.mstandardized.Category;
import com.lancas.vswap.subproject.mstandardized.CategoryRegistry;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DockDisplayLinkSrc extends DisplaySource {

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext ctx, DisplayTargetStats stats) {
        DisplayLinkBlockEntity disLink = ctx.blockEntity();
        Direction direction = disLink.getDirection();

        if (ctx.level().isClientSide)
            return Collections.emptyList();

        if (!(ctx.level().getBlockEntity(ctx.getSourcePos()) instanceof DockBe dockBe)) {
            EzDebug.warn("fail to get dockBe for DisplayLink");
            return Collections.emptyList();
        }

        DockBe controllerBe = dockBe.getControllerBE();

        if (controllerBe.constructHandler == null) {
            return List.of(Component.translatable("msg.vswap.display_link.no_construction"));
        }
        if (controllerBe.constructHandler.isCompleted()) {
            return List.of(Component.translatable("msg.vswap.display_link.construction_completed"));
        }

        List<MutableComponent> comps = new ArrayList<>();
        comps.add(Component.translatable("vswap.category.word.needed_material"));
        for (var materialEntry : controllerBe.constructHandler.toConstruct.entrySet()) {
            Category category = CategoryRegistry.getCategory(materialEntry.getKey());
            if (category.isEmpty()) {
                EzDebug.warn("get empty category!");
                continue;
            }

            if (materialEntry.getValue().isEmpty()) {
                EzDebug.warn("the construct pos is empty!");
                continue;
            }

            comps.add(
                Component.literal(String.valueOf(materialEntry.getValue().size()))
                    .append(Component.translatable("vswap.category.word.of"))
                    .append(category.getLocalized())
            );
        }

        return comps;
    }
}
