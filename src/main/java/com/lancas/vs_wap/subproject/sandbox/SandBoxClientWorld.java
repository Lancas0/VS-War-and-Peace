package com.lancas.vs_wap.subproject.sandbox;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.ship.ShipClientRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//todo store all renderers for each levelName
//get renderers by curLevelName ?

@OnlyIn(Dist.CLIENT)
public class SandBoxClientWorld {
    public static SandBoxClientWorld INSTANCE = new SandBoxClientWorld();
    private SandBoxClientWorld() {}

    private String curLevelName;  //not actually player level, the unupdated level with all current ship data
    private final Map<UUID, ShipClientRenderer> renderers = new Hashtable<>();

    public void reloadLevel(String levelName, Map<UUID, CompoundTag> savedRenders) {
        curLevelName = levelName;

        synchronized (renderers) {
            renderers.clear();
            for (var savedShip : savedRenders.entrySet()) {
                UUID uuid = savedShip.getKey();
                ShipClientRenderer renderer = new ShipClientRenderer(savedShip.getValue());//SerializeUtil.deserializeAsClient(savedShip.getValue());

                renderers.put(uuid, renderer);

                if (!Objects.equals(renderer.uuid, uuid)) {
                    EzDebug.error("the uuid key is not equal to ship uuid!");
                }
            }
        }
    }

    //todo note: note synced
    public Iterable<ShipClientRenderer> allRenderers() {
        return renderers.values();
    }

    public String getCurLevelName() { return curLevelName; }
    public void setCurLevelName(String s) { curLevelName = s; }


    @Nullable
    public ShipClientRenderer getRenderer(UUID uuid) {
        synchronized (renderers) { return renderers.get(uuid); }
    }

    //should only be used for network sync
    public void addRenderer(ShipClientRenderer renderer) {
        EzDebug.log("add renderer: " + renderer.uuid + " had key:" + renderers.containsKey(renderer.uuid));
        synchronized (renderers) { renderers.put(renderer.uuid, renderer); }
    }
    //should only be used for network sync
    public void removeRenderer(UUID uuid) {
        synchronized (renderers) { renderers.remove(uuid); }
    }
}
