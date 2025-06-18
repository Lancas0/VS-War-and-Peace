package com.lancas.vswap.subproject.pondervs;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.item.items.vsmotion.MotionRecord;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.mixinfriend.HookedPonderScene;
import com.lancas.vswap.mixinfriend.IExOutliner;
import com.lancas.vswap.mixins.create.ponder.PonderSceneAccessor;
import com.lancas.vswap.mixins.create.ponder.SceneBuilderAccessor;
import com.lancas.vswap.mixins.create.ui.InputWindowElementAccessor;
import com.lancas.vswap.subproject.pondervs.element.NoPointLineTextElement;
import com.lancas.vswap.subproject.pondervs.instructions.OnceInstruction;
import com.lancas.vswap.subproject.pondervs.instructions.TweenInstruction;
import com.lancas.vswap.subproject.pondervs.instructions.UnLimitedWorldModifyInstruction;
import com.lancas.vswap.subproject.pondervs.outline.InSpaceOBBOutline;
import com.lancas.vswap.subproject.pondervs.outline.OBBOutline;
import com.lancas.vswap.subproject.sandbox.SandBoxPonderWorld;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.compact.mc.GroundShipWrapped;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.TweenData;
import com.lancas.vswap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxPonderShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.RandUtil;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.*;
import com.simibubi.create.foundation.ponder.instruction.*;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.lang.Math;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

public class PonderVsSceneBuilder {
    public static @Nullable SandBoxPonderWorld getSandBoxPonder(PonderScene scene) {
        return ((HookedPonderScene)scene).getSandBoxPonderWorld();
    }
    public static void withSandBoxPonderDo(PonderScene scene, Consumer<SandBoxPonderWorld> action) {
        Optional.ofNullable(getSandBoxPonder(scene))
            .ifPresent(action);
    }
    public static void withPonderShipDo(PonderScene scene, UUID shipUuid, BiConsumer<SandBoxPonderWorld, SandBoxPonderShip> action) {
        Optional.ofNullable(getSandBoxPonder(scene))
            .map(x -> new BiTuple<>(x, x.getShip(shipUuid)))
            .filter(x -> x.getFirst() != null && x.getSecond() != null)
            .ifPresentOrElse(
                x -> action.accept(x.getFirst(), x.getSecond()),
                () -> EzDebug.warn("fail with ponder ship do!")
            );
    }
    public static <T extends IConstraint> void withPonderConstraintDo(PonderScene scene, UUID constraintUuid, BiConsumer<SandBoxPonderWorld, T> action) {
        Optional.ofNullable(getSandBoxPonder(scene))
            .map(x -> {
                T constraint = x.getConstraintSolver().getConstraint(constraintUuid);
                return new BiTuple<>(
                    x,
                    constraint
                );
            })
            .filter(x -> x.getFirst() != null && x.getSecond() != null)
            .ifPresentOrElse(
                x -> action.accept(x.getFirst(), x.getSecond()),
                () -> EzDebug.warn("fail with ponder constraint do!")
            );
    }

    protected SceneBuilder origin;
    public VsInstructions vs = new VsInstructions();
    public EnhancedSceneInstructions scene = new EnhancedSceneInstructions();
    public EnhancedWorldInstructions world = new EnhancedWorldInstructions();
    public EnhancedOverlayInstructions overlay = new EnhancedOverlayInstructions();
    public EnhancedEffectInstructions effect = new EnhancedEffectInstructions();

    public Util util = new Util();

    public PonderScene getPonderScene() {
        return ((SceneBuilderAccessor)origin).getScene();
    }
    public @Nullable SandBoxPonderWorld getSandBoxPonder() {
        return ((HookedPonderScene)getPonderScene()).getSandBoxPonderWorld();
    }

    public PonderVsSceneBuilder(SceneBuilder inOrigin) {
        origin = inOrigin;

        PonderScene scene = ((SceneBuilderAccessor)origin).getScene();
        ((HookedPonderScene)scene).setSandBoxPonderWorld(new SandBoxPonderWorld(scene.getWorld()));
    }

    protected void addInstruction(PonderInstruction instruction) {
        ((PonderSceneAccessor)getPonderScene()).getSchedule().add(instruction);
    }
    protected void addInstruction(Consumer<PonderScene> callback) {
        this.addInstruction(PonderInstruction.simple(callback));
    }

    public class VsInstructions {
        //Since when ponder resets inorder that instruction added, just reset when making ship is fine.

        public UUID makeShip(RigidbodyData rigidData, BlockClusterData blockData, boolean hideAtFirst) {
            UUID uuid = UUID.randomUUID();
            addInstruction(new PonderInstruction() {
                private boolean made = false;

                @Override
                public boolean isComplete() { return made; }

                @Override
                public void tick(PonderScene scene) {
                    withSandBoxPonderDo(scene, p -> {
                        if (p.containsShip(uuid)) {
                            EzDebug.warn("There is already a ship with uuid:" + uuid + " exist!");
                            made = true;
                            return;
                        }

                        SandBoxPonderShip ship = new SandBoxPonderShip(uuid, rigidData, blockData);
                        p.addShip(ship);

                        p.setShipHideState(uuid, hideAtFirst);
                        //ship.pushState();
                        made = true;
                    });
                }

                @Override
                public void reset(PonderScene scene) {
                    withSandBoxPonderDo(scene, p -> {
                        p.markShipDeleted(uuid);
                        //no need set hide, since always set hide at tick
                        //setHide?
                        //ship.popState();
                        made = false;
                    });
                }
            });
            return uuid;
        }
        public UUID makeShipWithTween(RigidbodyData rigidData, BlockClusterData blockData, TweenData.TweenFunction tween, ITransformPrimitive from, ITransformPrimitive to, int ticks, @Nullable Consumer<TweenBuilder> tweenBuilder) {
            //initialize rigidData
            rigidData.setTransformImmediately(tween.getNextTransform(rigidData.getTransform(), from, to, 0));

            UUID shipUuid = makeShip(rigidData, blockData, false);
            TweenBuilder tweenB = tweenShip(shipUuid, tween, from, to, ticks);  //from and to is immutable in tweenShip()
            if (tweenBuilder != null)
                tweenBuilder.accept(tweenB);

            return shipUuid;
        }
        public UUID makeShipWithScaleTween(RigidbodyData rigidData, BlockClusterData blockData, Vector3dc from, Vector3dc to, int ticks, @Nullable Consumer<TweenBuilder> tweenBuilder) {
            return makeShipWithTween(rigidData, blockData, TweenData.TweenFunction.Scale, new TransformPrimitive().setScale(from), new TransformPrimitive().setScale(to), ticks, tweenBuilder);
        }
        public UUID makeShipWithPositionTween(RigidbodyData rigidData, BlockClusterData blockData, Vector3dc from, Vector3dc to, int ticks, @Nullable Consumer<TweenBuilder> tweenBuilder) {
            return makeShipWithTween(rigidData, blockData, TweenData.TweenFunction.Position, new TransformPrimitive().setPosition(from), new TransformPrimitive().setPosition(to), ticks, tweenBuilder);
        }
        public UUID makeShipWithRotationTween(RigidbodyData rigidData, BlockClusterData blockData, Quaterniondc from, Quaterniondc to, int ticks, @Nullable Consumer<TweenBuilder> tweenBuilder) {
            return makeShipWithTween(rigidData, blockData, TweenData.TweenFunction.Rotation, new TransformPrimitive().setRotation(from), new TransformPrimitive().setRotation(to), ticks, tweenBuilder);
        }

        public UUID makeConstraint(Function<UUID, IConstraint> constraintSup) {
            UUID uuid = UUID.randomUUID();

            addInstruction(new PonderInstruction() {
                private boolean added = false;
                @Override
                public boolean isComplete() { return added; }

                @Override
                public void tick(PonderScene scene) {
                    if (added)
                        return;
                    added = true;
                    IConstraint constraint = constraintSup.apply(uuid);
                    withSandBoxPonderDo(scene, p -> {
                        p.getConstraintSolver().addConstraint(constraint);
                    });
                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    withPonderConstraintDo(scene, uuid, (p, c) -> {
                        p.getConstraintSolver().markConstraintRemoved(uuid);
                    });
                    added = false;
                }
            });

            return uuid;
        }
        public UUID makeWithGroundConstraint(BiFunction<UUID, GroundShipWrapped, IConstraint> constraintSup) {
            UUID uuid = UUID.randomUUID();

            addInstruction(new PonderInstruction() {
                private boolean added = false;
                @Override
                public boolean isComplete() { return added; }

                @Override
                public void tick(PonderScene scene) {
                    if (added)
                        return;
                    added = true;

                    withSandBoxPonderDo(scene, p -> {
                        GroundShipWrapped groundShip = p.wrapOrGetGround();
                        IConstraint constraint = constraintSup.apply(uuid, groundShip);
                        p.getConstraintSolver().addConstraint(constraint);
                    });
                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    withPonderConstraintDo(scene, uuid, (p, c) -> {
                        p.getConstraintSolver().markConstraintRemoved(uuid);
                    });
                    added = false;
                }
            });
            return uuid;
        }
        public <T extends IConstraint> void modifyConstraint(UUID uuid, Consumer<T> consumer) {
            addInstruction(new PonderInstruction() {
                @Override
                public boolean isComplete() { return true; }
                @Override
                public void tick(PonderScene scene) {
                    PonderVsSceneBuilder.<T>withPonderConstraintDo(scene, uuid, (p, c) -> {
                        consumer.accept(c);
                    });
                }
            });
        }
        public <T extends IConstraint> TweenBuilder tweenConstraint(UUID uuid, BiConsumer<T, Double> tweener, int ticks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);

            addInstruction(new TickingInstruction(false, ticks) {
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    double t01 = 1.0 - (double)remainingTicks / totalTicks;
                    PonderVsSceneBuilder.<T>withPonderConstraintDo(scene, uuid, (p, c) -> {
                        tweener.accept(c, curve.get().evaluate(t01));
                    });
                }
            });

            return new TweenBuilder(curve::set, curve::get);
        }

        public void deleteConstraint(UUID uuid) {
            addInstruction(new OnceInstruction() {
                @Override
                public void execute(PonderScene scene) {  //no need to reset, anyway it will always be added before removed, right?
                    withSandBoxPonderDo(scene, saPonder -> {
                        saPonder.getConstraintSolver().markConstraintRemoved(uuid);
                    });
                }
            });
        }

        public void playMotionForShip(UUID uuid, String modId, String motionName, double speed, double startFrame) {
            MotionRecord record = MotionRecord.loadMotion(modId, motionName);
            record.setCurrentFrame(startFrame);
            if (record.isEmpty()) {
                EzDebug.warn("get empty motion");
                return;
            }

            addInstruction(new PonderInstruction() {
                boolean completed = false;
                @Override
                public boolean isComplete() { return completed; }

                @Override
                public void tick(PonderScene scene) {
                    if (!completed) {
                        withPonderShipDo(scene, uuid, (p, s) -> {
                            completed = !record.play(s.getRigidbody().getDataWriter(), speed);
                        });
                    }
                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    record.setCurrentFrame(startFrame);
                    completed = false;
                }
            });
        }

        /*public void spawnShip(SandBoxPonderShip ship) {
            addInstruction(new PonderInstruction() {
                private boolean spawned = false;

                @Override
                public boolean isComplete() {
                    return spawned;
                }

                @Override
                public void tick(PonderScene scene) {
                    if (spawned)
                        return;

                    withSandBoxPonderDo(scene, p -> {
                        p.addShip(ship);
                        ship.pushState();
                        spawned = true;
                    });
                }

                @Override
                public void reset(PonderScene scene) {
                    withSandBoxPonderDo(scene, p -> {
                        p.markShipDeleted(ship.getUuid());
                        ship.popState();
                        spawned = false;
                    });
                }
            });
        }*/

        public void setShipHideState(UUID uuid, boolean hide) {
            addInstruction(new PonderInstruction() {
                private boolean completed = false;

                @Override
                public boolean isComplete() { return completed; }

                @Override
                public void tick(PonderScene scene) {
                    if (completed)
                        return;

                    withSandBoxPonderDo(scene, p -> {
                        //ship.pushState();
                        //p.markShipDeleted(ship.getUuid());
                        p.setShipHideState(uuid, hide);
                        completed = true;
                    });
                }

                @Override
                public void reset(PonderScene scene) { completed = false; }

                //no need to reset, makeShip will handle everything
                /*@Override
                public void reset(PonderScene scene) {
                    withSandBoxPonderDo(scene, p -> {
                        //ship.popState();
                        //p.addShip(ship);
                        completed = false;
                    });
                }*/
            });
        }

        public void hideShip(UUID uuid) { setShipHideState(uuid, true); }
        public void showShip(UUID uuid) { setShipHideState(uuid, false); }


        public void modifyShip(UUID uuid, Consumer<SandBoxPonderShip> consumer) {
            addInstruction(new PonderInstruction() {
                private boolean modified = false;
                private boolean exception = false;

                @Override
                public boolean isComplete() {
                    return modified || exception;
                }

                @Override
                public void tick(PonderScene scene) {
                    if (modified)
                        return;

                    withSandBoxPonderDo(scene, p -> {
                        SandBoxPonderShip ship = p.getShip(uuid);
                        if (ship == null) {
                            EzDebug.warn("get null ship by uuid:" + uuid);
                            exception = true;
                            return;
                        }

                        //ship.pushState();
                        consumer.accept(ship);
                        modified = true;
                    });
                }
                @Override
                public void reset(PonderScene scene) { modified = false; exception = false; }

                //make ship will handle everything
                /*@Override
                public void reset(PonderScene scene) {
                    withSandBoxPonderDo(scene, p -> {
                        SandBoxPonderShip ship = p.getShip(uuid);
                        if (ship != null) {
                            ship.popState();
                        } else {
                            EzDebug.warn("get null ship by uuid:" + uuid);
                        }

                        modified = false;
                        exception = false;
                    });
                }*/
            });
        }


        public void setShipPos(UUID uuid, Vector3dc newPos) {
            Vector3d newPosImm = new Vector3d(newPos);
            //state is immutable, no need to copy
            modifyShip(uuid, s -> {
                s.getRigidbody().getDataWriter().setPosition(newPos);
            });
        }
        public void setShipBlock(UUID uuid, Vector3ic localPos, BlockState state) {
            Vector3i localPosImm = new Vector3i(localPos);
            //state is immutable, no need to copy
            modifyShip(uuid, s -> {
                s.getBlockCluster().getDataWriter()
                    .setBlock(localPosImm, state, false);
            });
        }
        public void modifyShipBlock(UUID uuid, Vector3ic localPos, UnaryOperator<BlockState> state) {
            Vector3i localPosImm = new Vector3i(localPos);
            //state is immutable, no need to copy
            modifyShip(uuid, s -> {
                BlockState prev = s.getBlockCluster().getDataReader().getBlockState(localPosImm);
                s.getBlockCluster().getDataWriter().setBlock(localPosImm, state.apply(prev), false);
            });
        }

        public TweenBuilder tweenShip(UUID uuid, BiConsumer<SandBoxPonderShip, Double> tweener, int ticks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            addInstruction(new TweenInstruction(false, ticks) {
                @Override
                public void tween(PonderScene scene, double t01) {
                    withPonderShipDo(scene, uuid, (p, s) -> {
                        double curve01 = curve.get().evaluate(t01);
                        tweener.accept(s, curve01);
                    });
                }
            });
            return new TweenBuilder(curve::set, curve::get);
        }
        public TweenBuilder tweenShip(UUID uuid, TweenData.TweenFunction inTween, ITransformPrimitive from, ITransformPrimitive to, int ticks) {
            final TweenData.TweenFunction tween = inTween;
            final AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);

            TransformPrimitive fromImm = new TransformPrimitive(from);
            TransformPrimitive toImm = new TransformPrimitive(to);

            addInstruction(new TickingInstruction(false, ticks) {
                /*@Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    withSandBoxPonderDo(scene, p -> {
                        SandBoxPonderShip ship = p.getShip(uuid);
                        if (ship == null) {
                            EzDebug.warn("fail to get ship with uuid:" + uuid);
                            return;
                        }
                        ship.popState();
                    });
                }*/
                /*@Override
                protected void firstTick(PonderScene scene) {
                    withSandBoxPonderDo(scene, p -> {
                        SandBoxPonderShip ship = p.getShip(uuid);
                        if (ship == null) {
                            EzDebug.warn("fail to get ship with uuid:" + uuid);
                            return;
                        }
                        ship.pushState();
                    });
                }*/

                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);

                    withSandBoxPonderDo(scene, p -> {
                        SandBoxPonderShip ship = p.getShip(uuid);
                        if (ship == null) {
                            EzDebug.warn("fail to get ship with uuid:" + uuid);
                            return;
                        }

                        double t01 = 1.0 - ((double)remainingTicks / ticks);
                        //double lastT01 = 1.0 - (Math.min(remainingTicks + 1, ticks) / (double)ticks);
                        double curvedT01 = curve.get().evaluate(t01);
                        //double lastCurvedT01 = curve.get().evaluate(lastT01);

                        TransformPrimitive prev = ship.getRigidbody().getDataReader().getTransform().copy();
                        TransformPrimitive post = tween.getNextTransform(prev, fromImm, toImm, curvedT01);//tween.getNextTransform(prev, curvedT01, curvedT01 - lastCurvedT01);
                        ship.getRigidbody().getDataWriter().setTransform(post);
                    });

                }

                @Override
                public boolean isComplete() {
                    return super.isComplete();
                }
            });

            return new TweenBuilder(curve::set, curve::get);
        }

        public TweenBuilder tweenShipScale(UUID uuid, Vector3dc from, Vector3dc to, int ticks) {
            Vector3d fromImm = new Vector3d(from);
            Vector3d toImm = new Vector3d(to);
            return tweenShip(
                uuid,
                TweenData.TweenFunction.Scale,
                new TransformPrimitive().setScale(fromImm),  //since TweenFunction.Scale only set scale, no worry about other component
                new TransformPrimitive().setScale(toImm),
                ticks
            );
        }
        public TweenBuilder tweenShipPosition(UUID uuid, Vector3dc from, Vector3dc to, int ticks) {
            Vector3d fromImm = new Vector3d(from);
            Vector3d toImm = new Vector3d(to);
            return tweenShip(
                uuid,
                TweenData.TweenFunction.Position,
                new TransformPrimitive().setPosition(fromImm),  //since TweenFunction.setPosition only set position, no worry about other component
                new TransformPrimitive().setPosition(toImm),
                ticks
            );
        }
        public TweenBuilder tweenShipRotation(UUID uuid, Quaterniondc from, Quaterniondc to, int ticks) {
            Quaterniond fromImm = new Quaterniond(from);
            Quaterniond toImm = new Quaterniond(to);
            return tweenShip(
                uuid,
                TweenData.TweenFunction.Rotation,
                new TransformPrimitive().setRotation(fromImm),  //since TweenFunction.setRotation only set rotation, no worry about other component
                new TransformPrimitive().setRotation(toImm),
                ticks
            );
        }

        /*public int gravitySimulate(UUID uuid, Vector3dc gravity, double height, int bounce, double resiliency) {
            if (bounce < 0) {
                throw new InvalidParameterException("Bounce must >= 0");
            }
            double gravityMag = gravity.length();
            if (gravityMag < 1E-4) {
                throw new InvalidParameterException("Gravity Mag is too small");
            }

            double curDist = height;
            double totalTime = 0;
            for (int i = 0; i <= bounce; ++i) {
                double curTime = Math.sqrt(2 * curDist / gravityMag);
                totalTime += curTime;
                curDist *= resiliency;
            }


            int totalTicks = (int)(totalTime / 0.05);

            TweenData.TweenFunction tween = (prev, t01, step01) -> {
                double curTime =

                double stepTime = step01 * ticks * 0.05;
                return prev.copy().translate(gravity.mul(stepTime, new Vector3d()));
            };

            tweenShip(uuid, tween, ticks)
                .curve(TweenData.Curve.OutBounce);
        }*/
        /*public void gravitySimulate(UUID uuid, Vector3dc gravity, int ticks, boolean setZeroVelAtEnd) {
            addInstruction(new TickingInstruction(false, ticks) {
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);

                    withPonderShipDo(scene, uuid, (p, s) -> {
                        var rigidWriter = s.getRigidbody().getDataWriter();

                        if (super.remainingTicks == 0) {
                            if (setZeroVelAtEnd)
                                rigidWriter.setVelocity(0, 0, 0);
                            return;
                        }

                        rigidWriter.updateVelocity(v -> v.add(
                            gravity.mul(0.05, new Vector3d()),
                            new Vector3d()
                        ));
                    });
                }
            });
        }*/
        public int gravitySimulateForHeight(UUID uuid, Vector3dc gravity, double height, boolean setZeroVelAtEnd) {
            if (height < 0) {
                EzDebug.warn("height is negative! The height suppose be a positive number, will negate it.");
                height = -height;
            }
            double finialHeight = height;

            Vector3d finalGravity = new Vector3d(gravity);
            double gravityMag = gravity.length();
            if (gravityMag < 1E-4) {
                EzDebug.warn("gravity length is too small! will set earth gravity.");
                finalGravity.set(0, -9.8, 0);
            }

            int totalTicks = (int)(Math.sqrt(2 * finialHeight / gravityMag) * 20);
            if (totalTicks <= 0) {
                EzDebug.warn("totalTicks is " + totalTicks + " which is no need for a simulation!");
                return 0;
            }

            addInstruction(new TickingInstruction(false, totalTicks) {
                private Vector3d initialPos = null;

                @Override
                protected void firstTick(PonderScene scene) {
                    withPonderShipDo(scene, uuid, (p, s) -> {
                        initialPos = s.getRigidbody().getDataReader().getPosition().get(new Vector3d());
                    });
                }

                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);

                    withPonderShipDo(scene, uuid, (p, s) -> {
                        var rigidWriter = s.getRigidbody().getDataWriter();

                        if (super.remainingTicks == 0) {
                            if (setZeroVelAtEnd)
                                rigidWriter.setVelocity(0, 0, 0);

                            if (initialPos == null) {
                                EzDebug.warn("get null initial pos! won't relocate!");
                            } else {
                                rigidWriter.setPosition(
                                    finalGravity.normalize(finialHeight, new Vector3d())
                                        .add(initialPos)
                                );
                            }
                            return;
                        }

                        rigidWriter.updateVelocity(v -> v.add(
                            gravity.mul(0.05, new Vector3d()),
                            new Vector3d()
                        ));
                    });
                }
            });
            //gravitySimulate(uuid, finalGravity, totalTicks, setZeroVelAtEnd);
            return totalTicks;
        }

        private void increaseDestroyBlockProgressAction(SandBoxPonderShip ship, Vector3ic localPos, boolean canBreak, @Nullable Consumer<BlockState> breakCallback) {
            ship.addBreakProgress(localPos, canBreak, state -> {
                Vector3d worldCenter = ship.getRigidbody().getDataReader().localIToWorldPos(localPos);
                getPonderScene().getWorld().addBlockDestroyEffects(JomlUtil.bpContaining(worldCenter), state);

                if (breakCallback != null)
                    breakCallback.accept(state);
            });
        }

        public void increaseDestroyBlockProgress(UUID uuid, Vector3ic localPos, boolean canBreak, @Nullable Consumer<BlockState> breakCallback) {
            Vector3i breakPos = new Vector3i(localPos);
            modifyShip(uuid, s -> increaseDestroyBlockProgressAction(s, localPos, canBreak, breakCallback));
        }
        public void increaseDestroyBlocksProgress(UUID uuid, Function<SandBoxPonderShip, List<Vector3ic>> rangeGetter, boolean canBreak, @Nullable Consumer<BlockState> breakCallback) {
            modifyShip(uuid, s -> {
                List<Vector3ic> breakPoses = rangeGetter.apply(s);
                if (breakPoses == null) {
                    EzDebug.warn("get null breakPoses");
                    return;
                }

                for (Vector3ic breakPos : breakPoses)
                    increaseDestroyBlockProgressAction(s, breakPos, canBreak, breakCallback);
            });
        }

        public int destroyBlocksUtilBreak(UUID uuid, Function<SandBoxPonderShip, List<Vector3ic>> rangeGetter, int intervalTick, @Nullable Consumer<BlockState> breakCallback) {
            int totalTicks = intervalTick * 10;

            addInstruction(new TickingInstruction(false, totalTicks) {
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    int passedTick = totalTicks - remainingTicks - 1;

                    if (passedTick % intervalTick == 0) {
                        withPonderShipDo(scene, uuid, (world, ship) -> {
                            List<Vector3ic> breakPoses = rangeGetter.apply(ship);
                            if (breakPoses == null) {
                                EzDebug.warn("get null breakPoses");
                                return;
                            }

                            for (Vector3ic breakPos : breakPoses)
                                increaseDestroyBlockProgressAction(ship, breakPos, true, breakCallback);
                        });
                    }
                }
            });

            return totalTicks;
        }


        public void setPhysTimeScale(double scale) {
            addInstruction(new PonderInstruction() {
                private boolean set = false;
                @Override
                public boolean isComplete() { return set; }

                @Override
                public void tick(PonderScene scene) {
                    set = true;
                    withSandBoxPonderDo(scene, p -> p.setPhysTimeScale(scale));
                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    set = false;
                    withSandBoxPonderDo(scene, SandBoxPonderWorld::resetPhysTimeScale);
                }
            });
        }
        public TweenBuilder tweenPhysTimeScale(double from, double to, int ticks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            addInstruction(new TickingInstruction(false, ticks) {
                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    withSandBoxPonderDo(scene, SandBoxPonderWorld::resetPhysTimeScale);
                }

                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    withSandBoxPonderDo(scene, p -> {
                        double t01 = 1.0 - (double)remainingTicks / totalTicks;
                        p.setPhysTimeScale((to - from) * curve.get().evaluate(t01) + from);
                    });
                }
            });
            return new TweenBuilder(curve::set, curve::get);
        }

        public void toggleShipRedstonePower(UUID uuid, Vector3ic localPos) {
            Vector3i localPosImm = new Vector3i(localPos);
            modifyShipBlock(uuid, localPosImm, s -> {
                if (s.hasProperty(BlockStateProperties.POWER)) {
                    s = s.setValue(BlockStateProperties.POWER, (Integer)s.getValue(BlockStateProperties.POWER) == 0 ? 15 : 0);
                }

                if (s.hasProperty(BlockStateProperties.POWERED)) {
                    s = s.cycle(BlockStateProperties.POWERED);
                }

                if (s.hasProperty(RedstoneTorchBlock.LIT)) {
                    s = s.cycle(RedstoneTorchBlock.LIT);
                }
                return s;
            });
        }

        public void delayModifyShip(int delayTicks, UUID uuid, Consumer<SandBoxPonderShip> consumer) {
            addInstruction(new TickingInstruction(false, delayTicks) {
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    if (remainingTicks == 0)
                        withPonderShipDo(scene, uuid, (p, s) -> {
                            consumer.accept(s);
                        });
                }
            });
        }
    }

    public class EnhancedSceneInstructions {
        public void rotateCameraX(float degree) {
            addInstruction(new PonderInstruction() {
                @Override
                public boolean isComplete() { return true; }
                @Override
                public void tick(PonderScene scene) {
                    PonderScene.SceneTransform transform = scene.getTransform();
                    transform.xRotation.chase(transform.xRotation.getChaseTarget() + degree, 0.10000000149011612d, LerpedFloat.Chaser.EXP);
                }
            });
        }
        public void setCameraRotateX(float degree) {
            addInstruction(new PonderInstruction() {
                @Override
                public boolean isComplete() { return true; }
                @Override
                public void tick(PonderScene scene) {
                    PonderScene.SceneTransform transform = scene.getTransform();
                    transform.xRotation.chase(degree, 0.10000000149011612d, LerpedFloat.Chaser.EXP);
                }
            });
        }
        public void setCameraRotateY(float degree) {
            addInstruction(new PonderInstruction() {
                @Override
                public boolean isComplete() { return true; }
                @Override
                public void tick(PonderScene scene) {
                    PonderScene.SceneTransform transform = scene.getTransform();
                    //float targetX = this.relative ? transform.xRotation.getChaseTarget() + this.xRot : this.xRot;
                    //transform.xRotation.chase(targetX, 0.10000000149011612d, LerpedFloat.Chaser.EXP);
                    transform.yRotation.chase(degree, 0.10000000149011612d, LerpedFloat.Chaser.EXP);
                }
            });
        }
        public void resetCameraRotation() {
            addInstruction(new PonderInstruction() {
                @Override
                public boolean isComplete() { return true; }
                @Override
                public void tick(PonderScene scene) {
                    PonderScene.SceneTransform transform = scene.getTransform();
                    transform.xRotation.chase(-35.0, 0.10000000149011612d, LerpedFloat.Chaser.EXP);
                    transform.yRotation.chase(145.0, 0.10000000149011612d, LerpedFloat.Chaser.EXP);
                }
            });
        }

        //todo replace all imm to not imm
        /*public void keepInterestOnShip(UUID uuid, Vector3dc localPos, int ticks, boolean doReturn) {
            addInstruction(new TickingInstruction(false, ticks) {
                Vec3 startInterestPos;

                @Override
                protected void firstTick(PonderScene scene) {
                    super.firstTick(scene);
                    startInterestPos = scene.getPointOfInterest();
                }
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);

                    if (remainingTicks == 0 && doReturn) {
                        scene.setPointOfInterest(startInterestPos);
                        return;
                    }

                    withPonderShipDo(scene, uuid, (p, s) -> {
                        Vec3 worldInterestPos = JomlUtil.v3(s.getRigidbody().getDataReader().localToWorldPos(localPos, new Vector3d()));
                        scene.setPointOfInterest(worldInterestPos);
                    });
                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    scene.setPointOfInterest(new Vec3(0.0d, 4.0d, 0.0d));
                }
            });
        }*/
        public void focusOn(Vector3dc pos, int ticks, boolean doReturn) {
            focusOn((float)pos.x(), (float)pos.y(), (float)pos.z(), ticks, doReturn);
        }
        public void focusOn(float x, float y, float z, int ticks, boolean doReturn) {
            addInstruction(new TickingInstruction(false, ticks) {
                private final Matrix4f prevMatrix = new Matrix4f();
                @Override
                protected void firstTick(PonderScene scene) {
                    super.firstTick(scene);
                    prevMatrix.set(((HookedPonderScene)scene).getOverSceneTarget());
                }
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    if (remainingTicks == 0 && doReturn) {
                        ((HookedPonderScene)scene).setOverSceneTarget(prevMatrix);
                        return;
                    }
                    ((HookedPonderScene)scene).setOverSceneTarget(
                        new Matrix4f(prevMatrix).translation(-x, -y + 4, -z)
                    );
                }
                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    ((HookedPonderScene)scene).setOverSceneTarget(new Matrix4f());
                }
            });
        }
        public void focusOnShip(UUID uuid, Vector3dc localPos, int ticks, boolean doReturn) {
            addInstruction(new TickingInstruction(false, ticks) {
                private final Matrix4f prevMatrix = new Matrix4f();
                @Override
                protected void firstTick(PonderScene scene) {
                    super.firstTick(scene);
                    prevMatrix.set(((HookedPonderScene)scene).getOverSceneTarget());
                }
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    if (remainingTicks == 0 && doReturn) {
                        ((HookedPonderScene)scene).setOverSceneTarget(prevMatrix);
                        return;
                    }
                    withPonderShipDo(scene, uuid, (p, s) -> {
                        Matrix4f target = new Matrix4f(prevMatrix).translation(
                            s.getRigidbody().getDataReader().localToWorldPos(localPos, new Vector3d()).get(new Vector3f()).negate().add(0, 4, 0)
                        );

                        EzDebug.log("target xyz:" + target.getTranslation(new Vector3f()));

                        ((HookedPonderScene)scene).setOverSceneTarget(target
                            //new Matrix4f(
                            //    s.getRigidbody().getDataReader().getPosition().add(0, 4, 0, new Vector3d())
                            //)

                        );
                    });
                }
                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    ((HookedPonderScene)scene).setOverSceneTarget(new Matrix4f());
                }
            });
        }
        public TweenBuilder focusTweenOnShip(UUID uuid, Vector3dc localPos, int ticks, boolean doReturn) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.One);
            addInstruction(new TickingInstruction(false, ticks) {
                private final Matrix4f prevMatrix = new Matrix4f();
                private final Vector3f prevTranslation = new Vector3f();
                @Override
                protected void firstTick(PonderScene scene) {
                    super.firstTick(scene);
                    prevMatrix.set(((HookedPonderScene)scene).getPrevOverScene());
                    prevMatrix.getTranslation(prevTranslation);
                }
                @Override
                public void tick(PonderScene scene) {
                    //debug
                    /*((IExOutliner)scene.getOutliner()).addWithoutChase("Debug1", new AABBOutline(
                        JomlUtil.centerExtended(
                            prevMatrix.getTranslation(new Vector3f()).get(new Vector3d()),
                            0.5
                        )
                    )).colored(PonderPalette.INPUT.getColor());*/

                    withPonderShipDo(scene, uuid, (p, s) -> {
                        float curve01 = (float)curve.get().evaluate(1.0 - (double)remainingTicks / totalTicks);
                        Vector3f terminalTranslation = s.getRigidbody().getDataReader().localToWorldPos(localPos, new Vector3d()).get(new Vector3f())
                            .add(0, 4, 0);

                        Matrix4f terminal = new Matrix4f(prevMatrix).translation(terminalTranslation);
                        Matrix4f target = new Matrix4f(prevMatrix).lerp(terminal, curve01);
                        ((HookedPonderScene)scene).setOverSceneTarget(target);

                        /*((IExOutliner)scene.getOutliner()).addWithoutChase("Debug2", new AABBOutline(
                            JomlUtil.centerExtended(
                                focusOn.get(new Vector3d()),
                                0.5
                            )
                        )).colored(PonderPalette.OUTPUT.getColor());*/
                    });

                    super.tick(scene);
                    if (remainingTicks == 0 && doReturn) {
                        ((HookedPonderScene)scene).setOverSceneTarget(prevMatrix);
                        return;
                    }
                }
                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    ((HookedPonderScene)scene).setOverSceneTarget(new Matrix4f());
                }
            });
            return new TweenBuilder(curve::set, curve::get);
        }

        public void switchToLocalSpaceOf(UUID uuid, int ticks, boolean doReturn) {
            addInstruction(new TickingInstruction(false, ticks) {
                private final Matrix4f prevMatrix = new Matrix4f();
                @Override
                protected void firstTick(PonderScene scene) {
                    super.firstTick(scene);
                    prevMatrix.set(((HookedPonderScene)scene).getOverSceneTarget());
                }
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    if (remainingTicks == 0 && doReturn) {
                        ((HookedPonderScene)scene).setOverSceneTarget(prevMatrix);
                        return;
                    }
                    withPonderShipDo(scene, uuid, (p, s) -> {
                        ((HookedPonderScene)scene).setOverSceneTarget(new Matrix4f(s.getRigidbody().getDataReader().getWorldToLocal()));
                        //Vector3f focusOn = s.getRigidbody().getDataReader().localToWorldPos(localPos, new Vector3d()).get(new Vector3f());
                        //((HookedPonderScene)scene).setOverSceneTarget(new Matrix4f(prevMatrix).translation(-focusOn.x, -focusOn.y, -focusOn.z));
                    });
                }
                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    ((HookedPonderScene)scene).setOverSceneTarget(new Matrix4f());
                }
            });
        }

        public TweenBuilder tweenSceneView(float from, float to, int ticks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            addInstruction(new TweenInstruction(false, ticks) {
                @Override
                public void tween(PonderScene scene, double t01) {
                    double curve01 = curve.get().evaluate(t01);
                    float curScale = (float)((to - from) * curve01 + from);

                    /*HookedPonderScene hooked = (HookedPonderScene)scene;
                    hooked.setOverSceneTarget(
                        new Matrix4f(hooked.getOverSceneTarget()).scaling(
                            new Vector3f(curScale, curScale, curScale)
                        )
                    );*/
                    origin.scaleSceneView(curScale);
                }
            });
            return new TweenBuilder(curve::set, curve::get);
        }

        public TweenBuilder tween(BiConsumer<PonderScene, Double> tweener, int ticks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            addInstruction(new TweenInstruction(false, ticks) {
                @Override
                public void tween(PonderScene scene, double t01) {
                    tweener.accept(scene, curve.get().evaluate(t01));
                }
            });
            return new TweenBuilder(curve::set, curve::get);
        }
    }

    //todo fill all origin world method to it....
    public class EnhancedWorldInstructions {
        public static class CallbackBuilder {
            private final List<Runnable> runnables = new ArrayList<>();
            public void on(@NotNull Runnable runnable) {
                runnables.add(runnable);
            }

            private void invoke() {
                runnables.forEach(Runnable::run);
            }
        }
        public void setNoGravity(ElementLink<EntityElement> link, boolean noGravity) {
            addInstruction(new PonderInstruction() {
                private boolean tried = false;
                @Override
                public boolean isComplete() {
                    return tried;
                }

                @Override
                public void tick(PonderScene scene) {
                    if (tried)
                        return;
                    tried = true;

                    scene.resolve(link).ifPresent(x -> {
                        x.setNoGravity(noGravity);
                    });
                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    tried = false;
                }
            });
        }
        public void throwEntityTo(ElementLink<EntityElement> link, Vector3dc to, @Nullable BiConsumer<PonderScene, Entity> arriveCallback) {
            int preTicks = 4;
            CallbackBuilder cb = new CallbackBuilder();
            Vector3d toPos = new Vector3d(to);
            //AtomicInteger needTicks = new AtomicInteger(0);

            addInstruction(new PonderInstruction() {
                private boolean tried = false;
                private int remainTicks = 0;

                @Override
                public boolean isComplete() { return remainTicks <= 0; }

                @Override
                public void tick(PonderScene scene) {
                    if (tried) {
                        if (remainTicks > 0) {
                            remainTicks--;
                            if (remainTicks == 0) {
                                cb.invoke();
                                //if (discardWhenArrive)
                                //    scene.resolve(link).ifPresent(Entity::discard);
                                if (arriveCallback != null)
                                    scene.resolve(link).ifPresent(x -> arriveCallback.accept(scene, x));
                            }
                        }
                        return;
                    }

                    tried = true;

                    scene.resolve(link).ifPresent(x -> {
                        Vector3d fromPos = JomlUtil.d(x.getPosition(1f));
                        Vector3d diff = toPos.sub(fromPos, new Vector3d());

                        if (diff.y <= 0) {  //drop
                            double time = Math.sqrt(2 * Math.abs(diff.y) / 9.8);
                            int ticks = (int)(time * 20);
                            x.setDeltaMovement(diff.x / ticks, 0, diff.z / ticks);
                            remainTicks = ticks;
                        } else {  //throw
                            double time = Math.sqrt(2 * diff.y / 9.8);
                            int ticks = (int)(time * 20);
                            x.setDeltaMovement(diff.x / ticks, 9.8 * time * 0.05, diff.z / ticks);
                            remainTicks = ticks;
                        }
                        remainTicks -= preTicks;
                    });
                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    tried = false;
                    remainTicks = 0;
                }
            });
        }

        public Terminator moveEntityToAndFloat(ElementLink<EntityElement> link, Vector3dc to, int ticks, TweenData.Curve inCurve) {
            Vector3d toPos = new Vector3d(to);
            //CallbackBuilder cb = new CallbackBuilder();
            //AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            AtomicBoolean terminated = new AtomicBoolean(false);
            Terminator terminator = new Terminator(() -> terminated.set(true));

            addInstruction(new TickingInstruction(false, ticks) {
                private final Vector3d initialPos = new Vector3d(0, 0, 0);  //for safe, will have initial value (0, 0, 0)
                private boolean arrived = false;
                private boolean lost = false;

                @Override
                public boolean isComplete() {
                    return lost || terminated.get();
                }

                @Override
                protected void firstTick(PonderScene scene) {
                    super.firstTick(scene);
                    scene.resolve(link).ifPresent(x -> JomlUtil.dSet(initialPos, x.position()));
                }
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    scene.resolve(link).ifPresent(x -> {
                        if (remainingTicks == 0 || arrived) {
                            arrived = true;
                            x.setPos(toPos.x, toPos.y, toPos.z);
                            x.setDeltaMovement(0, 0, 0);
                            return;
                        }

                        double t01 = 1.0 - ((double)remainingTicks / totalTicks);
                        Vector3d curPos = JomlUtil.dLerp(initialPos, toPos, inCurve.evaluate(t01), new Vector3d());

                        x.setPos(curPos.x, curPos.y, curPos.z);
                        x.setDeltaMovement(0, 0, 0);
                    });
                }
            });
            //return cb;
            //return new VsInstructions.TweenBuilder(curve);
            return terminator;
        }

        public static class Terminator {
            private final Runnable onTerminated;
            private Terminator(@NotNull Runnable inOnTerminated) {
                onTerminated = inOnTerminated;
            }
            public void terminate() {
                onTerminated.run();
            }
        }

        public void unlimitedModifyBlock(Selection section, BiFunction<BlockPos, BlockState, BlockState> stater, Predicate<BlockState> particlePredicator) {
            addInstruction(new UnLimitedWorldModifyInstruction(
                section, stater, particlePredicator
            ));
        }
        public void unlimitedModifyBlock(HashMap<BlockPos, BlockState> blocks, Predicate<BlockState> particlePredicator) {
            if (blocks.isEmpty())
                return;

            //get any one
            BlockPos bp = blocks.keySet().stream().findFirst().get();
            final Selection[] section = {Selection.of(BoundingBox.fromCorners(bp, bp))};

            blocks.keySet().forEach(x ->
                section[0] = section[0].add(
                    Selection.of(
                        BoundingBox.fromCorners(x, x)
                    )
                )
            );

            addInstruction(new UnLimitedWorldModifyInstruction(
                section[0], (curBp, prev) -> blocks.get(curBp), particlePredicator
            ));
        }
    }

    public class EnhancedOverlayInstructions {
        public TweenBuilder showAndTweenInputWindowElement(InputWindowElement control, Vec3 from, Vec3 to, int tweenTicks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            var instruction = new TickingInstruction(false, tweenTicks) {
                @Override
                public void tick(PonderScene scene) {
                    double t01 = 1.0 - (double)(remainingTicks) / tweenTicks;
                    Vec3 cur = from.lerp(to, curve.get().evaluate(t01));
                    ((InputWindowElementAccessor)control).setSceneSpace(cur);
                }
            };
            addInstruction(instruction);

            return new TweenBuilder(curve::set, curve::get);
        }
        public TweenBuilder showAndTweenOutline(Object slot, AABB from, AABB to, PonderPalette color, int tweenTicks, int keepTicks) {
            return showAndTweenOutline(slot, from, to, color, 0.0625f, tweenTicks, keepTicks);
        }
        public TweenBuilder showAndTweenOutline(Object slot, AABB from, AABB to, PonderPalette color, float lineWidth, int tweenTicks, int keepTicks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            addInstruction(new TickingInstruction(false, tweenTicks + keepTicks) {
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);

                    if (remainingTicks > keepTicks) {  //during tween
                        double t01 = 1.0 - (double)(remainingTicks - keepTicks) / tweenTicks;
                        AABB cur = JomlUtil.lerpAABB(from, to, curve.get().evaluate(t01));
                        scene.getOutliner().chaseAABB(slot, cur).lineWidth(lineWidth).colored(color.getColor());
                    } else {
                        scene.getOutliner().chaseAABB(slot, to).lineWidth(lineWidth).colored(color.getColor());
                    }
                }
            });
            return new TweenBuilder(curve::set, curve::get);
        }
        public TweenBuilder tweenDirectionalForBlock(Object slot, BlockPos bp, Direction towards, PonderPalette color, int tweenTicks, int keepTicks) {
            return showAndTweenOutline(slot, util.block.blockFace(bp, towards.getOpposite()), util.block.whole(bp), color, tweenTicks, keepTicks);
        }

        public void outlineShipBlock(UUID uuid, Vector3ic localPos, int color, int ticks) {
            addInstruction(new TickingInstruction(false, ticks) {
                final UUID outlineUuid = UUID.randomUUID();
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);

                    withPonderShipDo(scene, uuid, (p, s) -> {
                        //Matrix4f localToWorld = new Matrix4f(s.getRigidbody().getDataReader().getLocalToWorld());
                        ((IExOutliner)scene.getOutliner()).addWithoutChase(
                                outlineUuid,
                                new InSpaceOBBOutline(  //todo make it final field
                                    JomlUtil.centerExtended(new Vector3d(localPos), 0.5),
                                    () -> s.getCurrentTransform().makeLocalToWorld(new Matrix4f())
                                )
                            ).colored(color)
                            .lineWidth(0.0625f);
                    });
                }
            });
        }

        public void outlineShipBlocks(UUID uuid, Collection<Vector3ic> localPoses, int color, int ticks) {
            addInstruction(new TickingInstruction(false, ticks) {
                final UUID outlineUuid = UUID.randomUUID();
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);


                    withPonderShipDo(scene, uuid, (p, s) -> {
                        AABBd aabb = new AABBd();
                        localPoses.forEach(pos -> {
                            aabb.union(JomlUtil.d(pos).add(-0.5, -0.5, -0.5));
                            aabb.union(JomlUtil.d(pos).add(0.5, 0.5, 0.5));
                        });
                        if (!aabb.isValid())
                            return;
                        //Matrix4f localToWorld = new Matrix4f(s.getRigidbody().getDataReader().getLocalToWorld());
                        ((IExOutliner)scene.getOutliner()).addWithoutChase(
                                outlineUuid,
                                new InSpaceOBBOutline(  //todo make it final field
                                    JomlUtil.aabb(aabb),
                                    () -> s.getCurrentTransform().makeLocalToWorld(new Matrix4f())
                                )
                            ).colored(color)
                            .lineWidth(0.0625f);
                    });
                }
            });
        }

        public TweenBuilder tweenOutlineInShip(UUID uuid, AABBdc fromLocal, AABBdc toLocal, int color, int tweenTicks, int keepTicks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            AABBd fromImm = new AABBd(fromLocal);
            AABBd toLocalImm = new AABBd(toLocal);
            addInstruction(new TickingInstruction(false, tweenTicks + keepTicks) {
                final UUID outlineUuid = UUID.randomUUID();
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    withPonderShipDo(scene, uuid, (p, s) -> {
                        int passedTicks = totalTicks - remainingTicks;
                        if (passedTicks > tweenTicks) {
                            ((IExOutliner)scene.getOutliner()).addWithoutChase(
                                outlineUuid,
                                new InSpaceOBBOutline(
                                    JomlUtil.aabb(toLocalImm),
                                    () -> s.getCurrentTransform().makeLocalToWorld(new Matrix4f())
                                )
                            ).colored(color).lineWidth(0.0625f);
                            return;
                        }

                        double curve01 = curve.get().evaluate((double)passedTicks / tweenTicks);
                        //AABBd cur = JomlUtil.lerpAABBd(fromImm, toLocalImm, curve01, new AABBd());
                        ((IExOutliner)scene.getOutliner()).chaseOrAdd(
                            outlineUuid,
                            new OBBOutline(
                                JomlUtil.aabb(JomlUtil.lerpAABBd(fromImm, toLocalImm, curve01, new AABBd())),
                                s.getCurrentTransform().makeLocalToWorld(new Matrix4f())
                            )
                        ).colored(color).lineWidth(0.0625f);
                    });
                }
            });
            return new TweenBuilder(curve::set, curve::get);
        }
        public TweenBuilder tweenBlockInShip(UUID uuid, Vector3ic localPos, Direction localDir, int color, int tweenTicks, int keepTicks) {
            AtomicReference<TweenData.Curve> curve = new AtomicReference<>(TweenData.Curve.Linear);
            //Vector3i localPosImm = new Vector3i(localPos);
            AABBd from = util.block.shipBlockFace(localPos, localDir.getOpposite());
            AABBd to = JomlUtil.dCenterExtended(JomlUtil.d(localPos), 0.5);

            return tweenOutlineInShip(uuid, from, to, color, tweenTicks, keepTicks);
        }

        public void showShipBlockAndText(UUID uuid, Vector3ic localPos, PonderPalette color, int ticks) {
            origin.overlay.showText(ticks)
                .text("text placeholder")
                .colored(color)
                .attachKeyFrame()
                .placeNearTarget();
            outlineShipBlock(uuid, localPos, color.getColor(), ticks);
        }

        public TextWindowElement.Builder textPointToFocusRelative(Vec3 offset, int ticks) {
            return origin.overlay.showText(ticks)
                .pointAt(offset.add(offset.x(), offset.y() + 4, offset.x()));
        }
        public NoPointLineTextElement.Builder textPointToFocusRelativeNoLine(Vec3 offset, int ticks) {
            return addNoLineText(ticks)
                .pointAt(offset.add(offset.x(), offset.y() + 4, offset.z()));
        }

        public NoPointLineTextElement.Builder addNoLineText(int ticks) {
            NoPointLineTextElement text = new NoPointLineTextElement();
            addAnimatedElement(text, ticks);
            return text.new Builder(getPonderScene());
        }

        public void addAnimatedElement(AnimatedOverlayElement element, int ticks) {
            addInstruction(new FadeInOutInstruction(ticks) {
                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                }

                @Override
                protected void show(PonderScene scene) {
                    scene.addElement(element);
                    element.setVisible(true);
                }

                @Override
                protected void hide(PonderScene scene) { element.setVisible(false); }

                @Override
                protected void applyFade(PonderScene scene, float fade) {
                    element.setFade(fade);
                }
            });
            //SceneBuilder.this.addInstruction(new TextInstruction(textWindowElement, duration));
        }
    }

    public class EnhancedEffectInstructions {
        public void indicateShipRedstone(UUID uuid, Vector3ic localBp) {
            emitShipDustParticle(uuid, JomlUtil.dCenterExtended(JomlUtil.d(localBp), 0.5), 16711680, t01 -> new Vector3d(0, 0, 0), 10, 2);
        }

        public void emitShipDustParticle(UUID uuid, AABBdc localRange, int color, Function<Double, Vector3d> t01ToLocalVel, int totalAmount, int ticks) {
            ParticleOptions dust = new DustParticleOptions(new Color(color).asVectorF(), 1.0F);
            emitShipParticles(uuid, localRange, dust, t01ToLocalVel, totalAmount, ticks);
        }

        public <T extends ParticleOptions> void emitShipParticles(UUID uuid, AABBdc localRange, T data, Function<Double, Vector3d> t01ToLocalVel, int totalAmount, int ticks) {
            AABBdc localRangeImm = new AABBd(localRange);

            float amountPreRun = (float)totalAmount / ticks;
            addInstruction(new TickingInstruction(false, ticks) {
                double t01 = 0;
                float toGenerate = 0;
                int generated = 0;

                /*final EmitParticlesInstruction.Emitter emitter = (w, x, y, z) -> {
                    Vec3 dMotion = t01ToLocalDeltaMove.apply(t01);
                    w.addParticle(data, x, y, z, dMotion.x, dMotion.y, dMotion.z);
                };*/

                @Override
                public void tick(PonderScene scene) {
                    super.tick(scene);
                    t01 = 1.0 - (double)remainingTicks / ticks;
                    withPonderShipDo(scene, uuid, (p, s) -> {
                        Vector3d emitPos = s.getRigidbody().getDataReader().localToWorldPos(
                            RandUtil.nextPos(localRangeImm, new Vector3d())
                        );

                        toGenerate += amountPreRun;
                        while (generated < toGenerate) {
                            generated++;
                            //emitter.create(scene.getWorld(), emitPos.x, emitPos.y, emitPos.z);
                            Vector3d dmInWorld = s.getRigidbody().getDataReader().getRotation().transform(t01ToLocalVel.apply(t01)).mul(0.05);
                            scene.getWorld().addParticle(
                                data,
                                emitPos.x, emitPos.y, emitPos.z,
                                dmInWorld.x, dmInWorld.y, dmInWorld.z
                            );
                        }
                    });

                }

                @Override
                public void reset(PonderScene scene) {
                    super.reset(scene);
                    toGenerate = 0;
                    generated = 0;
                    t01 = 0;
                }
            });
        }
    }


    public class Util {
        public BlockUtil block = new BlockUtil();
        public ShipUtil ship = new ShipUtil();
        public TransformUtil transform = new TransformUtil();

        public void beltPlaceLikePlaceImmediately(PonderScene scene, BlockPos bp, ItemStack stack, Direction insertionSide) {
            PonderWorld world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(bp);
            if (blockEntity instanceof SmartBlockEntity beltLikeBe) {
                DirectBeltInputBehaviour behaviour = beltLikeBe.getBehaviour(DirectBeltInputBehaviour.TYPE);
                if (behaviour != null) {
                    behaviour.handleInsertion(stack, insertionSide.getOpposite(), false);
                }
            }
        }

        public class BlockUtil {
            public AABB blockFace(BlockPos bp, Direction face) {
                AABB blockBound = JomlUtil.boundBlock(bp);
                return switch (face) {
                    case UP -> blockBound.setMinY(blockBound.maxY);
                    case DOWN -> blockBound.setMaxY(blockBound.minY);

                    case SOUTH -> blockBound.setMinZ(blockBound.maxZ);
                    case NORTH -> blockBound.setMaxZ(blockBound.minZ);

                    case WEST -> blockBound.setMaxX(blockBound.minX);
                    case EAST -> blockBound.setMinX(blockBound.maxX);
                };
            }
            public AABBd shipBlockFace(Vector3ic localPos, Direction face) {
                AABBd blockBound = JomlUtil.dCenterExtended(JomlUtil.d(localPos), 0.5);
                switch (face) {
                    case UP -> blockBound.minY = blockBound.maxY;
                    case DOWN -> blockBound.maxY = blockBound.minY;

                    case SOUTH -> blockBound.minZ = blockBound.maxZ;
                    case NORTH -> blockBound.maxZ = blockBound.minZ;

                    case WEST -> blockBound.maxX = blockBound.minX;
                    case EAST -> blockBound.minX = blockBound.maxX;
                };
                return blockBound;
            }
            public AABB whole(BlockPos bp) {
                return new AABB(bp.getX(), bp.getY(), bp.getZ(), bp.getX() + 1, bp.getY() + 1, bp.getZ() + 1);
            }
            public AABB boundBetweenIncluding(BlockPos from, BlockPos to) {
                return new AABB(
                    from.getX(),
                    from.getY(),
                    from.getZ(),
                    to.getX() + 1,
                    to.getY() + 1,
                    to.getZ() + 1
                );
            }

            public Vec3 midBetween(BlockPos a, BlockPos b) {
                return a.getCenter().add(b.getCenter()).scale(0.5);
            }
        }
        public class ShipUtil {
            public BlockClusterData copyWorldSectionAsShipBlocks(Selection selection, Vector3ic origin) {
                BlockClusterData blockData = new BlockClusterData();
                selection.forEach(bp -> {  //todo also copy be when possible
                    BlockState state = getPonderScene().getWorld().getBlockState(bp);
                    if (state.isAir())
                        return;

                    blockData.setBlock(JomlUtil.i(bp).sub(origin), state);
                });
                return blockData;
            }
        }
        public class TransformUtil {
            public Quaterniond rotOfAngleAxisDeg(double deg, double x, double y, double z) {
                return rotOfAngleAxisRad(Math.toRadians(deg), x, y, z);
            }
            public Quaterniond rotOfAngleAxisRad(double rad, double x, double y, double z) {
                /*return Optional.ofNullable(getPonderScene())
                    .map(s -> {
                        float pt = AnimationTickHolder.getPartialTicks(s.getWorld());
                        PonderScene.SceneTransform sceneTrans = s.getTransform();

                        Quaterniond baseRot = new Quaterniond().rotateX(sceneTrans.xRotation.getValue(pt)).rotateY(sceneTrans.yRotation.getValue(pt));
                        Quaterniond rot = new Quaterniond(new AxisAngle4d(rad, x, y, z));

                        return baseRot.mul(rot);
                    })
                    .orElse(new Quaterniond());*/
                return new Quaterniond(new AxisAngle4d(rad, x, y, z));
            }

            public Vector3d rotateAround(Vector3dc pos, Vector3dc around, Quaterniondc rotation, Vector3d dest) {
                Vector3d offset = pos.sub(around, new Vector3d());
                Vector3d rotatedOffset = rotation.transform(offset);
                return dest.set(pos).add(rotatedOffset);
            }
        }
        public class InstructionUtil {

        }
    }
}
