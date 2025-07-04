package com.lancas.vswap.content;

//import com.lancas.vs_wap.content.items.docker.DockerItem;
import com.lancas.vswap.content.item.items.BreechUnloader;
import com.lancas.vswap.content.item.items.DebugTool;
import com.lancas.vswap.content.item.items.GrabIt;
import com.lancas.vswap.content.item.items.GreenPrint;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.item.items.vsmotion.MotionRecorder;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static com.lancas.vswap.VsWap.REGISTRATE;

public class WapItems {

    public static final ItemEntry<GrabIt> GRAB_IT = REGISTRATE
            .item("grab_it", GrabIt::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<Docker> DOCKER = REGISTRATE
        .item("docker", Docker::new)
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

    public static final ItemEntry<Item> GREEN_PRINT  = REGISTRATE
        .item("green_print", p -> new GreenPrint(p).getItem())
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

    public static final ItemEntry<MaterialStandardizedItem> MATERIAL_STANDARDIZED = REGISTRATE
        .item("ms_item", MaterialStandardizedItem::new)
        .properties(p -> p.stacksTo(64))
        .register();

    public static final ItemEntry<Item> CREATIVE_MS = REGISTRATE
        .item("ms_creative", Item::new)
        .properties(p -> p.stacksTo(64))
        .register();

    public static final ItemEntry<MotionRecorder> MOTION_RECORDER = REGISTRATE
        .item("motions_recorder", MotionRecorder::new)
        .properties(p -> p.stacksTo(1))
        .register();

    /*public static final ItemEntry<BlockItemPlus> WIND_CAP = REGISTRATE
        .item("wind_cap", p -> BlockItemPlus.fromBlockPlus(EinherjarBlocks.Cartridge.WIND_CAP.get(), p))
        .properties(p -> p)
        .register();*/

    /*public static final ItemEntry<PropellantItem> PROPELLANT_ITEM = REGISTRATE
        .item("propellant_item", PropellantItem::new)
        .*/

    public static class CraftItems {

        public static final ItemEntry<Item> CARBON_FIBER =  REGISTRATE
            .item("carbon_fiber", Item::new)
            .register();

        public static void register() {}
    }


    public static void register() {
        //test testIterable
        //WapItems.Docker.register();
        CraftItems.register();
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
