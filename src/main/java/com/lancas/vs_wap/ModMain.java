package com.lancas.vs_wap;

import com.lancas.vs_wap.compact.create.arminteraction.ArmInteractionTypes;
import com.lancas.vs_wap.content.*;
import com.lancas.vs_wap.content.behaviour.item.DockerDispenseBehaviour;
import com.lancas.vs_wap.content.WapMass;
import com.lancas.vs_wap.event.EventMgr;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.sandbox.schedule.ClientShardShipScheduler;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModMain.MODID)
public class ModMain  {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "vs_wap";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation getResLocation(String path) { return new ResourceLocation(MODID, path); }

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);


    public ModMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();


        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        forgeBus.addListener(this::registerCommands);


        NetworkHandler.register();


        REGISTRATE.registerEventListeners(modEventBus);
        WapTabs.register(modEventBus);
        WapItems.register();
        WapBlocks.register();
        WapBlockEntites.register();
        WapUI.register();
        ArmInteractionTypes.register();

        WapMass.INSTANCE.register();

        EventMgr.registerDefault();
        SandBoxEventMgr.register();


        ClientShardShipScheduler.register();



        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //todo remove for test
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        Mod.EventBusSubscriber.Bus.FORGE.bus().get().addListener(EventPriority.HIGH, this::registerResourceManagers);
        //MinecraftForge.EVENT_BUS.addListener();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));


        //RefWithFallback don't have the behaviour
        DispenserBlock.registerBehavior(WapItems.Docker.SHIP_DATA_DOCKER.get(), new DockerDispenseBehaviour());
    }

    // Add the example block item to the building blocks tab
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

    private void registerResourceManagers(AddReloadListenerEvent event) {
        event.addListener(new DoubleBlockInfoRegister());
    }

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
}
