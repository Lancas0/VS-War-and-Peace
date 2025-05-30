package com.lancas.vswap.content.block.blockentity;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.foundation.network.server2client.CreateOutlinePacketS2C;
import com.lancas.vswap.subproject.lostandfound.content.LostAndFoundBehaviour;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class LostAndFoundBe extends SmartBlockEntity {
    public LostAndFoundBe(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //private LostAndFoundBehaviour lostAndFoundBeh;// = new LostAndFoundBehaviour(this);

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {
        //lostAndFoundBeh = new LostAndFoundBehaviour(this);
        //list.add(lostAndFoundBeh);
    }

    @Override
    public void tick() {
        super.tick();
        /*BlockPos bp = lostAndFoundBeh.currentBlockPos();
        //EzDebug.warn("get bp:" + bp);
        if (bp != null) {
            NetworkHandler.sendToAllPlayers(new CreateOutlinePacketS2C("lost_and_found_test", JomlUtil.boundBlock(bp)));
        }*/
    }

    public void destroy() {
        super.destroy();
        //EzDebug.warn("Lost and Found be: destory is invoke!");
    }
}
