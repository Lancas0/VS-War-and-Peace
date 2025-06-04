package com.lancas.vswap;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class VsWapClient {
    public static final ConfigScreenHandler.ConfigScreenFactory FACTORY =
        new ConfigScreenHandler.ConfigScreenFactory(VsWapClient::openConfig);

    public static Screen openConfig(Minecraft mc, Screen screen)
    {
        Util.getPlatform().openFile(
            FMLPaths.CONFIGDIR.get().resolve(VsWap.COMMON_FILENAME).toFile()
        );
        return screen;
    }

    public static void init(ModLoadingContext modLoadingContext) {
        modLoadingContext.registerExtensionPoint(FACTORY.getClass(), () -> FACTORY);

        //ExpertGooglesOverlayRenderer.register();
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        /*event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "remaining_air", RemainingAirOverlay.INSTANCE);
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "train_hud", TrainHUD.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "value_settings", CreateClient.VALUE_SETTINGS_HANDLER);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "track_placement", TrackPlacementOverlay.INSTANCE);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "goggle_info", GoggleOverlayRenderer.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "blueprint", BlueprintOverlayRenderer.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "linked_controller", LinkedControllerClientHandler.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "schematic", CreateClient.SCHEMATIC_HANDLER);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "toolbox", ToolboxHandlerClient.OVERLAY);*/

    }

}