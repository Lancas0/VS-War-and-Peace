package com.lancas.vs_wap.content;

import com.lancas.vs_wap.content.ui.ValkyrienBreechMenu;
import com.lancas.vs_wap.content.ui.ValkyrienBreechScreen;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

import static com.lancas.vs_wap.ModMain.REGISTRATE;
import static net.minecraftforge.registries.ForgeRegistries.MENU_TYPES;

public class WapUI {

    public static MenuEntry<ValkyrienBreechMenu> VALKYRIEN_BREECH_MENU = REGISTRATE
        .menu("valkyrien_breech_menu", ValkyrienBreechMenu::new, () -> ValkyrienBreechScreen::new)
        .register();

    /*public static final RegistryObject<MenuType<ValkyrienBreechMenu>> EXAMPLE_CONTAINER =
        MENU_TYPES.register("example_container",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                new ValkyrienBreechMenu(windowId, inv)));*/

    public static void register() {}
}
