package com.lancas.vswap.content;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.ui.ponder.WapPonderScenes;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

public class WapPonderIndex {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(VsWap.MODID);

    public static void register() {  //最好在模组主类内注册，不这样Ponder有时会找不到
        HELPER.forComponents(new ItemProviderEntry[] {WapBlocks.Industrial.Create.CARBON_FIBER_SAIL}).addStoryBoard(WapPonderScenes.CARBON_FIBER_SAIL_ID, WapPonderScenes::carbonFiberSail);

        HELPER.forComponents(new ItemProviderEntry[] {
            WapBlocks.Industrial.Projector.VS_PROJECTOR, WapBlocks.Industrial.Projector.PROJECT_LEN,
            WapItems.GREEN_PRINT
        })
            .addStoryBoard(WapPonderScenes.Projector.GP_PROJECT_BASIC_ID, WapPonderScenes.Projector::gpProjectorBasic)
            .addStoryBoard(WapPonderScenes.Projector.EXTRACT_AND_INSERT_GP_ID, WapPonderScenes.Projector::extractAndInsertGp)
            .addStoryBoard(WapPonderScenes.Projector.GP_USE_ON_SHIP_ID, WapPonderScenes.Projector::gpUseOnShip)
            .addStoryBoard(WapPonderScenes.Projector.PROJECTOR_ROTATION_ID, WapPonderScenes.Projector::projectorRotation);

        HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.Industrial.Machine.SHREDDER
        })
            .addStoryBoard(WapPonderScenes.ShredderAndMs.SHREDDER_USAGE_ID, WapPonderScenes.ShredderAndMs::shredderUsage)
            .addStoryBoard(WapPonderScenes.ShredderAndMs.SHREDDER_BREAKING_ID, WapPonderScenes.ShredderAndMs::shredderBreaking)
            .addStoryBoard(WapPonderScenes.ShredderAndMs.IntegrationId, WapPonderScenes.ShredderAndMs::integration);

        HELPER.forComponents(new ItemProviderEntry[]{
            WapItems.MATERIAL_STANDARDIZED
        })
            .addStoryBoard(WapPonderScenes.ShredderAndMs.IntegrationId, WapPonderScenes.ShredderAndMs::integration);


        HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.Artillery.EJECTING_BREECH,
            WapBlocks.Artillery.ARTILLERY_BARREL
        })
            .addStoryBoard(WapPonderScenes.Artillery.AssemblyId, WapPonderScenes.Artillery::assembly)
            .addStoryBoard(WapPonderScenes.Artillery.DockerLoadingId, WapPonderScenes.Artillery::dockerLoading)
            .addStoryBoard(WapPonderScenes.Artillery.ArmLoadingId, WapPonderScenes.Artillery::armLoading)
            .addStoryBoard(WapPonderScenes.Artillery.PhysLoadingId, WapPonderScenes.Artillery::physLoad);

        HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.Cartridge.Primer.PRIMER,
            WapBlocks.Cartridge.Primer.TORPEDO_PRIMER,
            WapBlocks.Cartridge.Attach.ROCKET_BOOSTER,

        })
            .addStoryBoard(WapPonderScenes.Munition.PrimerGeneralId, WapPonderScenes.Munition::primerGeneral);

        HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.Cartridge.Primer.PRIMER,
            WapBlocks.Cartridge.Primer.TORPEDO_PRIMER,
            WapBlocks.Cartridge.Attach.ROCKET_BOOSTER,
            WapBlocks.Cartridge.Attach.FLOATING_CHAMBER,
                WapBlocks.Cartridge.Attach.GLIDER,
                WapBlocks.Cartridge.Warhead.HE_WARHEAD,
                WapBlocks.Cartridge.Warhead.WARHEAD_APCR,
                WapBlocks.Cartridge.Warhead.WARHEAD_APDS,
                WapBlocks.Cartridge.Warhead.WARHEAD_HEAT,
                WapBlocks.Cartridge.Propellant.SHELLED_PROPELLANT,
                WapBlocks.Cartridge.Propellant.COMBUSTIBLE_PROPELLANT,
                WapBlocks.Cartridge.Fuze.IMPACT_FUSE,

                WapBlocks.Cartridge.TAIL_FIN,
        })
            //.addStoryBoard(WapPonderScenes.Munition.PrimerGeneralId, WapPonderScenes.Munition::primerGeneral);
            .addStoryBoard(WapPonderScenes.Munition.MunitionStructureId, WapPonderScenes.Munition::munitionStructure);
            //.addStoryBoard(WapPonderScenes.Munition.MunitionApId, WapPonderScenes.Munition::munitionAp)
            //.addStoryBoard(WapPonderScenes.Munition.MunitionGliderApId, WapPonderScenes.Munition::munitionGlider)
            //.addStoryBoard(WapPonderScenes.Munition.MunitionTailFindId, WapPonderScenes.Munition::munitionTailFin);

        HELPER.forComponents(new ItemProviderEntry[]{
                WapBlocks.Cartridge.Warhead.WARHEAD_APCR,
                WapBlocks.Cartridge.Warhead.WARHEAD_APDS,
        })
            .addStoryBoard(WapPonderScenes.Munition.MunitionApId, WapPonderScenes.Munition::munitionAp);

        HELPER.forComponents(new ItemProviderEntry[]{
                WapBlocks.Cartridge.TAIL_FIN,
            })
            .addStoryBoard(WapPonderScenes.Munition.MunitionTailFindId, WapPonderScenes.Munition::munitionTailFin);

        HELPER.forComponents(new ItemProviderEntry[]{
                WapBlocks.Cartridge.Attach.GLIDER,
            })
            .addStoryBoard(WapPonderScenes.Munition.MunitionGliderApId, WapPonderScenes.Munition::munitionGlider);

        HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.Industrial.DOCK, WapBlocks.Industrial.DOCK_GP_HOLDER,
            WapBlocks.Industrial.DOCK_UNLOCKER, WapBlocks.Industrial.DOCK_CONSTRUCTION_DETECTOR
        })
            .addStoryBoard(WapPonderScenes.Dock.DockUsageId, WapPonderScenes.Dock::dockUsage)
            .addStoryBoard(WapPonderScenes.Dock.DockConstraintId, WapPonderScenes.Dock::dockConstraint)
            .addStoryBoard(WapPonderScenes.Dock.DockConstructId, WapPonderScenes.Dock::dockConstruction)
            .addStoryBoard(WapPonderScenes.Dock.DockDisplayId, WapPonderScenes.Dock::dockDisplay);
        /*HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.MECH_SCOPE_BLOCK
        })
            .addStoryBoard(WapPonderScenes.Artillery.TEST_ID, WapPonderScenes.Artillery::test);*/

        HELPER.forComponents(new ItemProviderEntry[]{
            WapItems.DOCKER
        }).addStoryBoard(WapPonderScenes.DockerScene.DockUsageId, WapPonderScenes.DockerScene::dockerUsage);
    }
}
