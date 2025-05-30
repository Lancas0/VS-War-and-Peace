package com.lancas.vswap.content.block.blocks.cartridge.modifier;

import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;

import java.util.List;

public class RocketBooster extends BlockPlus implements IModifier/*, IBE<RocketBoosterBE>*/ {
    public static double BOOST_POWER = 4E5;

    public static List<IBlockAdder> providers = List.of(
        new DefaultCartridgeAdder()
    );
    @Override
    public List<IBlockAdder> getAdders() { return providers; }

    public RocketBooster(Properties p_49795_) {
        super(p_49795_);
    }


    /*@Override
    public Vector3dc calculateForceInServerTick(ModifierData data) {
        if (!data.isOutArtillery) return new Vector3d();

        if (!(data.getBlockEntity() instanceof RocketBoosterBE be)) {
            EzDebug.fatal("should have rocket booster be");
            return new Vector3d();
        }

        return switch (be.tickNext()) {
            case Ended, Ready -> new Vector3d();
            case Boost -> data.projectileShip.getVelocity().normalize(BOOST_POWER, new Vector3d());//data.getLaunchDir().normalize(BOOST_POWER, new Vector3d());
        };
    }*/


    /*@Override
    public Class<RocketBoosterBE> getBlockEntityClass() { return RocketBoosterBE.class; }
    @Override
    public BlockEntityType<? extends RocketBoosterBE> getBlockEntityType() {
        return EinherjarBlockEntites.ROCKET_BOOSTER_BE.get();
    }*/
}
