package com.lancas.vswap.content;

import com.lancas.vswap.content.behaviour.block.ScopeMovementBehaviour;
import com.lancas.vswap.content.block.blocks.artillery.ArtilleryBarrelBlock;
import com.lancas.vswap.content.block.blocks.artillery.CartridgeRailBlock;
import com.lancas.vswap.content.block.blocks.artillery.breech.EjectingBreech;
import com.lancas.vswap.content.block.blocks.cartridge.attach.FloatingChamber;
import com.lancas.vswap.content.block.blocks.cartridge.booster.RocketBooster;
import com.lancas.vswap.content.block.blocks.cartridge.attach.glider.Glider;
import com.lancas.vswap.content.block.blocks.cartridge.attach.glider.GliderWing;
import com.lancas.vswap.content.block.blocks.cartridge.primer.TorpedoPrimer;
import com.lancas.vswap.content.block.blocks.cartridge.propellant.CombustiblePropellantBlock;
import com.lancas.vswap.content.block.blocks.cartridge.primer.PrimerBlock;
import com.lancas.vswap.content.block.blocks.cartridge.propellant.ShelledPropellant;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.*;
import com.lancas.vswap.content.block.blocks.cartridge.fuze.ImpactFuze;
import com.lancas.vswap.content.block.blocks.cartridge.attach.TailFin;
import com.lancas.vswap.content.block.blocks.cartridge.propellant.empty.EmptyShelledPropellant;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.apds.WarheadAPDS;
import com.lancas.vswap.content.block.blocks.debug.Block01;
import com.lancas.vswap.content.block.blocks.industry.create.CarbonFiberSail;
import com.lancas.vswap.content.block.blocks.industry.dock.*;
import com.lancas.vswap.content.block.blocks.industry.shredder.Shredder;
import com.lancas.vswap.content.block.blocks.industry.projector.ProjectionCenter;
import com.lancas.vswap.content.block.blocks.industry.projector.ProjectorLen;
import com.lancas.vswap.content.block.blocks.industry.projector.VSProjector;
//import com.lancas.vswap.content.block.blocks.redstone.ActivatorBlock;
import com.lancas.vswap.content.block.blocks.scope.MechScopeBlock;
import com.lancas.vswap.content.block.blocks.scope.TelescopicScope;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemPlus;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import static com.lancas.vswap.VsWap.REGISTRATE;


public class WapBlocks {

    public static class Artillery {
        /*public static BlockEntry<ArtilleryCornerBlock> ARTILLERY_CORNER = REGISTRATE //todo friction
        .block(ArtilleryCornerBlock.ID, ArtilleryCornerBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.mapColor(MapColor.STONE))
        .item()
        .build()
        .register();*/

        /*public static BlockEntry<ArtilleryPlateBlock> ARTILLERY_PLATE = REGISTRATE //todo friction
            .block("artillery_plate", ArtilleryPlateBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))
            .item()
            .build()
            .register();*/

        public static BlockEntry<ArtilleryBarrelBlock> ARTILLERY_BARREL = REGISTRATE
            .block("artillery_barrel", ArtilleryBarrelBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();

        public static BlockEntry<CartridgeRailBlock> CARTRIDGE_RAIL = REGISTRATE
            .block("cartridge_rail", CartridgeRailBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();

        /*public static BlockEntry<ArtilleryPart1Block> ARTILLERY_PART1 = REGISTRATE
            .block("artillery_part1", ArtilleryPart1Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();*/

        public static BlockEntry<EjectingBreech> EJECTING_BREECH = REGISTRATE
            .block("ejecting_breech", EjectingBreech::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();

        /*public static BlockEntry<DroppingBreech> DROPPING_BREECH = REGISTRATE
            .block("dropping_breech", DroppingBreech::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();*/

        /*public static BlockEntry<RapidBreech> RAPID_BREECH = REGISTRATE
            .block("rapid_breech", RapidBreech::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();*/

        /*public static BlockEntry<ArtilleryDoorBlock> ARTILLERY_DOOR = REGISTRATE
            .block("artillery_door", ArtilleryDoorBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();*/

        /*public static BlockEntry<HellBreech> HELL_BREECH = REGISTRATE
            .block("hell_breech", HellBreech::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();*/

        /*public static BlockEntry<ValkyrienBreech> VALKYRIEN_BREECH = REGISTRATE
            .block("valkyrien_breech", ValkyrienBreech::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();*/

        public static void register() { }
    }
    public static class Cartridge {
        public static class Propellant {
            public static class Empty {
                public static BlockEntry<EmptyShelledPropellant> EMPTY_PROPELLANT = REGISTRATE
                    .block("empty_shelled_propellant", EmptyShelledPropellant::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                    .item()
                    .build()
                    .register();

                /*public static BlockEntry<EmptyGauge12> EMPTY_GAUGE12 = REGISTRATE
                    .block("empty_gauge12", EmptyGauge12::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                    .item()
                    .build()
                    .register();*/

                public static void register() {}
            }
            public static void register() {
                Empty.register();
            }

            public static BlockEntry<ShelledPropellant> SHELLED_PROPELLANT = REGISTRATE
                .block("shelled_propellant", ShelledPropellant::new)
                .initialProperties(SharedProperties::wooden)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            public static BlockEntry<CombustiblePropellantBlock> COMBUSTIBLE_PROPELLANT = REGISTRATE
                .block("ccc", CombustiblePropellantBlock::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();

            /*public static BlockEntry<Gauge12Block> GAUGE12 = REGISTRATE
                .block("gauge12", Gauge12Block::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();*/


        }
        public static class Warhead {

            public static BlockEntry<WarheadAPCR> WARHEAD_APCR = REGISTRATE
                .block("warhead_apcr", WarheadAPCR::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            public static BlockEntry<WarheadAPDS> WARHEAD_APDS = REGISTRATE
                .block("warhead_apds", WarheadAPDS::new)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();
            /*public static BlockEntry<ApdsQuarterShell> APFSDS_QUARTER_SHELL = REGISTRATE
                .block("apfsds_quarter_shell", ApdsQuarterShell::new)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();*/

            /*public static BlockEntry<BluntApWarhead> BLUNT_AP_WARHEAD = REGISTRATE
                .block("blunt_ap_warhead", BluntApWarhead::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();*/

            public static BlockEntry<HeWarhead> HE_WARHEAD = REGISTRATE
                .block("he_warhead", HeWarhead::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            public static BlockEntry<WarheadHEAT> WARHEAD_HEAT = REGISTRATE
                .block("warhead_heat", WarheadHEAT::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            /*public static BlockEntry<ThermobaricWarhead> THERMOBARIC_WARHEAD = REGISTRATE
                .block("thermobaric_warhead", ThermobaricWarhead::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();*/

            public static void register() {}
        }
        public static class Fuze {
            /*public static BlockEntry<ProximityFuze> PROXIMITY_FUZE = REGISTRATE
                .block("proximity_fuze", ProximityFuze::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();*/

            public static BlockEntry<ImpactFuze> IMPACT_FUSE = REGISTRATE
                .block("impact_fuze", ImpactFuze::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();


            public static void register() {}
        }
        public static class Attach {
            public static BlockEntry<Glider> GLIDER = REGISTRATE
                .block("glider", Glider::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            public static BlockEntry<GliderWing> GLIDER_WING = REGISTRATE
                .block("glider_wing", GliderWing::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();


            public static BlockEntry<RocketBooster> ROCKET_BOOSTER = REGISTRATE
                .block("rocket_booster", RocketBooster::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            public static BlockEntry<FloatingChamber> FLOATING_CHAMBER = REGISTRATE
                .block("floating_chamber", FloatingChamber::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();


            public static void register() {}
        }


        public static class Primer {
            public static BlockEntry<PrimerBlock> PRIMER = REGISTRATE
                .block("primer", PrimerBlock::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            public static BlockEntry<TorpedoPrimer> TORPEDO_PRIMER = REGISTRATE
                .block("torpedo_primer", TorpedoPrimer::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item(ItemPlus::getOrCreateFromBlockAndGet)
                .build()
                .register();

            public static void register() {}
        }





        /*public static BlockEntry<TestModifier> TEST_MODIFIER = REGISTRATE
            .block("test_modifier", TestModifier::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();

        public static BlockEntry<BlackBox> BLACK_BOX = REGISTRATE
            .block("black_box", BlackBox::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();*/
        /*public static BlockEntry<RocketBooster> ROCKET_BOOSTER = REGISTRATE
            .block("rocket_booster", RocketBooster::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();*/

        public static BlockEntry<TailFin> TAIL_FIN = REGISTRATE
            .block("tail_fin", TailFin::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item(ItemPlus::getOrCreateFromBlockAndGet)
            .build()
            .register();

        /*public static BlockEntry<ShellFrame> SHELL_FRAME = REGISTRATE
            .block("shell_frame", ShellFrame::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();*/

        /*public static BlockEntry<DenseShell> DENSE_SHELL = REGISTRATE
            .block("dense_shell", DenseShell::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item(BlockItemPlus::getOrCreateFrom)
            .build()
            .register();*/

        /*public static BlockEntry<WindCap> WIND_CAP = REGISTRATE
            .block("wind_cap", WindCap::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item(ItemPlus::getOrCreateFromBlockAndGet)
            .build()
            .register();*/

        /*public static BlockEntry<Rotator> ROTATOR = REGISTRATE
            .block("rotator", Rotator::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();*/




        public static void register() {
            Primer.register();
            Propellant.register();
            Warhead.register();
            Fuze.register();
            Attach.register();
        }
    }

    public static class Industrial {
        /*public static BlockEntry<ValkyrienAssemblerBlock> V_ASSEMBLE = REGISTRATE
            .block("v_assemble", ValkyrienAssemblerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();*/
        public static class Create {
            public static BlockEntry<CarbonFiberSail> CARBON_FIBER_SAIL = REGISTRATE
                .block("carbon_fiber_sail", CarbonFiberSail::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();

            public static void register() { }
        }

        public static class Projector {
            public static BlockEntry<ProjectionCenter> PROJECTION_CENTER =  REGISTRATE
                .block("projection_center", ProjectionCenter::new)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();

            public static BlockEntry<VSProjector> VS_PROJECTOR =  REGISTRATE
                .block("vs_projector", VSProjector::new)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();

            public static BlockEntry<ProjectorLen> PROJECT_LEN =  REGISTRATE
                .block("projector_len", ProjectorLen::new)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();

            public static void register() {}
        }

        public static class Machine {
            public static BlockEntry<Shredder> SHREDDER = REGISTRATE
                .block("shredder", Shredder::new)
                .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                .item()
                .build()
                .register();
        }

        public static BlockEntry<Dock> DOCK = REGISTRATE
            .block("dock", Dock::new)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .onRegister(CreateRegistrate.connectedTextures(DockCTBehaviour::new))
            //.item(ItemPlus::getOrCreateFromBlockAndGet)
            .item()
            .build()
            .register();

        public static BlockEntry<DockGreenPrintHolder> DOCK_GP_HOLDER = REGISTRATE
            .block("dock_green_print_holder", DockGreenPrintHolder::new)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .onRegister(CreateRegistrate.connectedTextures(DockCTBehaviour::new))
            //.item(ItemPlus::getOrCreateFromBlockAndGet)
            .item()
            .build()
            .register();

        public static BlockEntry<DockConstructionDetector> DOCK_CONSTRUCTION_DETECTOR = REGISTRATE
            .block("dock_construction_detector", DockConstructionDetector::new)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .onRegister(CreateRegistrate.connectedTextures(DockCTBehaviour::new))
            .item()
            .build()
            .register();

        public static BlockEntry<DockUnlocker> DOCK_UNLOCKER = REGISTRATE
            .block("dock_unlocker", DockUnlocker::new)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .onRegister(CreateRegistrate.connectedTextures(DockCTBehaviour::new))
            .item()
            .build()
            .register();

        /*public static BlockEntry<RobotArm> ROBOT_ARM = REGISTRATE
            .block("robot_arm", RobotArm::new)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item(ItemPlus::getOrCreateFromBlockAndGet)
            .build()
            .register();

        public static BlockEntry<UnderConstruction> UNDER_CONSTRUCTION = REGISTRATE
            .block("under_construction", UnderConstruction::new)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item(ItemPlus::getOrCreateFromBlockAndGet)
            .build()
            .register();*/





        public static void register() {
            Projector.register();
            Create.register();

        }
    }

    public static class Debug {
        /*public static BlockEntry<LostAndFoundBlock> LOST_AND_FOUND = REGISTRATE
            .block("lost_and_found", LostAndFoundBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
            .item()
            .build()
            .register();*/
        public static BlockEntry<Block> DIR_INDICATOR = REGISTRATE
            .block("dir_indicator", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();

        public static BlockEntry<Block01> BLOCK01 = REGISTRATE
            .block("block01", Block01::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .item()
            .build()
            .register();

        public static void register() {}
    }

    public static BlockEntry<MechScopeBlock> MECH_SCOPE_BLOCK = REGISTRATE
        .block("mech_scope_block", MechScopeBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .onRegister(AllMovementBehaviours.movementBehaviour(new ScopeMovementBehaviour()))
        .item()
        .build()
        .register();

    public static BlockEntry<TelescopicScope> TELE_SCOPE_BLOCK = REGISTRATE
        .block("tele_scope_block", TelescopicScope::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .onRegister(AllMovementBehaviours.movementBehaviour(new ScopeMovementBehaviour()))
        .item()
        .build()
        .register();

    /*public static BlockEntry<DelayedExplosiveBlock> SMALL_DELAYED_EXPLOSIVE_BLOCK = REGISTRATE
        .block("small_delayed_explosive_block", p -> new DelayedExplosiveBlock(p, 90, 3))
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();*/

    /*public static BlockEntry<C4ExplosiveBlock> C4Block = REGISTRATE
        .block(C4ExplosiveBlock.ID, C4ExplosiveBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();*/

    /*public static BlockEntry<DetonatorBlock> Detonator = REGISTRATE
        .block(DetonatorBlock.ID, DetonatorBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();

    public static BlockEntry<FragWarheadBlock> FRAG_WARHEAD_BLOCK = REGISTRATE
        .block(FragWarheadBlock.ID, FragWarheadBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();*/

    /*public static BlockEntry<FlareWarheadBlock> FLARE_WARHEAD = REGISTRATE
        .block("flare_warhead", FlareWarheadBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();*/

    /*public static BlockEntry<EmptySolidFuelBlock> EMPTY_SOLID_FUEL = REGISTRATE
        .block("empty_solid_fuel", EmptySolidFuelBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();

    public static BlockEntry<SolidFuelBlock> SOLID_FUEL = REGISTRATE
        .block("solid_fuel", SolidFuelBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();

    public static BlockEntry<EngineIgniterBlock> ENGINE_IGNITER = REGISTRATE
        .block("engine_igniter_block", EngineIgniterBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();

    public static BlockEntry<NozzleBlock> NOZZLE = REGISTRATE
        .block("nozzle_block", NozzleBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();

    public static BlockEntry<StabilizeFinsBlock> STABILIZE_FINS = REGISTRATE
        .block("stabilize_fins", StabilizeFinsBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();*/

    /*public static BlockEntry<ActivatorBlock> ACTIVATOR = REGISTRATE
        .block("activator", ActivatorBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
        .item()
        .build()
        .register();*/


    /*public static BlockEntry<SignalDetectorBlock> FIRE_DETECTOR_BLOCK = REGISTRATE
        .block("fire_detector_block", (p) -> new SignalDetectorBlock(KeyBinding.FireKey, p))
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.mapColor(MapColor.STONE))
        .item()
        .build()
        .register();*/

    /*public static BlockEntry<AmmoHolderBlock> AMMO_HOLDER = REGISTRATE
        .block("ammo_holder", AmmoHolderBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
        .item()
        .build()
        .register();*/

    /*public static BlockEntry<LocatorBlock> LOCATOR = REGISTRATE
        .block("locator", LocatorBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p.mapColor(MapColor.STONE))  //todo friction
        .item()
        .build()
        .register();*/



    public static void register() {
        Artillery.register();
        Cartridge.register();

        Industrial.register();

        //Debug.register();
    }

/*
    var10000 = (BlockBuilder)Create.REGISTRATE.block("mechanical_saw", SawBlock::new)
        .initialProperties(SharedProperties::stone)
    .addLayer(() -> RenderType::cutoutMipped)
        .properties((p) -> p.mapColor(MapColor.PODZOL))
        .transform(TagGen.axeOrPickaxe());
    SawGenerator var43 = new SawGenerator();
    MECHANICAL_SAW = ((BlockBuilder)((BlockBuilder)((BlockBuilder)var10000
        .blockstate(var43::generate)
        .transform(BlockStressDefaults.setImpact((double)4.0F)))
        .onRegister(AllMovementBehaviours.movementBehaviour(new SawMovementBehaviour()))).addLayer(() -> RenderType::cutoutMipped).item().tag(new TagKey[]{
        AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();

 */
    /*public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static RegistryObject<Block> ScopeBlockRO = BLOCKS.register(
        "scope_block", () -> {
            //movementBehaviour(new ScopeMovementBehaviour());
            return new ScopeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
        }
    );*/
    //public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    /*public static void forEach(Consumer<RegistryObject<Block>> fun) {
        if (fun == null) return;
        fun.accept(FireSignalDetectorBlock);
        fun.accept(MoveForwardSignalDetectorBlock);
        fun.accept(MoveBackwardSignalDetectorBlock);
        fun.accept(MoveLeftSignalDetectorBlock);
        fun.accept(MoveRightSignalDetectorBlock);
    }


    public static final RegistryObject<Block> FireSignalDetectorBlock = BLOCKS.register(
        "fire_signal_detector",
        () -> new SignalDetectorBlock(
            KeyBinding.FireKey,
            AllBlockEntites.FireSignalDetectorBlockEntity,
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
        )
    );
    public static final RegistryObject<Block> MoveForwardSignalDetectorBlock = BLOCKS.register(
        "move_forward_signal_detector",
        () -> new SignalDetectorBlock(
            KeyBinding.MoveForwardKey,
            AllBlockEntites.MoveForwardSignalDetectorBlockEntity,
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
        )
    );
    public static final RegistryObject<Block> MoveBackwardSignalDetectorBlock = BLOCKS.register(
        "move_backward_signal_detector",
        () -> new SignalDetectorBlock(
            KeyBinding.MoveBackwardKey,
            AllBlockEntites.MoveBackwardSignalDetectorBlockEntity,
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
        )
    );
    public static final RegistryObject<Block> MoveLeftSignalDetectorBlock = BLOCKS.register(
        "move_left_signal_detector",
        () -> new SignalDetectorBlock(
            KeyBinding.MoveLeftKey,
            AllBlockEntites.MoveLeftSignalDetectorBlockEntity,
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
        )
    );
    public static final RegistryObject<Block> MoveRightSignalDetectorBlock = BLOCKS.register(
        "move_right_signal_detector",
        () -> new SignalDetectorBlock(
            KeyBinding.MoveRightKey,
            AllBlockEntites.MoveRightSignalDetectorBlockEntity,
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
        )
    );
    public static RegistryObject<Block> TestBlock = BLOCKS.register(
        "test_block",
        () -> new TestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
    );
    public static RegistryObject<Block> TestBootBlock = BLOCKS.register(
        "test_boot_block",
        () -> new TestBootBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
    );

    public static RegistryObject<Block> SolidAirBlock = BLOCKS.register(
        "solid_air_block", SolidAirBlock::new
    );*/
    /**/
    /*public static BlockEntry<ScopeBlock> ScopeBlock = REGISTRATE
        .block("scope_block", ScopeBlock::new)
        .initialProperties(SharedProperties::stone)
        .blockstate(BlockStateGen.directionalBlockProvider(true))
        .onRegister(movementBehaviour(new ScopeMovementBehaviour()))
        .register();*/
    /*public static final BlockEntry<TestBlock> TestBlock =
        REGISTRATE
            .block("test_block", TestBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(TagGen.pickaxeOnly())
            .item()
            .build().register();

     */
}
