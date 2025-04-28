package com.lancas.vs_wap.ship.attachment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.WapBlocks;
import com.lancas.vs_wap.content.blocks.industry.ProjectCenter;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.data.SavedBlockPos;
import com.lancas.vs_wap.ship.helper.LazyShip;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.Objects;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectingShipAtt {

    @NotNull
    public static ProjectingShipAtt getOrCreate(@NotNull ServerShip ship, BlockPos inProjectorBp) {
        ProjectingShipAtt att = getFrom(ship);
        if (att == null) {
            att = new ProjectingShipAtt(ship, inProjectorBp);
            ship.saveAttachment(ProjectingShipAtt.class, att);
        }
        return att;
    }
    @Nullable
    public static ProjectingShipAtt getFrom(@Nullable ServerShip ship) {
        if (ship == null) return null;
        return ship.getAttachment(ProjectingShipAtt.class);
    }

    private long shipId;
    private LazyShip lazyShip;
    private SavedBlockPos projectorBp;  //to do what if projector is destroyed?

    public BlockPos getProjectorBp() { return projectorBp.toBp(); }

    private ProjectingShipAtt() {}
    public ProjectingShipAtt(ServerShip ship, BlockPos inProjectorBp) {
        shipId = ship.getId();
        lazyShip = new LazyShip((l, owner) -> ShipUtil.getShipByID(l, ((ProjectingShipAtt)owner).shipId));
        projectorBp = new SavedBlockPos(inProjectorBp);
    }

    //由于iitalize与ProjectorBE代码重复，现在不再使用
    public ProjectingShipAtt initialize(ServerLevel level) {
        ServerShip ship = lazyShip.get(level, this);
        ShipBuilder builder = ShipBuilder.modify(level, ship);

        if (builder == null) {
            EzDebug.warn("fail to get projecting ship with id:" + shipId);
            return this;
        }

        if (builder.isEmpty()) {
            builder.addBlock(BlockPos.ZERO, WapBlocks.Industrial.PROJECT_CENTER.getDefaultState());
            EzDebug.highlight("initialize: place project center because empty ship");
        }

        return this;
    }
    //todo shedule on greenprint
    public ProjectingShipAtt onUpdateBlock(ServerLevel level, BlockPos at) {
        ServerShip ship = lazyShip.get(level, this);
        ShipBuilder builder = Objects.requireNonNull(ShipBuilder.modify(level, ship));

        //for safe, also check at at
        if (level.getBlockState(at).getBlock() instanceof ProjectCenter)
            level.setBlockAndUpdate(at, Blocks.AIR.defaultBlockState());//todo update immedate?
        for (Direction dir : Direction.values()) {
            BlockPos curPos = at.relative(dir);

            if (level.getBlockState(curPos).getBlock() instanceof ProjectCenter)
                level.setBlockAndUpdate(curPos, Blocks.AIR.defaultBlockState());
        }

        if (builder.isEmpty()) {
            builder.addBlock(BlockPos.ZERO, WapBlocks.Industrial.PROJECT_CENTER.getDefaultState());
            EzDebug.highlight("initialize: place project center because empty ship");
        }

        return this;
    }
}
