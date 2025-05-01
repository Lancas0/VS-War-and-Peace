package com.lancas.vs_wap.content.block.blocks.rocket;

import com.lancas.vs_wap.content.block.blocks.abstrac.DirectionalBlockImpl;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.ServerShip;

public class StabilizeFinsBlock extends DirectionalBlockImpl {
    public StabilizeFinsBlock(Properties p_49795_) {
        super(p_49795_);
    }

    //private Dictionary<BlockPos, String> blockPos2Guid = new Hashtable<>();  //todo saved, todo concurrent?


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        EzDebug.log("fin stabilize block placed");

        if (level.isClientSide) return;

        ServerShip ship = ShipUtil.getServerShipAt((ServerLevel)level, pos);
        if (ship == null) return;

        //debug
        /*if (blockPos2Guid.get(pos) != null) EzDebug.fatal("already has a guid on pos:" + pos);
        String guid = Guid.GUID.newGuid().toGuidString();

        blockPos2Guid.put(pos, guid);*/
        /*BallisticsController.getOrCreate(ship).addTailFinControl(
            pos.toShortString(),
            new TailFinControl(
                JomlUtil.dCenter(pos),
                JomlUtil.dNormal(state.getValue(FACING)),
                0.01,
                1
            )
        );*/

        //FinStabilizer.getOrCreate(ship).finCount++;
        /*BallisticsController.getOrCreate(ship).addSymmetricFins(
            JomlUtil.dCenter(pos),
            JomlUtil.dNormal(state.getValue(FACING)),
            JomlUtil.dCenter(pos).add(0, 1, 0),
            new Vector3d(1, 0, 0),
            4,
            0.05,
            0.02
        );*/
    }

    @Override
    public Direction getDirectionForPlacement(BlockPlaceContext ctx) {
        return ctx.getNearestLookingDirection();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide) return;
        ServerShip ship = ShipUtil.getServerShipAt((ServerLevel)level, pos);

        if (ship == null) return;

        /*String guid = blockPos2Guid.get(pos);
        if (guid == null) {
            EzDebug.fatal("fail to get guid at pos:" + pos);
            return;
        }
        blockPos2Guid.remove(pos);
        BallisticsController.getOrCreate(ship).removeSubControl(guid);*/
        //BallisticsController.getOrCreate(ship).removeTailFinControlAt(pos.toShortString());

    }
}
