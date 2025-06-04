package com.lancas.vswap.ship.feature.hide;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancas.vswap.VsWap;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.EventMgr;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber
public class VsHideAndSeek extends SavedData {
    public static ObjectMapper hideAndSeekMapper = new ObjectMapper();
    static {
        hideAndSeekMapper.setVisibility(hideAndSeekMapper.getSerializationConfig().getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
    }

    //private static final Logger log = LogManager.getLogger(VsHideAndSeek.class);
    private final ConcurrentHashMap<Long, IHideAndSeek> hidingShips = new ConcurrentHashMap<>();
    //don't save
    private final Set<Long> toHideShips = ConcurrentHashMap.newKeySet();
    private final Set<Long> toSeekShips = ConcurrentHashMap.newKeySet();

    public static VsHideAndSeek getOrCreate(ServerLevel inLevel) {
        return inLevel.getDataStorage().computeIfAbsent(
            t -> new VsHideAndSeek(inLevel).load(t),
            () -> new VsHideAndSeek(inLevel),
            VsWap.MODID + "_vs_hide_and_seek"
        );
    }

    private final ServerLevel level;
    private VsHideAndSeek(ServerLevel inLevel) {
        level = inLevel;
        EventMgr.Server.onVsShipUnloaded.addListener(id -> {  //todo can't handle all situation
            hidingShips.remove(id);
            toHideShips.remove(id);
            toSeekShips.remove(id);
            setDirty();
        });
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return new NbtBuilder()
            .putJacksonMap("hiding_ships", hidingShips, hideAndSeekMapper, hideAndSeekMapper).get();
    }
    public VsHideAndSeek load(CompoundTag tag) {
        hidingShips.clear();
        NbtBuilder.modify(tag).readJacksonMap("hiding_ships", Long.class, IHideAndSeek.class, hideAndSeekMapper, hideAndSeekMapper, hidingShips);

        //record to hide ships when load
        toHideShips.addAll(hidingShips.keySet());
        //toHideShips.put(hidingShips.keySet());
        /*//must have
        VSEvents.ShipLoadEvent.Companion.on(event -> {
            Long shipId = event.getShip().getId();
            IHideAndSeek hideAndSeek = hidingShips.get(shipId);
            if (hideAndSeek != null) {
                hideAndSeek.hide(event.getShip());
            }
        });*/

        return this;
    }

    public static void hide(ServerLevel inLevel, ServerShip ship, IHideAndSeek hideAndSeek) { getOrCreate(inLevel).hide(ship, hideAndSeek); }
    public void hide(@NotNull ServerShip ship, @NotNull IHideAndSeek hideAndSeek) {
        hidingShips.put(ship.getId(), hideAndSeek);

        if (!hideAndSeek.hide(level, ship)) {  //fail to hide
            toHideShips.add(ship.getId());
        } else {
            hideAndSeek.onSuccessHide(ship);
        }

        setDirty();
    }
    public static void seek(ServerLevel inLevel, ServerShip ship) { getOrCreate(inLevel).seek(ship); }
    public void seek(@NotNull ServerShip ship) {
        IHideAndSeek hideAndSeek = hidingShips.get(ship.getId());
        if (hideAndSeek == null)  //not hiding
            return;

        /*ServerShip ship = ShipUtil.getServerShipByID(level, shipId);
        if (ship == null)
            return null;*/

        if (!hideAndSeek.seek(level, ship)) {  //fail to seek
            toSeekShips.add(ship.getId());
        } else {
            hideAndSeek.onSuccessSeek(ship);
            hidingShips.remove(ship.getId());
        }
        setDirty();
    }

    private static final LazyTicks lazy = new LazyTicks(10);
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        if (!lazy.shouldWork())
            return;

        StreamSupport.stream(event.getServer().getAllLevels().spliterator(), false)
            .map(VsHideAndSeek::getOrCreate)
            .forEach(x -> {
                var toHideIt = x.toHideShips.iterator();
                while (toHideIt.hasNext()) {
                    long shipId = toHideIt.next();
                    ServerShip ship = ShipUtil.getServerShipByID(x.level, shipId);
                    if (ship == null)
                        continue;  //may the level is not fully initialized todo fail too much times and remove it?

                    IHideAndSeek hideAndSeek = x.hidingShips.get(shipId);
                    if (hideAndSeek == null) {
                        EzDebug.warn("fail to get hideAndSeek of " + shipId + " that is toHide");
                        toHideIt.remove();
                        continue;
                    }

                    if (hideAndSeek.hide(x.level, ship)) {
                        hideAndSeek.onSuccessHide(ship);
                        toHideIt.remove();
                    }
                }

                var toSeekIt = x.toSeekShips.iterator();
                while (toSeekIt.hasNext()) {
                    long shipId = toSeekIt.next();
                    ServerShip ship = ShipUtil.getServerShipByID(x.level, shipId);
                    if (ship == null)
                        continue;  //may the level is not fully initialized todo fail too much times and remove it?

                    IHideAndSeek hideAndSeek = x.hidingShips.get(shipId);
                    if (hideAndSeek == null) {
                        EzDebug.warn("fail to get hideAndSeek of " + shipId + " that is toSeek");
                        toSeekIt.remove();
                        continue;
                    }

                    if (hideAndSeek.seek(x.level, ship)) {
                        hideAndSeek.onSuccessSeek(ship);
                        toSeekIt.remove();
                        x.hidingShips.remove(shipId);
                    }
                }
            });
    }



}
