package com.lancas.vs_wap.content.item.entity;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DockerItemEntity extends ItemEntity {
    public DockerItemEntity(Level level, double x, double y, double z, ItemStack stack, double dvx, double dvy, double dvz) {
        super(level, x, y, z, stack, dvx, dvy, dvz);
    }

    @Override
    public float getSpin(float p_32009_) {
        return 0;
    }
}
