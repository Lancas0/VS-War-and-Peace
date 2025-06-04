package com.lancas.vswap.content.block.blocks.misc;

import com.lancas.vswap.content.WapMass;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;
import org.valkyrienskies.mod.common.BlockStateInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

public class CompressAirChamber extends BlockPlus implements ISandBoxBallisticBlock {
    public static enum CompressLevel {

    }


    protected CompressAirChamber(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return List.of();
    }

    @Override
    public void serverTick(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos) {
        BlockPos shipWorldBp = JomlUtil.bpContaining(ship.getRigidbody().getDataReader().getPosition());
        if (!level.getFluidState(shipWorldBp).isEmpty()) {  //todo terminate or effect when meet high temp fluid?

        }
    }


}
