package com.lancas.vs_wap.content.block.blocks.cartridge.ticker;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.StrUtil;
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
    public Iterable<IBlockAdder> getAdders() { return providers; }

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
