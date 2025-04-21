package com.lancas.vs_wap.content.blocks.cartridge.modifier;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;

import java.util.List;

public class TestModifier extends BlockPlus implements IModifier {
    private static List<IBlockAdder> providers = List.of(
        new DefaultCartridgeAdder()
    );
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return providers;
    }

    public TestModifier(Properties p_49795_) {
        super(p_49795_);
    }


    /*@Override
    public Vector3dc calculateForceInServerTick(ModifierData data) {
        return null;
        //if (!data.isOutArtillery)
        //    return new Vector3d();


        /*BlockPos worldBp = data.getWorldBp();
        if (data.level.getBlockState(worldBp).getBlock().) {

        }*./


        //Vector3d antiGravity = new Vector3d(0, 8, 0).mul(data.projectileShip.getInertiaData().getMass());
        //EzDebug.Log("Anti Gravity:" + StringUtil.toNormalString(antiGravity));
        //return antiGravity;
    }

    @Override
    public Vector3dc calculateTorqueInServerTick(ModifierData data) {
        Vector3dc angularVelocity = data.projectileShip.getOmega();
        Vector3d desiredTorque = angularVelocity.mul(20, new Vector3d()).mul(data.projectileShip.getInertiaData().getMomentOfInertiaTensor()); // 阻尼旋转
        return desiredTorque;
    }*/
}
