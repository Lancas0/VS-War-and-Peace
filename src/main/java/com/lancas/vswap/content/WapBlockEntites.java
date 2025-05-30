package com.lancas.vswap.content;

import com.lancas.vswap.content.block.blockentity.*;
import com.lancas.vswap.content.block.blocks.industry.dock.DockBe;
import com.lancas.vswap.content.block.blocks.industry.dock.GreenPrintHolderBe;
import com.lancas.vswap.content.block.blocks.industry.shredder.ShredderBe;
import com.lancas.vswap.content.block.blocks.industry.shredder.ShredderRenderer;
import com.lancas.vswap.renderer.industry.ProjectorLenRenderer;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.lancas.vswap.ModMain.REGISTRATE;
import static com.lancas.vswap.content.WapBlocks.*;
import static com.lancas.vswap.content.WapBlocks.Cartridge.Warhead.AP_CORE_WARHEAD;
import static com.lancas.vswap.content.WapBlocks.Cartridge.Warhead.BLUNT_AP_WARHEAD;

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

    public static final BlockEntityEntry<ValkyrienBreechBE> VALKYRIEN_BREECH_BE = REGISTRATE
        .blockEntity("valkyrien_be", ValkyrienBreechBE::new)
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
        .validBlocks(Industrial.Projector.VS_PROJECTOR)
        .register();

    public static final BlockEntityEntry<ProjectorLenBe> PROJECTOR_LEN_BE = REGISTRATE
        .blockEntity("projector_len_be", ProjectorLenBe::new)
        .validBlocks(Industrial.Projector.PROJECT_LEN)
        .renderer(() -> ProjectorLenRenderer::new)
        .register();

    /*public static final BlockEntityEntry<UnderConstructionBe> UNDER_CONSTRUCTION_BE = REGISTRATE
        .blockEntity("under_construction_be", UnderConstructionBe::new)
        .validBlocks(Industrial.UNDER_CONSTRUCTION)
        //.renderer(() -> ProjectorLenRenderer::new)
        .register();

    public static final BlockEntityEntry<RobotArmBe> ROBOT_ARM_BE = REGISTRATE
        .blockEntity("robot_arm_be", RobotArmBe::new)
        .validBlocks(Industrial.ROBOT_ARM)
        //.renderer(() -> ArmRenderer::new)
        .register();*/


    /*public static final BlockEntityEntry<LostAndFoundBe> LOST_AND_FOUND_BE = REGISTRATE
        .blockEntity("lost_and_found_be", LostAndFoundBe::new)
        .validBlocks(Debug.LOST_AND_FOUND)
        .register();*/


    public static final BlockEntityEntry<ShredderBe> SHREDDER_BE = REGISTRATE
        .blockEntity("shredder_be", ShredderBe::new)
        .instance(() -> ShaftInstance::new)
        .validBlocks(Industrial.Machine.SHREDDER)
        .renderer(() -> ShredderRenderer::new)
        .register();

    public static final BlockEntityEntry<DockBe> DOCK_BE = REGISTRATE
        .blockEntity("dock_be", DockBe::new)
        //.instance(() -> DockInstance::new)
        .validBlocks(Industrial.DOCK)
        //.renderer(() -> PulverizerRenderer::new)
        .register();


    public static final BlockEntityEntry<GreenPrintHolderBe> GREEN_PRINT_HOLDER_BE = REGISTRATE
        .blockEntity("green_print_holder", GreenPrintHolderBe::new)
        //.instance(() -> DockInstance::new)
        .validBlocks(Industrial.GREEN_PRINT_HOLDER)
        //.renderer(() -> PulverizerRenderer::new)
        .register();
    /*public static final BlockEntityEntry<ArmBlockEntity> INDUSTRIAL_ROBOT_ARM_BE = REGISTRATE
        .blockEntity("industrial_robot_arm_be", ArmBlockEntity::new)
        //.visual(() -> ArmRenderer::new, false)
        .validBlocks(Industrial.INDUSTRIAL_ROBOT_ARM)
        .renderer(() -> ArmRenderer::new)
        .register();*/

    /*public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_CHAIN_COGWHEEL = REGISTRATE
        .blockEntity("encased_chain_cogwheel", SimpleKineticBlockEntity::new)
        //.visual(() -> EncasedCogVisual::small, false)
        //.validBlocks(CCBlocks.ENCASED_CHAIN_COGWHEEL)
        .renderer(() -> EncasedCogRenderer::small)
        .register();*/


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
