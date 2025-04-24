package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3d;

public class SandBoxRigidbodyData implements IComponentData<SandBoxRigidbodyData>, IExposedRigidbodyData {
    public double mass = 0;
    public final Vector3d localPosMassMul = new Vector3d();  //todo may exceed limit


    @Override
    public SandBoxRigidbodyData copyData(SandBoxRigidbodyData src) {
        return this;
    }
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putNumber("mass", mass)
            .putVector3("local_pos_mass_mul", localPosMassMul)
            .get();
    }
    @Override
    public IComponentData<SandBoxRigidbodyData> load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readDoubleDo("mass", v -> mass = v)
            .readVector3d("local_pos_mass_mul", localPosMassMul);
        return this;
    }


    @Override
    public double getMass() { return mass; }

    /*@Override
    public Vector3d getLocalMassCenter(Vector3d dest) {
        return localPosMassMultip.div(mass, dest);
    }*/
}
