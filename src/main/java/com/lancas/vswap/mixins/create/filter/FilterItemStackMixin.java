package com.lancas.vswap.mixins.create.filter;

import com.lancas.vswap.content.item.IFilterItem;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FilterItemStack.class)
public class FilterItemStackMixin {
    @Inject(
        method = "of(Lnet/minecraft/world/item/ItemStack;)Lcom/simibubi/create/content/logistics/filter/FilterItemStack;",
        at = @At(value = "RETURN"),
        remap = false,
        cancellable = true
    )
    private static void ofMixin(ItemStack filter, CallbackInfoReturnable<FilterItemStack> cir) {
        if (filter.getItem() instanceof IFilterItem f) {
            cir.setReturnValue(f.getFilterItemStack(filter));
        }
    }
}
