package com.lancas.vswap.sandbox.industry;

import com.lancas.vswap.content.block.blocks.industry.dock.DockBe;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.ServerOnlyBehaviour;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.WorldUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3d;
import org.joml.primitives.AABBdc;

public class MountToDockBehaviour extends ServerOnlyBehaviour<MountToDockData> {
    @Override
    protected MountToDockData makeInitialData() { return new MountToDockData(); }

    @Override
    public void serverTick(ServerLevel level) {
        if (level.getBlockEntity(data.dockBp) instanceof DockBe be) {
            if (be.isController()) {

                IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();
                IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();

                AABBdc localAABB = ship.getBlockCluster().getDataReader().getLocalAABB();
                if (!localAABB.isValid()) {
                    //empty aabb, set position instead
                    Vector3d setPos = WorldUtil.getWorldCenter(level, data.dockBp);
                    rigidWriter.setPosition(setPos);
                    //EzDebug.log("set ship pos to " + StrUtil.F2(setPos));
                    return;
                }

                Vector3d localBottom = JomlUtil.dFaceCenter(localAABB, Direction.DOWN);
                rigidWriter.moveLocalPosToWorld(localBottom, WorldUtil.getWorldCenter(level, data.dockBp).add(0, 0.5, 0));
                return;
            }
        }

        EzDebug.highlight("ship removed because of not valid Dock, make sure docker item is thrown.");
        SandBoxServerWorld.getOrCreate(level).markShipDeleted(ship.getUuid());
    }

    @Override
    public Class<?> getDataType() { return MountToDockData.class; }
}
