package com.lancas.vswap.content.block.blocks.cartridge.ticker;

import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;

public class BlackBox extends BlockPlus implements ITicker {
    public static List<IBlockAdder> providers = List.of(
        new DefaultCartridgeAdder()
    );
    @Override
    public List<IBlockAdder> getAdders() { return providers; }

    public BlackBox(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    public void serverTicker(BlockState state, BlockPos bp, ServerLevel level, ServerShip projectileShip) {
        EzDebug.log("current speed:" + StrUtil.toNormalString(projectileShip.getVelocity()));
    }
    @Override
    public void physicTicker(PhysShip physShip) {

    }
}
