package com.lancas.vswap.mixinfriend;

/*
import com.lancas.vswap.mixins.create.ponder.PonderSceneAccessor;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.instruction.*;

import java.util.function.Consumer;

public interface IPonderVsScene {
    public PonderWorld ponderWorld();
    public PonderScene ponderScene();
    public default PonderSceneAccessor ponderSceneAccessor() {
        return (PonderSceneAccessor)ponderScene();
    }

    public VsInstructions vs();

    public static class VsInstructions {
        protected final IPonderVsScene vsScene;
        public VsInstructions(IPonderVsScene inVsScene) {
            vsScene = inVsScene;
        }

        protected void addInstruction(Consumer<PonderScene> instruction) {
            addInstruction(PonderInstruction.simple(instruction));
        }
        protected void addInstruction(PonderInstruction instruction) {
            vsScene.ponderSceneAccessor().getSchedule().add(instruction);
        }


    }
}
*/



/*public class WorldInstructions {
    public WorldInstructions() {
    }

    public void incrementBlockBreakingProgress(BlockPos pos) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            PonderWorld world = scene.getWorld();
            int progress = (Integer)world.getBlockBreakingProgressions().getOrDefault(pos, -1) + 1;
            if (progress == 9) {
                world.addBlockDestroyEffects(pos, world.getBlockState(pos));
                world.destroyBlock(pos, false);
                world.setBlockBreakingProgress(pos, 0);
                scene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
            } else {
                world.setBlockBreakingProgress(pos, progress + 1);
            }

        }));
    }

    public void showSection(Selection selection, Direction fadeInDirection) {
        SceneBuilder var10000 = SceneBuilder.this;
        PonderScene var10006 = SceneBuilder.this.scene;
        Objects.requireNonNull(var10006);
        var10000.addInstruction(new DisplayWorldSectionInstruction(15, fadeInDirection, selection, Optional.of(var10006::getBaseWorldSection)));
    }

    public void showSectionAndMerge(Selection selection, Direction fadeInDirection, ElementLink<WorldSectionElement> link) {
        SceneBuilder.this.addInstruction(new DisplayWorldSectionInstruction(15, fadeInDirection, selection, Optional.of((Supplier)() -> (WorldSectionElement)SceneBuilder.this.scene.resolve(link))));
    }

    public void glueBlockOnto(BlockPos position, Direction fadeInDirection, ElementLink<WorldSectionElement> link) {
        SceneBuilder.this.addInstruction(new DisplayWorldSectionInstruction(15, fadeInDirection, SceneBuilder.this.scene.getSceneBuildingUtil().select.position(position), Optional.of((Supplier)() -> (WorldSectionElement)SceneBuilder.this.scene.resolve(link)), position));
    }

    public ElementLink<WorldSectionElement> showIndependentSection(Selection selection, Direction fadeInDirection) {
        DisplayWorldSectionInstruction instruction = new DisplayWorldSectionInstruction(15, fadeInDirection, selection, Optional.empty());
        SceneBuilder.this.addInstruction(instruction);
        return instruction.createLink(SceneBuilder.this.scene);
    }

    public ElementLink<WorldSectionElement> showIndependentSection(Selection selection, Direction fadeInDirection, int fadeInDuration) {
        DisplayWorldSectionInstruction instruction = new DisplayWorldSectionInstruction(fadeInDuration, fadeInDirection, selection, Optional.empty());
        SceneBuilder.this.addInstruction(instruction);
        return instruction.createLink(SceneBuilder.this.scene);
    }

    public ElementLink<WorldSectionElement> showIndependentSectionImmediately(Selection selection) {
        DisplayWorldSectionInstruction instruction = new DisplayWorldSectionInstruction(0, Direction.DOWN, selection, Optional.empty());
        SceneBuilder.this.addInstruction(instruction);
        return instruction.createLink(SceneBuilder.this.scene);
    }

    public void hideSection(Selection selection, Direction fadeOutDirection) {
        WorldSectionElement worldSectionElement = new WorldSectionElement(selection);
        ElementLink<WorldSectionElement> elementLink = new ElementLink(WorldSectionElement.class);
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            scene.getBaseWorldSection().erase(selection);
            scene.linkElement(worldSectionElement, elementLink);
            scene.addElement(worldSectionElement);
            worldSectionElement.queueRedraw();
        }));
        this.hideIndependentSection(elementLink, fadeOutDirection);
    }

    public void hideIndependentSection(ElementLink<WorldSectionElement> link, Direction fadeOutDirection) {
        SceneBuilder.this.addInstruction(new FadeOutOfSceneInstruction(15, fadeOutDirection, link));
    }

    public void hideIndependentSection(ElementLink<WorldSectionElement> link, Direction fadeOutDirection, int fadeOutDuration) {
        SceneBuilder.this.addInstruction(new FadeOutOfSceneInstruction(fadeOutDuration, fadeOutDirection, link));
    }

    public void hideIndependentSectionImmediately(ElementLink<WorldSectionElement> link) {
        SceneBuilder.this.addInstruction(new FadeOutOfSceneInstruction(0, Direction.DOWN, link));
    }

    public void restoreBlocks(Selection selection) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> scene.getWorld().restoreBlocks(selection)));
    }

    public ElementLink<WorldSectionElement> makeSectionIndependent(Selection selection) {
        WorldSectionElement worldSectionElement = new WorldSectionElement(selection);
        ElementLink<WorldSectionElement> elementLink = new ElementLink(WorldSectionElement.class);
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            scene.getBaseWorldSection().erase(selection);
            scene.linkElement(worldSectionElement, elementLink);
            scene.addElement(worldSectionElement);
            worldSectionElement.queueRedraw();
            worldSectionElement.resetAnimatedTransform();
            worldSectionElement.setVisible(true);
            worldSectionElement.forceApplyFade(1.0F);
        }));
        return elementLink;
    }

    public void rotateSection(ElementLink<WorldSectionElement> link, double xRotation, double yRotation, double zRotation, int duration) {
        SceneBuilder.this.addInstruction(AnimateWorldSectionInstruction.rotate(link, new Vec3(xRotation, yRotation, zRotation), duration));
    }

    public void configureCenterOfRotation(ElementLink<WorldSectionElement> link, Vec3 anchor) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> ((WorldSectionElement)scene.resolve(link)).setCenterOfRotation(anchor)));
    }

    public void configureStabilization(ElementLink<WorldSectionElement> link, Vec3 anchor) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> ((WorldSectionElement)scene.resolve(link)).stabilizeRotation(anchor)));
    }

    public void moveSection(ElementLink<WorldSectionElement> link, Vec3 offset, int duration) {
        SceneBuilder.this.addInstruction(AnimateWorldSectionInstruction.move(link, offset, duration));
    }

    public void rotateBearing(BlockPos pos, float angle, int duration) {
        SceneBuilder.this.addInstruction(AnimateBlockEntityInstruction.bearing(pos, angle, duration));
    }

    public void movePulley(BlockPos pos, float distance, int duration) {
        SceneBuilder.this.addInstruction(AnimateBlockEntityInstruction.pulley(pos, distance, duration));
    }

    public void animateBogey(BlockPos pos, float distance, int duration) {
        SceneBuilder.this.addInstruction(AnimateBlockEntityInstruction.bogey(pos, distance, duration + 1));
    }

    public void moveDeployer(BlockPos pos, float distance, int duration) {
        SceneBuilder.this.addInstruction(AnimateBlockEntityInstruction.deployer(pos, distance, duration));
    }

    public void setBlocks(Selection selection, BlockState state, boolean spawnParticles) {
        SceneBuilder.this.addInstruction(new ReplaceBlocksInstruction(selection, ($) -> state, true, spawnParticles));
    }

    public void destroyBlock(BlockPos pos) {
        this.setBlock(pos, Blocks.AIR.defaultBlockState(), true);
    }

    public void setBlock(BlockPos pos, BlockState state, boolean spawnParticles) {
        this.setBlocks(SceneBuilder.this.scene.getSceneBuildingUtil().select.position(pos), state, spawnParticles);
    }

    public void replaceBlocks(Selection selection, BlockState state, boolean spawnParticles) {
        this.modifyBlocks(selection, ($) -> state, spawnParticles);
    }

    public void modifyBlock(BlockPos pos, UnaryOperator<BlockState> stateFunc, boolean spawnParticles) {
        this.modifyBlocks(SceneBuilder.this.scene.getSceneBuildingUtil().select.position(pos), stateFunc, spawnParticles);
    }

    public void cycleBlockProperty(BlockPos pos, Property<?> property) {
        this.modifyBlocks(SceneBuilder.this.scene.getSceneBuildingUtil().select.position(pos), (s) -> s.hasProperty(property) ? (BlockState)s.cycle(property) : s, false);
    }

    public void modifyBlocks(Selection selection, UnaryOperator<BlockState> stateFunc, boolean spawnParticles) {
        SceneBuilder.this.addInstruction(new ReplaceBlocksInstruction(selection, stateFunc, false, spawnParticles));
    }

    public void toggleRedstonePower(Selection selection) {
        this.modifyBlocks(selection, (s) -> {
            if (s.hasProperty(BlockStateProperties.POWER)) {
                s = (BlockState)s.setValue(BlockStateProperties.POWER, (Integer)s.getValue(BlockStateProperties.POWER) == 0 ? 15 : 0);
            }

            if (s.hasProperty(BlockStateProperties.POWERED)) {
                s = (BlockState)s.cycle(BlockStateProperties.POWERED);
            }

            if (s.hasProperty(RedstoneTorchBlock.LIT)) {
                s = (BlockState)s.cycle(RedstoneTorchBlock.LIT);
            }

            return s;
        }, false);
    }

    public <T extends Entity> void modifyEntities(Class<T> entityClass, Consumer<T> entityCallBack) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> scene.forEachWorldEntity(entityClass, entityCallBack)));
    }

    public <T extends Entity> void modifyEntitiesInside(Class<T> entityClass, Selection area, Consumer<T> entityCallBack) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> scene.forEachWorldEntity(entityClass, (e) -> {
            if (area.test(e.blockPosition())) {
                entityCallBack.accept(e);
            }

        })));
    }

    public void modifyEntity(ElementLink<EntityElement> link, Consumer<Entity> entityCallBack) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            EntityElement resolve = (EntityElement)scene.resolve(link);
            if (resolve != null) {
                Objects.requireNonNull(entityCallBack);
                resolve.ifPresent(entityCallBack::accept);
            }

        }));
    }

    public ElementLink<EntityElement> createEntity(Function<Level, Entity> factory) {
        ElementLink<EntityElement> link = new ElementLink(EntityElement.class, UUID.randomUUID());
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            PonderWorld world = scene.getWorld();
            Entity entity = (Entity)factory.apply(world);
            EntityElement handle = new EntityElement(entity);
            scene.addElement(handle);
            scene.linkElement(handle, link);
            world.addFreshEntity(entity);
        }));
        return link;
    }

    public ElementLink<EntityElement> createItemEntity(Vec3 location, Vec3 motion, ItemStack stack) {
        return this.createEntity((world) -> {
            ItemEntity itemEntity = new ItemEntity(world, location.x, location.y, location.z, stack);
            itemEntity.setDeltaMovement(motion);
            return itemEntity;
        });
    }

    public void createItemOnBeltLike(BlockPos location, Direction insertionSide, ItemStack stack) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            PonderWorld world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(location);
            if (blockEntity instanceof SmartBlockEntity beltBlockEntity) {
                DirectBeltInputBehaviour behaviour = (DirectBeltInputBehaviour)beltBlockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE);
                if (behaviour != null) {
                    behaviour.handleInsertion(stack, insertionSide.getOpposite(), false);
                }
            }
        }));
        this.flapFunnel(location.above(), true);
    }

    public ElementLink<BeltItemElement> createItemOnBelt(BlockPos beltLocation, Direction insertionSide, ItemStack stack) {
        ElementLink<BeltItemElement> link = new ElementLink(BeltItemElement.class);
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            PonderWorld world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(beltLocation);
            if (blockEntity instanceof BeltBlockEntity beltBlockEntity) {
                DirectBeltInputBehaviour behaviour = (DirectBeltInputBehaviour)beltBlockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE);
                behaviour.handleInsertion(stack, insertionSide.getOpposite(), false);
                BeltBlockEntity controllerBE = beltBlockEntity.getControllerBE();
                if (controllerBE != null) {
                    controllerBE.tick();
                }

                TransportedItemStackHandlerBehaviour transporter = (TransportedItemStackHandlerBehaviour)beltBlockEntity.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
                transporter.handleProcessingOnAllItems((tis) -> {
                    BeltItemElement tracker = new BeltItemElement(tis);
                    scene.addElement(tracker);
                    scene.linkElement(tracker, link);
                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                });
            }
        }));
        this.flapFunnel(beltLocation.above(), true);
        return link;
    }

    public void removeItemsFromBelt(BlockPos beltLocation) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            PonderWorld world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(beltLocation);
            if (blockEntity instanceof SmartBlockEntity beltBlockEntity) {
                TransportedItemStackHandlerBehaviour transporter = (TransportedItemStackHandlerBehaviour)beltBlockEntity.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
                if (transporter != null) {
                    transporter.handleCenteredProcessingOnAllItems(0.52F, (tis) -> TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
                }
            }
        }));
    }

    public void stallBeltItem(ElementLink<BeltItemElement> link, boolean stalled) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            BeltItemElement resolve = (BeltItemElement)scene.resolve(link);
            if (resolve != null) {
                resolve.ifPresent((tis) -> tis.locked = stalled);
            }

        }));
    }

    public void changeBeltItemTo(ElementLink<BeltItemElement> link, ItemStack newStack) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            BeltItemElement resolve = (BeltItemElement)scene.resolve(link);
            if (resolve != null) {
                resolve.ifPresent((tis) -> tis.stack = newStack);
            }

        }));
    }

    public void setKineticSpeed(Selection selection, float speed) {
        this.modifyKineticSpeed(selection, (f) -> speed);
    }

    public void multiplyKineticSpeed(Selection selection, float modifier) {
        this.modifyKineticSpeed(selection, (f) -> f * modifier);
    }

    public void modifyKineticSpeed(Selection selection, UnaryOperator<Float> speedFunc) {
        this.modifyBlockEntityNBT(selection, SpeedGaugeBlockEntity.class, (nbt) -> {
            float newSpeed = (Float)speedFunc.apply(nbt.getFloat("Speed"));
            nbt.putFloat("Value", SpeedGaugeBlockEntity.getDialTarget(newSpeed));
        });
        this.modifyBlockEntityNBT(selection, KineticBlockEntity.class, (nbt) -> nbt.putFloat("Speed", (Float)speedFunc.apply(nbt.getFloat("Speed"))));
    }

    public void propagatePipeChange(BlockPos pos) {
        this.modifyBlockEntity(pos, PumpBlockEntity.class, (be) -> be.onSpeedChanged(0.0F));
    }

    public void setFilterData(Selection selection, Class<? extends BlockEntity> teType, ItemStack filter) {
        this.modifyBlockEntityNBT(selection, teType, (nbt) -> nbt.put("Filter", filter.serializeNBT()));
    }

    public void modifyBlockEntityNBT(Selection selection, Class<? extends BlockEntity> beType, Consumer<CompoundTag> consumer) {
        this.modifyBlockEntityNBT(selection, beType, consumer, false);
    }

    public <T extends BlockEntity> void modifyBlockEntity(BlockPos position, Class<T> beType, Consumer<T> consumer) {
        SceneBuilder.this.addInstruction((Consumer)((scene) -> {
            BlockEntity blockEntity = scene.getWorld().getBlockEntity(position);
            if (beType.isInstance(blockEntity)) {
                consumer.accept((BlockEntity)beType.cast(blockEntity));
            }

        }));
    }

    public void modifyBlockEntityNBT(Selection selection, Class<? extends BlockEntity> teType, Consumer<CompoundTag> consumer, boolean reDrawBlocks) {
        SceneBuilder.this.addInstruction(new BlockEntityDataInstruction(selection, teType, (nbt) -> {
            consumer.accept(nbt);
            return nbt;
        }, reDrawBlocks));
    }

    public void instructArm(BlockPos armLocation, ArmBlockEntity.Phase phase, ItemStack heldItem, int targetedPoint) {
        this.modifyBlockEntityNBT(SceneBuilder.this.scene.getSceneBuildingUtil().select.position(armLocation), ArmBlockEntity.class, (compound) -> {
            NBTHelper.writeEnum(compound, "Phase", phase);
            compound.put("HeldItem", heldItem.serializeNBT());
            compound.putInt("TargetPointIndex", targetedPoint);
            compound.putFloat("MovementProgress", 0.0F);
        });
    }

    public void flapFunnel(BlockPos position, boolean outward) {
        this.modifyBlockEntity(position, FunnelBlockEntity.class, (funnel) -> funnel.flap(!outward));
    }

    public void setCraftingResult(BlockPos crafter, ItemStack output) {
        this.modifyBlockEntity(crafter, MechanicalCrafterBlockEntity.class, (mct) -> mct.setScriptedResult(output));
    }

    public void connectCrafterInvs(BlockPos position1, BlockPos position2) {
        SceneBuilder.this.addInstruction((Consumer)((s) -> {
            ConnectedInputHandler.toggleConnection(s.getWorld(), position1, position2);
            s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
        }));
    }

    public void toggleControls(BlockPos position) {
        this.cycleBlockProperty(position, ControlsBlock.VIRTUAL);
    }

    public void animateTrainStation(BlockPos position, boolean trainPresent) {
        this.modifyBlockEntityNBT(SceneBuilder.this.scene.getSceneBuildingUtil().select.position(position), StationBlockEntity.class, (c) -> c.putBoolean("ForceFlag", trainPresent));
    }

    public void conductorBlaze(BlockPos position, boolean conductor) {
        this.modifyBlockEntityNBT(SceneBuilder.this.scene.getSceneBuildingUtil().select.position(position), BlazeBurnerBlockEntity.class, (c) -> c.putBoolean("TrainHat", conductor));
    }

    public void changeSignalState(BlockPos position, SignalBlockEntity.SignalState state) {
        this.modifyBlockEntityNBT(SceneBuilder.this.scene.getSceneBuildingUtil().select.position(position), SignalBlockEntity.class, (c) -> NBTHelper.writeEnum(c, "State", state));
    }

    public void setDisplayBoardText(BlockPos position, int line, Component text) {
        this.modifyBlockEntity(position, FlapDisplayBlockEntity.class, (t) -> t.applyTextManually(line, Component.Serializer.toJson(text)));
    }

    public void dyeDisplayBoard(BlockPos position, int line, DyeColor color) {
        this.modifyBlockEntity(position, FlapDisplayBlockEntity.class, (t) -> t.setColour(line, color));
    }

    public void flashDisplayLink(BlockPos position) {
        this.modifyBlockEntity(position, DisplayLinkBlockEntity.class, (linkBlockEntity) -> linkBlockEntity.glow.setValue((double)2.0F));
    }
}*/