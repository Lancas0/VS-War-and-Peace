package com.lancas.vs_wap.content;

//import com.lancas.vs_wap.content.items.docker.DockerItem;
import com.lancas.vs_wap.content.item.items.BreechUnloader;
import com.lancas.vs_wap.content.item.items.DebugTool;
import com.lancas.vs_wap.content.item.items.EinherjarWand;
import com.lancas.vs_wap.content.item.items.GreenPrint;
import com.lancas.vs_wap.content.item.items.docker.RefWithFallbackDocker;
import com.lancas.vs_wap.content.item.items.docker.ShipDataDocker;
import com.tterrag.registrate.util.entry.ItemEntry;

import static com.lancas.vs_wap.ModMain.REGISTRATE;

public class WapItems {
    public static class Docker {
        public static final ItemEntry<RefWithFallbackDocker> REF_WITH_FALLBACK_DOCKER = REGISTRATE
            .item("ref_docker", RefWithFallbackDocker::new)
            .properties(p -> p.stacksTo(1))
            .register();

        public static final ItemEntry<ShipDataDocker> SHIP_DATA_DOCKER = REGISTRATE
            .item("ship_data_docker", ShipDataDocker::new)
            .properties(p -> p.stacksTo(1))
            .register();

        public static void register() {}
    }

    public static final ItemEntry<EinherjarWand>

        EINHERJAR_WAND = REGISTRATE
            .item("vs_wap_wand", EinherjarWand::new)
            .properties(p -> p.stacksTo(1))
            .register();

    /*public static final ItemEntry<TestItem> TEST_ITEM = REGISTRATE
        .item("test_item", TestItem::new)
        .properties(p -> p.stacksTo(1))
        .register();

    public static final ItemEntry<ShipDuplicatorItem> SHIP_DUPLICATOR_ITEM = REGISTRATE
        .item(ShipDuplicatorItem.ID, ShipDuplicatorItem::new)
        .properties(p -> p.stacksTo(1))
        .register();*/

    /*public static final ItemEntry<DockerItem> DOCKER = REGISTRATE
        .item(DockerItem.ID, DockerItem::new)
        .properties(p -> p.stacksTo(1))
        .register();*/

    public static final ItemEntry<GreenPrint> GREEN_PRINT  = REGISTRATE
        .item("green_print", GreenPrint::new)
        .properties(p -> p.stacksTo(1))
        .register();

    /*public static final ItemEntry<ShipRotatorItem> SHIP_ROTATOR = REGISTRATE
        .item(ShipRotatorItem.ID, ShipRotatorItem::new)
        .properties(p -> p.stacksTo(1))
        .register();
        */

    public static final ItemEntry<DebugTool> DEBUG_TOOL = REGISTRATE
        .item("debug_tool", DebugTool::new)
        .properties(p -> p.stacksTo(1))
        .register();


    public static final ItemEntry<BreechUnloader> BREECH_UNLOADER = REGISTRATE
        .item("breech_unloader", BreechUnloader::new)
        .properties(p -> p.stacksTo(1))
        .register();

    /*public static final ItemEntry<BlockItemPlus> WIND_CAP = REGISTRATE
        .item("wind_cap", p -> BlockItemPlus.fromBlockPlus(EinherjarBlocks.Cartridge.WIND_CAP.get(), p))
        .properties(p -> p)
        .register();*/

    /*public static final ItemEntry<PropellantItem> PROPELLANT_ITEM = REGISTRATE
        .item("propellant_item", PropellantItem::new)
        .*/


    public static void register() {
        //test testIterable
        Docker.register();
    }
    //public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    //public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(AllBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    //public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
    //    .alwaysEat().nutrition(1).saturationMod(2f).build())));

    /*
    public static final RegistryObject<Item> ScopeBlockItem = ITEMS.register(
        "scope_block_item", () -> new BlockItem(AllBlocks.ScopeBlockRO.get(), new Item.Properties())
    );

    public static void tabAccept(CreativeModeTab.Output output) {
        output.accept(ScopeBlockItem.get());
    }*/


    // static final ItemEntry<TestItem> TEST_ITEM = REGISTRATE.item("test", TestItem::new)
    //    .register();
    //public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);

    /*public static final RegistryObject<Item> ShipSchemeSaverItem = ITEMS.register(
        "ship_scheme_saver",
        () -> new ShipSchemeSaverItem(new Item.Properties().stacksTo(1))
    );


    public static final RegistryObject<Item> ShipSchemeItem = ITEMS.register(
        "ship_scheme_item",
        () -> new ShipSchemeItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> ScalableShipCreatorItem = ITEMS.register(
        "scalable_ship_creator",
        () -> new ScalableShipCreatorItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> VSWeaponItem = ITEMS.register(
        "vs_weapon",
        () -> new VSWeaponItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> EinBootItem = ITEMS.register(
        "ein_boot",
        () -> new EinBoot()
    );

    public static final RegistryObject<Item> Test = ITEMS.register(
        "test_block_item",
        () -> new BlockItem(AllBlocks.TestBlock.get(), new Item.Properties())
        //() -> new TestItem(new Item.Properties().stacksTo(1))
    );
    public static final RegistryObject<Item> Test2 = ITEMS.register(
      "test_boot_item_block",
        () -> new BlockItem(AllBlocks.TestBootBlock.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> SolidAirBlockItem = ITEMS.register(
      "solid_block_item",
        () -> new BlockItem(AllBlocks.SolidAirBlock.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> TestItem = ITEMS.register(
      "test_item",
        () -> new TestItem(new Item.Properties())
    );


    public static List<RegistryObject<Item>> signalDetectorBlockItems = null;

    public static void register(IEventBus modEventBus) {
        signalDetectorBlockItems = new ArrayList<>();

        AllBlocks.forEach(block -> {
            signalDetectorBlockItems.add(ITEMS.register(
               block.getId().getPath() + "_item",
                () -> new BlockItem(block.get(), new Item.Properties())
            ));
        });

        ITEMS.register(modEventBus);
    }

    public static void tabAccept(CreativeModeTab.Output output) {
        output.accept(ShipSchemeSaverItem.get());
        //output.accept(TriggerBlockItem.get());
        output.accept(ScalableShipCreatorItem.get());
        output.accept(VSWeaponItem.get());
        output.accept(EinBootItem.get());
        output.accept(Test.get());
        output.accept(Test2.get());
        output.accept(SolidAirBlockItem.get());
        output.accept(TestItem.get());
        //output.accept(ScopeBlockItem.get());

        for (RegistryObject<Item> signalBlockItem : signalDetectorBlockItems) {
            output.accept(signalBlockItem.get());
        }
    }*/
    /*public static final RegistryObject<Item> ScopeBlockItem = ITEMS.register(
        "scope_block_item",
        () -> new BlockItem(AllBlocks.ScopeBlock.get(), new Item.Properties())
    );*/
    /*public static final RegistryObject<Item> TestItemRO = ITEMS.register(
      "test_item",
        () -> new Item(new Item.Properties())
    );

    public static void tabAccept(CreativeModeTab.Output output) {
        output.accept(TestItemRO.get());
    }*/


}
