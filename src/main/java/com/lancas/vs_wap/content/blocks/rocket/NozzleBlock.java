package com.lancas.vs_wap.content.blocks.rocket;

import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3d;

public class NozzleBlock extends Block implements INozzleBlock {
    public NozzleBlock(Properties props) {
        super(props);
    }

    @Override
    public Vector3d getPower(Direction dir) {
        return JomlUtil.d(dir.getOpposite().getNormal()).mul(20000);
    }
}