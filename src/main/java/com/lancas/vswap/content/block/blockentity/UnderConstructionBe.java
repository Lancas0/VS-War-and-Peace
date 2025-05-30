package com.lancas.vswap.content.block.blockentity;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.sandbox.industry.ConstructingShipBehaviour;
import com.lancas.vswap.sandbox.industry.ConstructingShipData;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.StrUtil;
import com.lancas.vswap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.primitives.AABBdc;

import java.util.UUID;

//todo green print can be extract from item port
public class UnderConstructionBe extends BlockEntity {
    public UnderConstructionBe(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    private UUID saShipUuid = null;

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putUUID("uuid", saShipUuid);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        saShipUuid = tag.getUUID("uuid");
    }

    public void initializeConstructingShip(ServerLevel level, RRWChunkyShipSchemeData schemeData) {
        if (saShipUuid != null) {
            EzDebug.warn("there is already a constructing ship, will not create another one.");
            return;
        }
        if (schemeData.isEmpty()) {
            EzDebug.warn("the schemeData should not be empty!");
            return;
        }

        Vector3d worldCenter = WorldUtil.getWorldCenter(level, worldPosition);

        saShipUuid = UUID.randomUUID();
        RigidbodyData rigidbody = new RigidbodyData();
        rigidbody.setPosition(worldCenter.add(0, 0.5, 0)).setStatic(true).setScale(schemeData.getScale());
        BlockClusterData blockData = new BlockClusterData();
        //blockData.setBlock(new Vector3i(), Blocks.IRON_BLOCK.defaultBlockState());
        SandBoxServerShip ship = new SandBoxServerShip(saShipUuid, rigidbody, blockData);

        ConstructingShipBehaviour constructingBeh = new ConstructingShipBehaviour();
        ConstructingShipData constructingData = new ConstructingShipData(schemeData, true);
        ship.addBehaviour(constructingBeh, constructingData);

        SandBoxServerWorld.addShipAndSyncClient(level, ship);
        EzDebug.highlight("add constructing ship:" + saShipUuid);

        EzDebug.highlight("set saShipUUID:" + saShipUuid);
    }
    public void onRemove(ServerLevel level) {
        if (saShipUuid == null) {
            EzDebug.warn("UnderConstruction Be onRemove:saShipUuid is null");
            return;
        }

        SandBoxServerWorld.getOrCreate(level).markShipDeleted(saShipUuid);
    }
    public void tick() {
        //EzDebug.log("UnderConstruction Be tick, saUuid:" + saShipUuid);

        if (saShipUuid == null || !(level instanceof ServerLevel sLevel)) return;

        SandBoxServerShip saShip = SandBoxServerWorld.getOrCreate(sLevel).getServerShip(saShipUuid);
        if (saShip == null) {
            EzDebug.warn("uuid is not null but failed to find constructing ship");
            return;
        }
        IRigidbodyDataReader rigidReader = saShip.getRigidbody().getDataReader();
        IRigidbodyDataWriter rigidWriter = saShip.getRigidbody().getDataWriter();

        AABBdc localAABB = saShip.getBlockCluster().getDataReader().getLocalAABB();
        if (!localAABB.isValid()) {
            //empty aabb, set position instead
            Vector3d setPos = WorldUtil.getWorldCenter(sLevel, worldPosition);
            rigidWriter.setPosition(setPos);
            //EzDebug.log("set ship pos to " + StrUtil.F2(setPos));
            return;
        }

        Vector3d localBottom = JomlUtil.dFaceCenter(localAABB, Direction.DOWN);
        rigidWriter.moveLocalPosToWorld(localBottom, WorldUtil.getWorldCenter(sLevel, worldPosition).add(0, 0.5, 0));
        //rigidWriter.setPosition(WorldUtil.getWorldCenter(sLevel, worldPosition).add(0, 1, 0));
    }

    public @Nullable ConstructingShipBehaviour getConstructingBehaviour() {
        if (saShipUuid == null || !(level instanceof ServerLevel sLevel))
            return null;

        SandBoxServerShip saShip = SandBoxServerWorld.getOrCreate(sLevel).getServerShip(saShipUuid);
        if (saShip == null)
            return null;

        return saShip.getBehaviour(ConstructingShipBehaviour.class);
    }
}
