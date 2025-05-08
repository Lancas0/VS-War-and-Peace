package com.lancas.vs_wap.content.behaviour.item;

import com.lancas.vs_wap.content.item.items.docker.IDocker;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class DockerDispenseBehaviour implements DispenseItemBehavior {
    @Override
    public ItemStack dispense(BlockSource source, ItemStack stack) {
        EzDebug.log("docker dispense beh is called");

        if (source.getLevel().isClientSide) return stack;  // 只在服务端执行
        if (!(stack.getItem() instanceof IDocker docker)) return stack;

        if (!docker.hasShipData(stack)) return stack;

        //发射器信息
        /*Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos targetPos = source.getPos().relative(direction);
        ServerLevel level = source.getLevel();*/

        boolean success = dispenseHandle(source, docker.getShipData(stack));

        if (success)
            stack.shrink(1);

        return stack;
    }

    private boolean dispenseHandle(BlockSource source, IShipSchemeData shipSchemeData) {
        //todo use holdable
        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos targetPos = source.getPos().relative(direction);
        ServerLevel level = source.getLevel();

        ServerShip parentShip = ShipUtil.getServerShipAt(level, source.getPos());

        Vector3d worldForward =
            parentShip == null ?
                JomlUtil.dNormal(direction) :
                parentShip.getShipToWorld().transformDirection(JomlUtil.dNormal(direction)).normalize();

        Vector3d worldTarget =
            parentShip == null ?
                JomlUtil.dCenter(targetPos) :
                parentShip.getShipToWorld().transformPosition(JomlUtil.dCenter(targetPos));
        worldTarget.add(worldForward.mul(0.5f, new Vector3d()));  //move the target a little more forward to avoid intersect with parent ship

        //朝向z轴正方向为默认方向
        ServerShip newShip = shipSchemeData.createShip(level);
        ShipBuilder sb = ShipBuilder.modify(level, newShip)
            .rotateForwardTo(worldForward)
            .moveFaceTo(Direction.NORTH, worldTarget)  //move the xy backward face to target pos
            .cancelIf(ship -> {
                var iter = VSGameUtilsKt.getShipsIntersecting(level, ship.getWorldAABB());
                boolean anyIntersect = false;
                for (Ship intersected : iter) {
                    if (intersected.getId() != ship.getId()) { //only detect different ship intersect
                        anyIntersect = true;
                        EzDebug.log("intersect with " + intersected.getSlug() + ": [" + intersected.getId() + "]");
                        break;
                    }
                }
                return anyIntersect;  //cancel if intersect with any ship
            });
            //.get();



        ServerShip ship = sb.get();
        return ship != null;  //success when ship is not null
    }
}
