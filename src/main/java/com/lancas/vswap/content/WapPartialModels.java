package com.lancas.vswap.content;


import com.jozufozu.flywheel.core.PartialModel;
import com.lancas.vswap.ModMain;

public class WapPartialModels {
    public static final PartialModel
        //PROJECTOR_LEN_BASE = block("industry/projector_len_base"),
        PROJECTOR_LEN_TOP = block("industry/projector/projector_len_top"),
        SHREDDER_BLADE = block("industry/shredder/shredder_blade"),
        DOCK_PLATE_EDGE = block("industry/dock/dock_plate_edge"),
        DOCK_PLATE_CORNER = block("industry/dock/dock_plate_corner"),
        DOCK_PLATE_TEST = block("industry/dock/dock_plate_test");

    private static PartialModel block(String path) {
        return new PartialModel(ModMain.asRes("block/" + path));
    }

    public static void init() {}
}