package com.lancas.vswap.content.block.blocks.cartridge.propellant;

import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

//import static com.lancas.vswap.content.WapBlocks.Cartridge.Propellant.Empty.EMPTY_GAUGE12;

public class Gauge12Block extends BlockPlus implements IPropellant {
    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            Gauge12Block.class,

            () -> List.of(new DefaultCartridgeAdder(true))
        );
    }

    public Gauge12Block(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    public double getSPE(BlockState state) { return WapBlockInfos.StdPropellantEnergy.valueOrDefaultOf(state); }
    @Override
    public boolean isEmpty(BlockState state) { return false; }

    @Override
    public BlockState getEmptyState(BlockState state) {
        Direction dir = state.getValue(DirectionAdder.FACING);
        throw new NotImplementedException("todo return empty gauge12");
        //return EMPTY_GAUGE12.getDefaultState().setValue(DirectionAdder.FACING, dir);
        /*ServerShip onShip = ShipUtil.getServerShipAt(level, pos);
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
        level.setBlock(pos, EMPTY_GAUGE12.getDefaultState().setValue(DirectionAdder.FACING, dir), Block.UPDATE_ALL);*/
        //todo spawn particle on set empty

    }
}