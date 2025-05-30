package com.lancas.vswap.obsolete.ship;

/*
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SplitAttachment {
    public static void setSplitting(@NotNull ServerShip ship, boolean inCanSplit) {
        SplitAttachment att = ship.getAttachment(SplitAttachment.class);
        if (att != null) {
            att.canSplit = inCanSplit;
        } else {
            att = new SplitAttachment(inCanSplit);
            ship.saveAttachment(SplitAttachment.class, att);
        }
    }

    public boolean canSplit;

    private SplitAttachment() {}
    public SplitAttachment(boolean inCanSplit) { canSplit = inCanSplit; }
}
*/