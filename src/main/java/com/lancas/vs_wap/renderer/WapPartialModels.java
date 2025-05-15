package com.lancas.vs_wap.renderer;


import com.jozufozu.flywheel.core.PartialModel;
import com.lancas.vs_wap.ModMain;

public class WapPartialModels {
    public static final PartialModel
        //PROJECTOR_LEN_BASE = block("industry/projector_len_base"),
        PROJECTOR_LEN_TOP = block("industry/projector/projector_len_top");

    private static PartialModel block(String path) {
        return new PartialModel(ModMain.getResLocation("block/" + path));
    }

    public static void init() {}
}