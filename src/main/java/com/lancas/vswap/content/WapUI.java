package com.lancas.vswap.content;

import com.lancas.vswap.content.ui.ValkyrienBreechMenu;
import com.lancas.vswap.content.ui.ValkyrienBreechScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.lancas.vswap.VsWap.REGISTRATE;

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
