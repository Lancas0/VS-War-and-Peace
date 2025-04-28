package com.lancas.vs_wap.ship.attachment;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoPlayerCollisionAttachment {  //todo use this thing or someother thing to replace slug regex match
    /*public static ShipBytesDataAttachment getOrAdd(@NotNull ServerShip ship) {
        ShipBytesDataAttachment attachment = ship.getAttachment(ShipBytesDataAttachment.class);
        if (attachment == null) {
            attachment = new ShipBytesDataAttachment();
            ship.saveAttachment(ShipBytesDataAttachment.class, attachment);
        }

        return attachment;
    }*/

    private NoPlayerCollisionAttachment() {}
    public NoPlayerCollisionAttachment(@Nullable List<UUID> inIgnorePlayers, boolean inIgnoreAny) {
        ignorePlayerUUIDs.clear();
        if (inIgnorePlayers != null)
            ignorePlayerUUIDs.addAll(inIgnorePlayers);

        ignoreAnyPlayer = inIgnoreAny;
    }

    public static NoPlayerCollisionAttachment apply(@NotNull ServerShip ship, List<UUID> inIgnorePlayers) {
        var att = ship.getAttachment(NoPlayerCollisionAttachment.class);
        if (att == null) {
            att = new NoPlayerCollisionAttachment(inIgnorePlayers, false);
            ship.saveAttachment(NoPlayerCollisionAttachment.class, att);
        } else {
            att.ignorePlayerUUIDs.clear();
            att.ignorePlayerUUIDs.addAll(inIgnorePlayers);
        }

        return att;
    }
    public static NoPlayerCollisionAttachment applyIgnoreAny(@NotNull ServerShip ship) {
        var att = ship.getAttachment(NoPlayerCollisionAttachment.class);
        if (att == null) {
            att = new NoPlayerCollisionAttachment(null, true);
            ship.saveAttachment(NoPlayerCollisionAttachment.class, att);
        } else {
            att.ignoreAnyPlayer = true;
        }

        return att;
    }
    /*public static boolean isCollision(@NotNull ServerShip ship, Player player) {
        if (player == null) return false;
        var att = ship.getAttachment(NoPlayerCollisionAttachment.class);
        if (att == null) return true;

        EzDebug.log("ignPlayerUUID:" + att.ignorePlayerUUID + ", cur:" + player.getUUID());

        if (att.ignorePlayerUUID == null) return true;
        if (att.ignorePlayerUUID.equals(player.getUUID())) return false;
        return true;
    }*/

    //public UUID ignorePlayerUUID;
    private final List<UUID> ignorePlayerUUIDs = new ArrayList<>();
    private boolean ignoreAnyPlayer = false;
}
