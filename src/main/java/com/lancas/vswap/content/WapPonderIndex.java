package com.lancas.vswap.content;

import com.lancas.vswap.VsWap;
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
            WapBlocks.Artillery.EJECTING_BREECH
        })
            .addStoryBoard(WapPonderScenes.Artillery.AssemblyId, WapPonderScenes.Artillery::assembly)
            .addStoryBoard(WapPonderScenes.Artillery.DockerLoadingId, WapPonderScenes.Artillery::dockerLoading)
            .addStoryBoard(WapPonderScenes.Artillery.ArmLoadingId, WapPonderScenes.Artillery::armLoading)
            .addStoryBoard(WapPonderScenes.Artillery.PhysLoadingId, WapPonderScenes.Artillery::physLoad);

        HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.Cartridge.Primer.PRIMER,
            WapBlocks.Cartridge.Primer.TORPEDO_PRIMER,
            WapBlocks.Cartridge.Attach.ROCKET_BOOSTER
        })
            .addStoryBoard(WapPonderScenes.Munition.PrimerGeneralId, WapPonderScenes.Munition::primerGeneral)
            .addStoryBoard(WapPonderScenes.Munition.MunitionStructureId, WapPonderScenes.Munition::munitionStructure)
            .addStoryBoard(WapPonderScenes.Munition.MunitionApId, WapPonderScenes.Munition::munitionAp)
            .addStoryBoard(WapPonderScenes.Munition.MunitionGliderApId, WapPonderScenes.Munition::munitionGlider)
            .addStoryBoard(WapPonderScenes.Munition.MunitionTailFindId, WapPonderScenes.Munition::munitionTailFin);

        /*HELPER.forComponents(new ItemProviderEntry[]{
            WapBlocks.MECH_SCOPE_BLOCK
        })
            .addStoryBoard(WapPonderScenes.Artillery.TEST_ID, WapPonderScenes.Artillery::test);*/
    }
}
