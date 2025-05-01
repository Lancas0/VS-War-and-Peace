package com.lancas.vs_wap.content.block.blocks.cartridge.propellant;

import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.foundation.network.server2client.ConeParticlePacketS2C;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;

import static com.lancas.vs_wap.content.WapBlocks.Cartridge.Propellant.Empty.EMPTY_GAUGE12;

public class Gauge12Block extends BlockPlus implements IPropellant {
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            Gauge12Block.class,

            () -> List.of(new DefaultCartridgeAdder())
        );
    }

    public Gauge12Block(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    public double getEnergy(BlockState state) { return WapBlockInfos.propellant_power.valueOrDefaultOf(state); }
    @Override
    public boolean isEmpty(BlockState state) { return false; }

    @Override
    public void setAsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        ServerShip onShip = ShipUtil.getServerShipAt(level, pos);
        if (onShip == null) {
            EzDebug.error("gauge should be on ship");
            return;
        }

        Vector3d gaugeDirInShip = JomlUtil.dNormal(DirectionAdder.getDirection(state));

        //瓦尔基里已经兼容了shipyard里的粒子，直接传入shipyard里的方向即可
        NetworkHandler.channel.send(
            PacketDistributor.ALL.with(() -> { return null; }), //todo only send to near players
            new ConeParticlePacketS2C(pos, gaugeDirInShip.get(new Vector3f()), 45, 50)
        );

        Direction dir = state.getValue(DirectionAdder.FACING);
        level.setBlock(pos, EMPTY_GAUGE12.getDefaultState().setValue(DirectionAdder.FACING, dir), Block.UPDATE_ALL);
    }
}