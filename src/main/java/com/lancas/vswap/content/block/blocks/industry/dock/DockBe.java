package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.saved.vs_constraint.ConstraintSmartHolder;
import com.lancas.vswap.content.saved.vs_constraint.ConstraintTarget;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.EventMgr;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.data.SavedBlockPos;
import com.lancas.vswap.foundation.handler.construct.ShipConstructHandler;
import com.lancas.vswap.foundation.handler.multiblock.IMultiContainerBE;
import com.lancas.vswap.foundation.handler.multiblock.IMultiContainerType;
import com.lancas.vswap.foundation.handler.multiblock.util.MultiContainerBeData;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.ship.helper.LazyShip;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.ship.helper.builder.TeleportDataBuilder;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.lancas.vswap.util.*;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint;
import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

//todo when holding ship or building ship and destroyed...
//todo rotate 90 when axis is Z
public class DockBe extends SyncedBlockEntity implements IMultiContainerBE, IHaveGoggleInformation {
    protected long holdingVsShipId = -1;
    protected ConstraintSmartHolder constraintHolder;
    protected List<BlockPos> greenPrintHolders = new ArrayList<>();
    public ShipConstructHandler constructHandler = null;

    //don't save
    private final LazyShip lazyOnShip = LazyShip.ofBlockPos(new SavedBlockPos(worldPosition));

    public boolean isHoldingShip() { return holdingVsShipId >= 0; }

    public DockBe(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);

        EventMgr.Server.onVsShipUnloaded.addListener(id -> {
            if (holdingVsShipId == id) {
                holdingVsShipId = -1;
                EzDebug.highlight("remove holding shipId by vsShipUnload");
                //todo remove constraintHolder here?
                notifyUpdate();

                constructHandler = null;
            }
        });
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (VsUtil.isDummy(level))
            return;

        if (constraintHolder != null) {  //don't try to remove constraint if now is dummy (when game is starting, constraint won't be removed, anyway it's better than game crash)
            constraintHolder.setRemoved();
            constraintHolder = null;
        }
    }

    public void tick() {
        /*if (level == null || level.isClientSide)
            return;

        if (isController()) {
            RandUtil.pushSeed(worldPosition);
            int color = RandUtil.nextColor();
            RandUtil.popSeed();

            showOutline(color);
        }*/
        //tickMultiBlock();
        //tickTeleportShip();
    }

    /*protected void tickTeleportShip() {
        if (!isController() || !(level instanceof ServerLevel sLevel))  //todo be careful when compact interactive
            return;

        ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingVsShipId);
        if (holdingShip == null || holdingShip.getShipAABB() == null)
            return;





        Quaterniondc dockRotation = Optional.ofNullable(ShipUtil.getServerShipAt(sLevel, worldPosition))
            .map(s -> s.getTransform().getShipToWorldRotation())
            .orElse(new Quaterniond());

        Vector3dc bottomCenter = JomlUtil.dFaceCenter(holdingShip.getShipAABB(), Direction.DOWN);
        Vector3dc worldBlockTop = WorldUtil.getWorldCenter(sLevel, worldPosition).add(0, 0.5, 0);

        ShipUtil.teleport(sLevel, holdingShip, TeleportDataBuilder.noMovementOf(sLevel, holdingShip)
            .setRotation(dockRotation)
            .moveShipPosToWorld(holdingShip, bottomCenter, worldBlockTop)
            .get()
        );
    }*/
    protected Vector3d getMultiCenterTopInWorld(ServerLevel level) {
        ServerShip inShip = lazyOnShip.get(level, this);
        Vector3d multiDockCenterTop = JomlUtil.dLowerCorner(worldPosition).add(
            multiData.getLengthOfAxis(Direction.Axis.X) / 2.0,
            0.875,  //todo topY adjust
            multiData.getLengthOfAxis(Direction.Axis.Z) / 2.0
        );
        if (inShip != null)
            inShip.getTransform().getShipToWorld().transformPosition(multiDockCenterTop);

        return multiDockCenterTop;
    }
    protected Vector3d getMultiCenterTopInShipOrWorld() {
        //ServerShip inShip = lazyOnShip.get(level, this);
        Vector3d multiDockCenterTop = JomlUtil.dLowerCorner(worldPosition).add(
            multiData.getLengthOfAxis(Direction.Axis.X) / 2.0,
            0.875,  //todo topY adjust
            multiData.getLengthOfAxis(Direction.Axis.Z) / 2.0
        );
        //if (inShip != null)
        //    inShip.getTransform().getShipToWorld().transformPosition(multiDockCenterTop);

        return multiDockCenterTop;
    }

    private void moveAndApplyConstraint(ServerLevel level, ServerShip ship, Vector3dc bottomCenterInShip, Quaterniond addRot) {
        if (constraintHolder != null) {
            EzDebug.warn("prev constraintHolder is not null! it should be set null after reset/remove");
            constraintHolder.setRemoved();
        }

        constraintHolder = new ConstraintSmartHolder(
            ConstraintTarget.ofBlockNow(level, worldPosition),  //self
            ConstraintTarget.of(ship),
            level
        );
        constraintHolder.addConstraint(level, (id0, id1) -> {
            /*return new VSAttachmentOrientationConstraint(
                id0, id1,
                1e-10,
                getMultiCenterTopInShipOrWorld(), bottomCenterInShip,
                1e10,
                new Quaterniond(), addRot,
                1e10
            );*/
            return new VSAttachmentConstraint(
                id0, id1,
                1e-10,
                getMultiCenterTopInShipOrWorld()/*.add(0, 2, 0)/*todo temp*/, bottomCenterInShip,
                1e10, 0
            );
        }).addConstraint(level, (id0, id1) -> {
            return new VSFixedOrientationConstraint(
                id0, id1,
                1e-10,
                new Quaterniond(), addRot,
                1e10
            );
        });

        Vector3d multiCenterTopInWorld = getMultiCenterTopInWorld(level);
        EzDebug.log("move shipPos " + StrUtil.F2(bottomCenterInShip) + " to " + StrUtil.F2(multiCenterTopInWorld));

        ShipUtil.teleport(level, ship, TeleportDataBuilder.noMovementOf(level, ship)  //initial teleport
            .moveShipPosToWorld(ship, bottomCenterInShip, multiCenterTopInWorld)
            .get()
        );
    }
    public ItemStack onInteract(ItemStack withStack) {
        if (withStack.isEmpty())
            return ItemStack.EMPTY;

        /*todo if (withStack.getItem() instanceof IDocker) {
            if (level == null || level.isClientSide)  //put ship must in server
                return withStack;

            boolean success = getControllerBE().tryPutDocker(withStack, false);
            return success ? ItemStack.EMPTY : withStack;
        }*/

        return withStack;
    }
    public boolean tryPutDocker(ItemStack stack, boolean simulate) {
        //if (!(level instanceof ServerLevel sLevel))
        //    return false;
        if (!isController()) {
            //EzDebug.warn("try put docker to non-controller");
            //return false;
            DockBe controllerBe = getControllerBE();
            if (!controllerBe.isController()) {
                EzDebug.warn("get controller be but find out it is not controller");
                return false;
            }
            return controllerBe.tryPutDocker(stack, simulate);
        }

        if (isHoldingShip()) {
            EzDebug.light("can't load ship because isHolding one");
            return false;
        }
        //EzDebug.light("not holding ship:" + holdingVsShipId);

        if (!(stack.getItem() instanceof Docker)) {
            EzDebug.warn("the stack try to put in is not docker");
            return false;
        }

        AABBi shapeAABB = Docker.getShipAABBContainsShape(stack);
        Vector3dc scale = Docker.getScale(stack);
        if (shapeAABB == null || !shapeAABB.isValid() || scale == null) {
            EzDebug.warn("fail to get shapeAABB of Docker or scale of Docker");
            return false;
        }


        /*Vector3i toPutBottomSize = JomlUtil.faceSize(shapeAABB, Direction.DOWN);
        int shipLength = Math.max(toPutBottomSize.x, toPutBottomSize.z);
        int shipWidth = Math.min(toPutBottomSize.x, toPutBottomSize.z);  //todo ship len scaled by scale
        EzDebug.log("shipLength:" + shipLength + ", shipWidth:" + shipWidth + ", dockLen:" + multiData.length + ", dockWidth:" + multiData.width);
        if (shipLength > multiData.length || shipWidth > multiData.width) {
            EzDebug.light("toPutSize:" + StrUtil.poslike(shipLength, 0, shipWidth) + ", this size:" + StrUtil.poslike(multiData.length, 0, multiData.width));
            return false;
        }*/
        Quaterniond addRot = new Quaterniond();
        if (!canPutIn(shapeAABB, scale, addRot)) {
            //EzDebug.light("fail to put in because dock size is not enough");
            return false;
        }

        //can put in
        /*RigidbodyData rigidData = new RigidbodyData();
        rigidData.setPosition(WorldUtil.getWorldCenter(level, worldPosition).add(0, 0.5, 0));

        BlockClusterData blockData = new BlockClusterData();
        blockData

        SandBoxServerShip ship = new SandBoxServerShip(UUID.randomUUID(), x, );
        SandBoxServerWorld.addShipAndSyncClient(level, );*/
        if (!simulate) {
            if (!(level instanceof ServerLevel sLevel)) {
                EzDebug.warn("only can simulate when level is client");
                return false;
            }

            ShipBuilder builder = Docker.makeVsShipBuilder(sLevel, stack, true, true);
            if (builder == null) {
                EzDebug.warn("fail to make vsShip for placing on dock");
                return false;
            }

            /*builder.moveFaceTo(Direction.DOWN, getMultiCenterTopInWorld(sLevel))
                .setStatic(true);

            holdingVsShipId = builder.getId();
            EzDebug.log("holdingVsShipId set :" + builder.getId());
            notifyUpdate();*/
            Vector3d bottomCenterInShip = builder.getFaceCenterInShip(Direction.DOWN);
            if (bottomCenterInShip == null) {
                EzDebug.warn("get null shipBottomCenterInShip");
                return false;
            }

            /*if (constraintHolder != null) {
                EzDebug.warn("prev constraintHolder is not null! it should be set null after reset/remove");
                constraintHolder.setRemoved();
            }
            constraintHolder = new ConstraintSmartHolder(
                ConstraintTarget.ofBlockNow(sLevel, worldPosition),  //self
                ConstraintTarget.of(builder.get()),
                sLevel
            );
            constraintHolder.addConstraint(sLevel, (id0, id1) -> {
                return new VSAttachmentOrientationConstraint(
                    id0, id1,
                    1e-10,
                    getMultiCenterTopInShipOrWorld(), shipBottomCenterInShip,
                    1e10,
                    new Quaterniond(), additionalRotation,
                    1e10
                );
            });*/
            moveAndApplyConstraint(sLevel, builder.get(), bottomCenterInShip, addRot);
            holdingVsShipId = builder.getId();

            //EzDebug.log("holdingVsShipId set :" + builder.getId());
            notifyUpdate();
        }
        return true;
    }

    public boolean tryPutShip(@NotNull Ship ship, boolean simulate) {
        if (!isController()) {
            DockBe controllerBe = getControllerBE();
            if (!controllerBe.isController()) {
                EzDebug.warn("get controller be but find out it is not controller");
                return false;
            }
            return controllerBe.tryPutShip(ship, simulate);
        }

        if (isHoldingShip()) {
            EzDebug.light("can't load ship because isHolding one");
            return false;
        }
        AABBic shipAABB = ship.getShipAABB();
        Vector3dc scale = ship.getTransform().getShipToWorldScaling();
        if (shipAABB == null || !shipAABB.isValid()) {
            EzDebug.warn("fail to get shipAABB of ship");
            return false;
        }

        Quaterniond addRot = new Quaterniond();
        if (!canPutIn(shipAABB, scale, addRot)) {
            //EzDebug.light("fail to put in because dock size is not enough");
            return false;
        }

        if (!simulate) {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("only can simulate when level is client");
                return false;
            }

            ShipBuilder builder = ShipBuilder.modify(sLevel, sShip);
            Vector3d bottomCenterInShip = builder.getFaceCenterInShip(Direction.DOWN);
            if (bottomCenterInShip == null) {
                EzDebug.warn("get null shipBottomCenterInShip");
                return false;
            }

            moveAndApplyConstraint(sLevel, builder.get(), bottomCenterInShip, addRot);
            holdingVsShipId = builder.getId();

            notifyUpdate();
        }
        return true;
    }

    public ItemStack construct(ItemStack material, boolean simulate) {
        if (!isController()) {
            return getControllerBE().construct(material, simulate);
        }

        if (!(level instanceof ServerLevel sLevel))
            return material;  //must start construction in server
        if (constructHandler != null) {  //already started
            return constructHandler.putMaterial(sLevel, material, simulate);
        }

        if (holdingVsShipId >= 0)
            return material;  //holding ship and not constructing

        if (!(material.getItem() instanceof MaterialStandardizedItem ms)) {
            EzDebug.warn("construct accept not ms item:" + material.getItem());
            return material;
        }

        Vector3d centerTop = getMultiCenterTopInWorld(sLevel);

        for (BlockPos gpHolderBp : greenPrintHolders) {
            if (!(sLevel.getBlockEntity(gpHolderBp) instanceof GreenPrintHolderBe gpHolderBe)) {
                EzDebug.warn("at " + gpHolderBp + " can't get GreenPrintHolderBe");
                continue;
            }

            //judge if can start this construction
            RRWChunkyShipSchemeData curSchemeData = gpHolderBe.getNotEmptySchemeData();
            if (curSchemeData == null)
                continue;

            AABBic shapeSize = curSchemeData.getLocalAabbContainsShape();
            Vector3dc scale = curSchemeData.getScale();

            Quaterniond additionalRot = new Quaterniond();
            if (!canPutIn(shapeSize, scale, additionalRot))
                continue;

            //todo warn:this maybe slow!
            ShipConstructHandler testConstructHandler = new ShipConstructHandler(curSchemeData, centerTop);
            ItemStack remain = testConstructHandler.putMaterial(sLevel, material, simulate);

            if (remain.equals(material, true)) {  //not change
                continue;
            }

            if (!simulate) {
                constructHandler = testConstructHandler;

                ServerShip constructing = constructHandler.getConstructingShip(sLevel);
                holdingVsShipId = constructing.getId();
                BlockPos origin = RRWChunkyShipSchemeData.getOriginInShipForScheme(sLevel, constructing);
                AABBi constructedShipAABB = new AABBi(shapeSize).translate(origin.getX(), origin.getY(), origin.getZ());

                moveAndApplyConstraint(sLevel, constructing, JomlUtil.dFaceCenter(constructedShipAABB, Direction.DOWN), additionalRot);
            }
            return remain;
        }

        //no one gp match
        return material;
    }
    public boolean creativeConstruct(boolean simulate) {
        if (!isController()) {
            return getControllerBE().creativeConstruct(simulate);
        }

        if (!(level instanceof ServerLevel sLevel))
            return false;  //must start construction in server

        //todo doing construct stuff
        if (constructHandler != null) {
            if (!simulate)
                constructHandler.creativePutMaterial(sLevel);
            return true;  //already started
        }

        //not constructing but holding a ship
        if (holdingVsShipId >= 0)
            return false;  //holding ship

        Vector3d centerTop = getMultiCenterTopInWorld(sLevel);

        for (BlockPos gpHolderBp : greenPrintHolders) {
            if (!(sLevel.getBlockEntity(gpHolderBp) instanceof GreenPrintHolderBe gpHolderBe)) {
                EzDebug.warn("at " + gpHolderBp + " can't get GreenPrintHolderBe");
                continue;
            }

            //judge if can start this construction
            RRWChunkyShipSchemeData curSchemeData = gpHolderBe.getNotEmptySchemeData();
            if (curSchemeData == null)
                continue;

            //EzDebug.log("coordSize:" + curSchemeData.getLocalAabbContainsCoordinate());
            //EzDebug.log("shapeSize:" + curSchemeData.getLocalAabbContainsShape());
            AABBic shapeSize = curSchemeData.getLocalAabbContainsShape();
            Vector3dc scale = curSchemeData.getScale();

            if (!shapeSize.isValid()) {
                EzDebug.warn("get shape size is not valid:" + shapeSize);
                continue;
            }

            Quaterniond additionalRot = new Quaterniond();
            if (!canPutIn(shapeSize, scale, additionalRot))
                continue;

            if (!simulate) {
                //this can put in, mean it can built
                constructHandler = new ShipConstructHandler(curSchemeData, centerTop);
                constructHandler.creativePutMaterial(sLevel);

                ServerShip constructing = constructHandler.getConstructingShip(sLevel);
                holdingVsShipId = constructing.getId();
                BlockPos origin = RRWChunkyShipSchemeData.getOriginInShipForScheme(sLevel, constructing);
                AABBi constructedShipAABB = new AABBi(shapeSize).translate(origin.getX(), origin.getY(), origin.getZ());

                moveAndApplyConstraint(sLevel, constructing, JomlUtil.dFaceCenter(constructedShipAABB, Direction.DOWN), additionalRot);
            }
            return true;
        }
        return false;  //no one match
    }

    protected boolean canPutIn(AABBic shapeSize, Vector3dc scale, Quaterniond rotationWithDock) {
        return canPutIn(JomlUtil.scaleFromCenter(shapeSize, scale, new AABBd()), rotationWithDock);
    }
    protected boolean canPutIn(AABBdc worldSize, Quaterniond rotationWithDock) {
        if (!isController()) {
            return getControllerBE().canPutIn(worldSize, rotationWithDock);
        }
        if (level == null)
            return false;

        double selfScale = WorldUtil.getScaleOfShipOrWorld(level, worldPosition);
        double selfWorldLength = multiData.length * selfScale;
        double selfWorldWidth = multiData.width * selfScale;

        Vector3d toPutBottomSize = JomlUtil.faceSize(worldSize, Direction.DOWN);
        double toPutLength = Math.max(toPutBottomSize.x, toPutBottomSize.z);
        double toPutWidth = Math.min(toPutBottomSize.x, toPutBottomSize.z);  //todo ship len scaled by scale

        Direction.Axis toPutLongAxis = toPutBottomSize.x >= toPutBottomSize.z ? Direction.Axis.X : Direction.Axis.Z;

        if (toPutLength > selfWorldLength || toPutWidth > selfWorldWidth) {
            EzDebug.light("toPutSize is greater than this toPutSize:" + StrUtil.poslike(toPutLength, 0, toPutWidth) + ", this size:" + StrUtil.poslike(multiData.length, 0, multiData.width));
            return false;
        }

        if (toPutLongAxis == multiData.lengthAxis)
            rotationWithDock.identity();
        else
            rotationWithDock.setAngleAxis(Math.PI / 2.0, 0, 1, 0);

        //EzDebug.log("toPutLongAxis:" + toPutLongAxis + ", dockLongAxis:" + multiData.lengthAxis + ", rot:" + rotationWithDock);

        return true;
    }


    public boolean unboundHoldingShip(boolean forceUnboundEvenIncomplete, boolean simulate, Dest<ServerShip> dest) {
        if (VsUtil.isDummy(level))
            return false;

        if (!isController()) {
            return getControllerBE().unboundHoldingShip(forceUnboundEvenIncomplete, simulate, dest);
        }

        if (!(level instanceof ServerLevel sLevel))  //only unbound in server
            return false;

        if (holdingVsShipId < 0)
            return false;

        boolean canUnbound;
        if (constructHandler == null) {  //not constrcuting
            canUnbound = true;
        }
        else {
            if (forceUnboundEvenIncomplete) {
                canUnbound = true;  //if force, must be able to unbound
            } else {
                canUnbound = constructHandler.isCompleted();
            }
        }

        if (!canUnbound)
            return false;

        if (!simulate) {
            constructHandler = null;
            constraintHolder.setRemoved();
            constraintHolder = null;
            dest.set(ShipUtil.getServerShipByID(sLevel, holdingVsShipId));
            holdingVsShipId = -1;

            notifyUpdate();
        }
        return true;
    }

    public void showOutline(int color) {
        BlockPos controllerPos = getController();
        AABB outline = new AABB(
            controllerPos.getX(), controllerPos.getY(), controllerPos.getZ(),
            controllerPos.getX() + multiData.getLengthOfAxis(Direction.Axis.X), controllerPos.getY() + multiData.height, controllerPos.getZ() + multiData.getLengthOfAxis(Direction.Axis.Z)
        );
        String key = "dockbe outline" + worldPosition.toShortString();
        //RandUtil.pushSeed(key);
        //int color = RandUtil.nextColor();
        //RandUtil.popSeed();

        /*if (level instanceof ServerLevel && isController()) {
            NetworkHandler.sendToAllPlayers(new CreateOutlinePacketS2C(key, outline, color));
        } else {
            CreateClient.OUTLINER.showAABB(key, outline)
                .lineWidth(0.0625f)
                .colored(color);
        }*/
        CreateClient.OUTLINER.showAABB(key, outline)
            .lineWidth(0.0625f)
            .colored(color);
    }


    /*@OnlyIn(Dist.CLIENT)
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        //showOutline(WapColors.SHOWCASE_BLUE);
        //boolean result = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        //if (level == null)
        //    return result;



        //EzDebug.log("add goggle tooltip side:" + EffectiveSide.get());
        RobotArm.showRange(level, worldPosition);
        //EzDebug.log("arm is null?:" + (armPoint == null));
        if (armPoint != null) {
            boolean armPointInRange = RobotArm.isInRange(level, RobotArm.getRange(level, worldPosition), armPoint.getPos());
            RobotArm.showArmPoint(armPoint, armPointInRange);
        }
        return result;
    }*/



    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        NbtBuilder.modify(tag)
            .putCompound("multi_container_data", multiData.serializeNBT())
            .putLong("holding_vs_ship_id", holdingVsShipId)
            //.putQuaternion("additional_rot", shipAdditionalRotation)
            .putNBTSerializableIfNonNull("constraint_holder", constraintHolder)
            .putEach("green_print_holders", greenPrintHolders, NbtBuilder::tagOfBlockPos)
            .putNBTSerializableIfNonNull("construct", constructHandler);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readCompoundDo("multi_container_data", multiData::deserializeNBT)
            .readLongDo("holding_vs_ship_id", v -> holdingVsShipId = v)
            //.readQuaternionD("additional_rot", shipAdditionalRotation)
            //.readCompoundDo("constraint_holder", t -> constraintHolder = new ConstraintSmartHolder(t))
            .readCompoundDoIfExist("constraint_holder", t -> constraintHolder = new ConstraintSmartHolder(t))
            .readEachCompoundOverwrite("green_print_holders", NbtBuilder::blockPosOf, greenPrintHolders)
            .readCompoundDoIfExist("construct", t -> constructHandler = new ShipConstructHandler(t));
    }

    //multi block
    protected final MultiContainerBeData multiData = new MultiContainerBeData();

    @Override
    public boolean isPartOf(IMultiContainerType type) { return type instanceof DockMultiContainerType; }

    @Override
    public void setContinuousHeight(int h) { multiData.continuousHeight = h; }
    @Override
    public int getContinuousHeight() { return multiData.continuousHeight; }

    @Override
    public void setContinuousZLen(int l) { multiData.continuousZLen = l; }
    @Override
    public int getContinuousZLen() { return multiData.continuousZLen; }

    @Override
    public void onMultiContainerReset() {
        //multiData.reset();
        holdingVsShipId = -1;  //todo pop ship when reset?
        greenPrintHolders.clear();

        if (constraintHolder != null) {
            if (!VsUtil.isDummy(level)) {
                constraintHolder.setRemoved();
                constraintHolder = null;
            }
        }

        constructHandler = null;

        notifyUpdate();
    }

    @Override
    public void setController(BlockPos controller) { multiData.controller = controller; }

    @Override
    public void setSize(int length, int width, Direction.Axis lengthAxis, int height) { multiData.setSize(length, width, lengthAxis, height); }

    @Override
    public int getLengthOfAxis(Direction.Axis axis) { return multiData.getLengthOfAxis(axis); }

    @Override
    public @NotNull BlockPos getController() { return (isController() || multiData.controller == null) ? worldPosition : multiData.controller; }

    @Override
    public @NotNull DockBe getControllerBE() {
        if (multiData.controller == null || isController())
            return this;

        if (level == null) {
            EzDebug.warn("get controller BE when level is null! return this");
            return this;
        }

        if (!(level.getBlockEntity(multiData.controller) instanceof DockBe controllerBE)) {
            EzDebug.warn("controller is " + multiData.controller.toShortString() + ", but fail to get DockeBe at this pos. ret this");
            return this;
        }

        return controllerBE;
    }

    @Override
    public boolean isController() { return multiData.controller == null || multiData.controller.equals(worldPosition); }

    @Override
    public void setDirty() { notifyUpdate(); }

    @Override
    public void onIncludePart(BlockPos bp, IMultiContainerBE part) {
        if (part instanceof GreenPrintHolderBe) {
            greenPrintHolders.add(bp);
        }
    }
}
