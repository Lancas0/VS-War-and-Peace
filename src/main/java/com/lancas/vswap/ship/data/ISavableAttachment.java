package com.lancas.vswap.ship.data;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.stream.Stream;

public interface ISavableAttachment {
    /*public static <T> void saveToShip(ServerShip ship, T att) {
        ship.saveAttachment((Class<T>) att.getClass(), att);
    }*/
    public static List<Class<? extends ISavableAttachment>> allToSave = List.of(
        HoldableAttachment.class
    );

    public Stream<BlockPos> getAllBpInShipToSave();
    public void loadAllBp(List<BlockPos> bpsInShip);

    public default <T extends ISavableAttachment> T getActual() {
        try {
            return (T)this;
        } catch (Exception e) {
            EzDebug.error("fail convert att of class:" + getClass());
            return null;
        }
    }
}
