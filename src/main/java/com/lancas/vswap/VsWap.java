package com.lancas.vswap;

import com.lancas.vswap.compact.create.arminteraction.ArmInteractionTypes;
import com.lancas.vswap.content.*;
import com.lancas.vswap.content.behaviour.item.DockerDispenseBehaviour;
import com.lancas.vswap.content.WapMass;
import com.lancas.vswap.event.EventMgr;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.content.WapPartialModels;
import com.lancas.vswap.sandbox.schedule.ClientShardShipScheduler;
import com.lancas.vswap.subproject.mstandardized.CategoryRegistry;
import com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(VsWap.MODID)
public class VsWap {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "vswap";
    public static final String COMMON_FILENAME = "vswap-common.toml";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation asRes(String path) { return new ResourceLocation(MODID, path); }

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);


    public VsWap() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        forgeBus.addListener(this::registerCommands);


        NetworkHandler.register();


        REGISTRATE.registerEventListeners(modEventBus);
        WapTabs.register(modEventBus);
        WapItems.register();
        WapCT.register();
        WapBlocks.register();
        WapBlockEntites.register();
        WapUI.register();
        ArmInteractionTypes.register();

        WapSounds.register(modEventBus);

        WapMass.INSTANCE.register();

        EventMgr.registerDefault();
        SandBoxEventMgr.register();

        ClientShardShipScheduler.register();

        //WapConfig.register(modLoadingContext);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> WapPartialModels::init);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WapConfig.SPEC, "vswap-common-config.toml");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, WapCommonConfig.SPEC, COMMON_FILENAME);


        Mod.EventBusSubscriber.Bus.FORGE.bus().get().addListener(EventPriority.HIGH, this::registerHPResourceManagers);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().addListener(EventPriority.NORMAL, this::registerNPResourceManagers);

        //MinecraftForge.EVENT_BUS.addListener();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> VsWapClient.init(modLoadingContext));
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        /*OGGER.info("HELLO FROM COMMON SETUP");

        if (WapCommonConfig.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(WapCommonConfig.magicNumberIntroduction + WapCommonConfig.magicNumber);

        WapCommonConfig.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));*/


        //RefWithFallback don't have the behaviour
        DispenserBlock.registerBehavior(WapItems.DOCKER.get(), new DockerDispenseBehaviour());
    }

    // Add the vanilla.json block item to the building blocks tab
    /*private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }*/

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    private void registerHPResourceManagers(AddReloadListenerEvent event) {
        event.addListener(new DoubleBlockInfoRegister());
    }
    private void registerNPResourceManagers(AddReloadListenerEvent event) {
        event.addListener(new CategoryRegistry());
    }

    /*@SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataProvider.Factory<DatapackBuiltinEntriesProvider> providerFactory = output ->
            new CategoryRegistry.Provider(output, event.getLookupProvider());

        event.getGenerator().addProvider(
            event.includeServer(),  //only in server
            providerFactory
        );

        EzDebug.highlight("event onGatherData");
    }*/

    /*private void registerCommands(RegisterCommandsEvent event) {
        WapCommands.register(event);
    }*/
    private void registerCommands(RegisterCommandsEvent event) {
        WapCommands.registerServerCommands(event.getDispatcher());
        /*VSCommands.registerServerCommands(event.dispatcher)

        if (event.commandSelection == ALL || event.commandSelection == INTEGRATED) {
            VSCommands.registerClientCommands(event.dispatcher)
        }*/
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }



    /*@SubscribeEvent
    public void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MyCategoryRegistry.KEY, MyCategoryRegistry.CODEC);
    }*/

}