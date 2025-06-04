package com.lancas.vswap.content;

import com.lancas.vswap.VsWap;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;

public class WapCT {

    public static CTSpriteShiftEntry DOCK_TOP = CTSpriteShifter.getCT(
        AllCTTypes.RECTANGLE,
        VsWap.asRes("block/create/dock/dock_top"),
        VsWap.asRes("block/create/dock/dock_top_connected")
    );


    public static void register() {}
}
