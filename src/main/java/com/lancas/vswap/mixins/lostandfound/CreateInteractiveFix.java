package com.lancas.vswap.mixins.lostandfound;

/*
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreateInter)
public class CreateInteractiveFix {

}



public final void unlinkShipToContraption(long shipId, @NotNull AbstractContraptionEntity contraptionEntity) {
    Intrinsics.checkNotNullParameter(contraptionEntity, "contraptionEntity");
    if (contraptionEntity.m_9236_().f_46443_) {
        WeakReference<AbstractContraptionEntity> weakReference = shipIdToContraptionEntityClientInternal.get(Long.valueOf(shipId));
        AbstractContraptionEntity prevVal = weakReference != null ? weakReference.get() : null;
        if (prevVal != null && Intrinsics.areEqual(prevVal, contraptionEntity)) {
            shipIdToContraptionEntityClientInternal.remove(Long.valueOf(shipId));
            return;
        }
        return;
    }
    WeakReference<AbstractContraptionEntity> weakReference2 = shipIdToContraptionEntityServerInternal.get(Long.valueOf(shipId));
    AbstractContraptionEntity prevVal2 = weakReference2 != null ? weakReference2.get() : null;
    if (prevVal2 != null && Intrinsics.areEqual(prevVal2, contraptionEntity)) {
        shipIdToContraptionEntityServerInternal.remove(Long.valueOf(shipId));
    }
}
*/