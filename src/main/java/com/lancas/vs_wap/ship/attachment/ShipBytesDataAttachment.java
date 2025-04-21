package com.lancas.vs_wap.ship.attachment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipBytesDataAttachment {
    public static ShipBytesDataAttachment getOrAdd(@NotNull ServerShip ship) {
        ShipBytesDataAttachment attachment = ship.getAttachment(ShipBytesDataAttachment.class);
        if (attachment == null) {
            attachment = new ShipBytesDataAttachment();
            ship.saveAttachment(ShipBytesDataAttachment.class, attachment);
        }

        return attachment;
    }


    public byte[] bytes;

    public ShipBytesDataAttachment() {
        bytes = null;
    }
    public ShipBytesDataAttachment(byte[] inBytes) {
        bytes = inBytes;
    }
}
