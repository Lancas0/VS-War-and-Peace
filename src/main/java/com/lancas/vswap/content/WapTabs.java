package com.lancas.vswap.content;

import com.lancas.vswap.VsWap;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.lancas.vswap.VsWap.MODID;


public class WapTabs {

    private static final DeferredRegister<CreativeModeTab> REGISTER =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = REGISTER.register("tab",
        () -> CreativeModeTab.builder()
            .title(Components.literal(MODID))
            .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
            .icon(WapBlocks.Cartridge.Warhead.HE_WARHEAD::asStack)
            .displayItems((params, output) -> {

                List<ItemStack> items = VsWap.REGISTRATE.getAll(Registries.ITEM)
                    .stream()
                    .map((regItem) -> new ItemStack(regItem.get()))
                    .toList();

                output.acceptAll(items);
            })
            .build());

    /*
     *
     *
     * */

    public static void register(IEventBus modEventBus) {
        //ControlCraft.LOGGER.info("Registering Creative Tabs");
        REGISTER.register(modEventBus);
    }

    /*public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("example_tab", () -> CreativeModeTab.builder()
        .withTabsBefore(CreativeModeTabs.COMBAT)
        //.icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            //output.accept(AllItems.EXAMPLE_ITEM.get()); // Add the vanilla.disabled item to the tab. For your own tabs, this method is preferred over the event
            AllItems.tabAccept(output);
        }).build());*/




    //public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModMain.MODID);

    /*public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab",
        () -> CreativeModeTab.builder()
            //.title(Components.translatable("itemGroup."+ ControlCraft.MODID +".main"))
            .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
            //.icon(ControlCraftBlocks.ANCHOR_BLOCK::asStack)
            .displayItems((params, output) -> {
                List<ItemStack> items = ModMain.REGISTRATE.getAll(Registries.ITEM)
                    .stream()
                    .map((regItem) -> new ItemStack(regItem.get()))
                    .toList();
                output.acceptAll(items);

                //output.accept(AllItems.TEST_ITEM);
            })
            .build());

     */

    /*public static final RegistryObject<CreativeModeTab> BasicTab = TABS.register(
        "einheriar_basic",
        () -> CreativeModeTab.builder()
            .title(Components.literal("einherair"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> AllItems.TestItemRO.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                AllItems.tabAccept(output);
            }).build()
    );


    public static void register(IEventBus modEventBus) {

        TABS.register(modEventBus);
    }*/
}
