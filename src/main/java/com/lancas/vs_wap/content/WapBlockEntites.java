package com.lancas.vs_wap.content;

import com.lancas.vs_wap.content.blockentity.*;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.lancas.vs_wap.ModMain.REGISTRATE;
import static com.lancas.vs_wap.content.WapBlocks.*;
import static com.lancas.vs_wap.content.WapBlocks.Cartridge.Warhead.AP_CORE_WARHEAD;
import static com.lancas.vs_wap.content.WapBlocks.Cartridge.Warhead.BLUNT_AP_WARHEAD;

public class WapBlockEntites {

    public static final BlockEntityEntry<ScopeBE> SCOPE_BE = REGISTRATE
        .blockEntity("scope_block_entity", ScopeBE::new)
        .validBlocks(MECH_SCOPE_BLOCK, TELE_SCOPE_BLOCK)
        .register();

    /*public static final BlockEntityEntry<SignalDetectorBlockEntity> SIGNAL_DETECTOR_BE = REGISTRATE
        .blockEntity("signal_detector_block_entity", SignalDetectorBlockEntity::new)
        .validBlock(FIRE_DETECTOR_BLOCK)
        .register();*/

    /*public static final BlockEntityEntry<DelayedExplosiveBlockEntity> DELAYED_EXPLOSIVE_BLOCK_ENTITY = REGISTRATE
        .blockEntity(DelayedExplosiveBlockEntity.ID, DelayedExplosiveBlockEntity::new)
        .validBlocks(SMALL_DELAYED_EXPLOSIVE_BLOCK)
        .register();*/

    /*public static final BlockEntityEntry<EngineIgniterBlockEntity> ENGINE_IGNITER_BE = REGISTRATE
        .blockEntity("engine_igniter_be", EngineIgniterBlockEntity::new)
        .validBlocks(ENGINE_IGNITER)
        .register();*/

    public static final BlockEntityEntry<ActivatorBlockEntity> ACTIVATOR_BE = REGISTRATE
        .blockEntity("activator_be", ActivatorBlockEntity::new)
        .validBlocks(ACTIVATOR)
        .register();

    /*public static final BlockEntityEntry<ShellFrameBE> SHELL_FRAME_BE = REGISTRATE
        .blockEntity("shell_frame_be", ShellFrameBE::new)
        .validBlocks(Cartridge.SHELL_FRAME)
        .register();*/

    /*public static final BlockEntityEntry<ImpactFuseBE> IMPACT_FUSE_BE = REGISTRATE
        .blockEntity("impact_be", ImpactFuseBE::new)
        .validBlocks(IMPACT_FUSE)
        .register();*/

    /*public static final BlockEntityEntry<ApWarheadBE> AP_WARHEAD_BE = REGISTRATE
        .blockEntity("ap_warhead_be", ApWarheadBE::new)
        .validBlocks(AP_WARHEAD)
        .register();*/

    /*public static final BlockEntityEntry<PrimerBE> PRIMER_BE = REGISTRATE
        .blockEntity("primer_be", PrimerBE::new)
        .validBlocks(Cartridge.PRIMER)
        .register();*/

    /*public static final BlockEntityEntry<BreechBE> BREECH_BE = REGISTRATE
        .blockEntity("breech_be", BreechBE::new)
        .validBlocks(Artillery.EJECTING_BREECH, Artillery.DROPPING_BREECH)
        .register();*/

    /*public static final BlockEntityEntry<RocketBoosterBE> ROCKET_BOOSTER_BE = REGISTRATE
        .blockEntity("rocket_booster_be", RocketBoosterBE::new)
        .validBlocks(Cartridge.ROCKET_BOOSTER)
        .register();*/

    public static final BlockEntityEntry<ApWarheadBlockEntity> AP_BE = REGISTRATE
        .blockEntity("ap_be", ApWarheadBlockEntity::new)
        .validBlocks(AP_CORE_WARHEAD, BLUNT_AP_WARHEAD)
        .register();

    public static final BlockEntityEntry<VSProjectorBE> VS_PROJECTOR_BE = REGISTRATE
        .blockEntity("vs_projector_be", VSProjectorBE::new)
        .validBlocks(Industrial.VS_PROJECTOR)
        .register();

    public static void register() { }

    //Create.REGISTRATE.blockEntity("schematicannon", SchematicannonBlockEntity::new).instance(() -> SchematicannonInstance::new).validBlocks(new NonNullSupplier[]{
    //    com.simibubi.create.AllBlocks.SCHEMATICANNON}).renderer(() -> SchematicannonRenderer::new).register();
    /*public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<?>> ScopeBlockEntityRO = BLOCK_ENTITIES.register(
        "scope_block_entity",
        () -> typeOf(ScopeBlockEntity::new, ScopeBlockRO.get())
    );


    private static <T extends BlockEntity> BlockEntityType<T> typeOf(BlockEntityType.BlockEntitySupplier<T> entity, Block... blocks) {
        return BlockEntityType.Builder.of(entity, blocks).build(null);
    }*/

    /*public static final RegistryObject<BlockEntityType<?>> FireSignalDetectorBlockEntity = BLOCK_ENTITIES.register(
        "fire_signal_detector_block_entity",
        () -> typeOf(AllSignalDetectorBlockEntities.FireSignalDetectorBlockEntity::new, FireSignalDetectorBlock.get())
    );
    public static final RegistryObject<BlockEntityType<?>> MoveForwardSignalDetectorBlockEntity = BLOCK_ENTITIES.register(
        "moveforward_signal_detector_block_entity",
        () -> typeOf(AllSignalDetectorBlockEntities.MoveForwardSignalDetectorBlockEntity::new, MoveForwardSignalDetectorBlock.get())
    );
    public static final RegistryObject<BlockEntityType<?>> MoveBackwardSignalDetectorBlockEntity = BLOCK_ENTITIES.register(
        "movebackward_signal_detector_block_entity",
        () -> typeOf(AllSignalDetectorBlockEntities.MoveBackwardSignalDetectorBlockEntity::new, MoveBackwardSignalDetectorBlock.get())
    );
    public static final RegistryObject<BlockEntityType<?>> MoveLeftSignalDetectorBlockEntity = BLOCK_ENTITIES.register(
        "moveleft_signal_detector_block_entity",
        () -> typeOf(AllSignalDetectorBlockEntities.MoveLeftSignalDetectorBlockEntity::new, MoveLeftSignalDetectorBlock.get())
    );
    public static final RegistryObject<BlockEntityType<?>> MoveRightSignalDetectorBlockEntity = BLOCK_ENTITIES.register(
        "moveright_signal_detector_block_entity",
        () -> typeOf(AllSignalDetectorBlockEntities.MoveRightSignalDetectorBlockEntity::new, MoveRightSignalDetectorBlock.get())
    );


    */
}
