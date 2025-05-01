package com.lancas.vs_wap.content.block.blocks.cartridge.warhead;

import com.lancas.vs_wap.content.block.blocks.abstrac.DirectionalBlockImpl;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.foundation.network.server2client.FlarePackageS2C;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.valkyrienskies.core.api.ships.ServerShip;

public class FlareWarheadBlock extends DirectionalBlockImpl implements IWarheadBlock {
    //todo cone shape effect zone
    public static final int EFFECT_RANG = 20;  //todo larger and configurable
    public static final int EFFECT_TICKS = 100;  //todo longer and configurable
    public static final float GAMMA = 6f;  //500% brighter

    public FlareWarheadBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void onDestroyByExplosion(ServerLevel level, BlockPos pos, BlockState state, Explosion explosion) {
        Vec3 center;

        ServerShip onShip = ShipUtil.getServerShipAt(level, pos);
        if (onShip != null) {
            center = JomlUtil.v3(onShip.getShipToWorld().transformPosition(JomlUtil.dCenter(pos)));
        } else {
            center = pos.getCenter();
        }
        //todo a or some flare particle
        NetworkHandler.channel.send(
            PacketDistributor.ALL.with(() -> { return null; }), //send to all players and on client the distance will be calculated to avoid all effected, todo check dynamic
            new FlarePackageS2C(EFFECT_RANG, center, EFFECT_TICKS, GAMMA)
        );
    }
}
