package com.lancas.vswap.content.block.blocks.cartridge.booster;

import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;

import java.util.List;

public abstract class AbstractBooster extends BlockPlus implements ISandBoxBallisticBlock {


    protected AbstractBooster(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void physTick(SandBoxServerShip ship, BallisticData ballisticData) {

    }
}
