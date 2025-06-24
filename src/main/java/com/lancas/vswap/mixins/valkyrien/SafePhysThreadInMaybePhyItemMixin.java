package com.lancas.vswap.mixins.valkyrien;

import com.lancas.vswap.WapConfig;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixinfriend.ManualRemapPhysItem;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import org.joml.Matrix3dc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.api.ships.WingManager;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.core.impl.game.ships.WingManagerImpl;
import org.valkyrienskies.core.impl.game.ships.WingPhysicsSolver;
import org.valkyrienskies.core.impl.shadow.Aj;
import org.valkyrienskies.core.impl.shadow.Aq;
import org.valkyrienskies.core.impl.shadow.Ao;
import org.valkyrienskies.physics_api.PoseVel;


import java.lang.reflect.Constructor;

@Mixin(Aq.class)
public abstract class SafePhysThreadInMaybePhyItemMixin implements ManualRemapPhysItem {
    //@Unique private AtomicBoolean handlingPhysFrame = new AtomicBoolean(false);

    @Shadow(remap = false) @Final public abstract Ao a(Vector3dc vector3dc, double d2, boolean z2);
    @Shadow(remap = false) @Final protected abstract void b(Aj aj);
    @Shadow(remap = false) @Final protected abstract Ao h();
    @Shadow(remap = false) @Final protected abstract Object a(Vector3dc v, double d2, boolean z2, Continuation<? super Unit> continuation);


    @Unique protected Constructor<?> eConstructior = null;




    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;setPoseVel(Lorg/valkyrienskies/physics_api/PoseVel;)V"
        ),
        remap = false
    )
    private void safeSetPoseVel(PhysShipImpl ship, PoseVel newPoseVel) {
        if (!WapConfig.vsPhysSafeThread) {
            ship.setPoseVel(newPoseVel);
        } else {
            try {
                if (newPoseVel.component1().isFinite() && newPoseVel.component2().isFinite() && newPoseVel.component3().isFinite() && newPoseVel.component4().isFinite()) {
                    ship.setPoseVel(newPoseVel);
                } else {
                    EzDebug.warn("Skip assign invalid PoseVel：" + newPoseVel);
                }
            } catch (Exception e) {
                EzDebug.warn("During setPoseVel catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }


    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/shadow/Aq;b(Lorg/valkyrienskies/core/impl/shadow/Aj;)V"
        ),
        remap = false
    )
    private void safeInvokeB(Aq instance, Aj remove) {
        if (!WapConfig.vsPhysSafeThread) {
            b(remove);
        } else {
            try {
                b(remove);
            } catch (Exception e) {
                EzDebug.warn("During b(remove) catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }


    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;applyInvariantForce(Lorg/joml/Vector3dc;)V"
        ),
        remap = false
    )
    private void safeApplyForce(PhysShipImpl instance, Vector3dc force) {
        if (!WapConfig.vsPhysSafeThread)
            instance.applyInvariantForce(force);
        else {
            try {
                instance.applyInvariantForce(force);
            } catch (Exception e) {
                EzDebug.warn("During applying force catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;applyInvariantTorque(Lorg/joml/Vector3dc;)V"
        ),
        remap = false
    )
    private void safeApplyTorque(PhysShipImpl instance, Vector3dc torque) {
        if (!WapConfig.vsPhysSafeThread)
            instance.applyInvariantTorque(torque);
        else {
            try {
                instance.applyInvariantTorque(torque);
            } catch (Exception e) {
                EzDebug.warn("During applying torque catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;applyQueuedForces()V"
        ),
        remap = false
    )
    private void safeApplyQueuedForces(PhysShipImpl instance) {
        if (!WapConfig.vsPhysSafeThread)
            instance.applyQueuedForces();
        else {
            try {
                instance.applyQueuedForces();
            } catch (Exception e) {
                EzDebug.warn("During applying queued forces catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/WingPhysicsSolver;applyWingForces(Lorg/valkyrienskies/core/api/ships/properties/ShipTransform;Lorg/valkyrienskies/physics_api/PoseVel;Lorg/valkyrienskies/core/impl/game/ships/WingManagerImpl;Lorg/joml/Matrix3dc;)Lkotlin/Pair;"
        ),
        remap = false
    )
    private Pair<Vector3dc, Vector3dc> safeApplyWingForces(WingPhysicsSolver instance, ShipTransform shipTransform, PoseVel poseVel, WingManagerImpl wingManager, Matrix3dc momentOfInertia) {
        if (!WapConfig.vsPhysSafeThread)
            return instance.applyWingForces(shipTransform, poseVel, wingManager, momentOfInertia);
        else {
            try {
                return instance.applyWingForces(shipTransform, poseVel, wingManager, momentOfInertia);
            } catch (Exception e) {
                EzDebug.warn("During applying wing forces catch exception:" + e.toString());
                e.printStackTrace();
                return new Pair<>(new Vector3d(), new Vector3d()); // 返回0向量对，防止物理线程崩
            }
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/api/ships/ShipForcesInducer;applyForces(Lorg/valkyrienskies/core/api/ships/PhysShip;)V"
        ),
        remap = false
    )
    private void safeApplyForces(ShipForcesInducer inducer, PhysShip ship) {
        if (!WapConfig.vsPhysSafeThread)
            inducer.applyForces(ship);
        else {
            try {
                inducer.applyForces(ship);
            } catch (Exception e) {
                EzDebug.warn("During applying ship forces catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/api/ships/ShipForcesInducer;applyForcesAndLookupPhysShips(Lorg/valkyrienskies/core/api/ships/PhysShip;Lkotlin/jvm/functions/Function1;)V"
        ),
        remap = false
    )
    private void safeApplyForcesAndLookup(ShipForcesInducer inducer, PhysShip ship, Function1<Long, PhysShip> lookup) {
        if (!WapConfig.vsPhysSafeThread)
            inducer.applyForcesAndLookupPhysShips(ship, lookup);
        else {
            try {
                inducer.applyForcesAndLookupPhysShips(ship, lookup);
            } catch (Exception e) {
                EzDebug.warn("During applying forces and lookup catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }


    /*@Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;applyInvariantForce(Lorg/joml/Vector3dc;)V"
        ),
        remap = false
    )
    private void safeApplyForce(PhysShipImpl instance, Vector3dc force) {
        if (!WapConfig.vsPhysSafeThread)
            instance.applyInvariantForce(force);
        else {
            try {
                instance.applyInvariantForce(force);
            } catch (Exception e) {
                EzDebug.warn("During applying force catch exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }
    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;applyInvariantTorque(Lorg/joml/Vector3dc;)V"
        ),
        remap = false
    )
    private void safeApplyTorque(PhysShipImpl instance, Vector3dc torque) {
        try {
            instance.applyInvariantTorque(torque);
        } catch (Exception e) {
            EzDebug.warn("During applying force catch exception:" + e.toString());
            e.printStackTrace();
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;applyQueuedForces()V"
        ),
        remap = false
    )
    private void safeApplyQueuedForces(PhysShipImpl instance) {
        try {
            instance.applyQueuedForces();
        } catch (Exception e) {
            EzDebug.warn("During applying force catch exception:" + e.toString());
            e.printStackTrace();
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/impl/game/ships/WingPhysicsSolver;applyWingForces(Lorg/valkyrienskies/core/api/ships/properties/ShipTransform;Lorg/valkyrienskies/physics_api/PoseVel;Lorg/valkyrienskies/core/impl/game/ships/WingManagerImpl;Lorg/joml/Matrix3dc;)Lkotlin/Pair;"
        ),
        remap = false
    )
    private Pair<Vector3dc, Vector3dc> safeApplyWingForces(
        WingPhysicsSolver instance,
        ShipTransform shipTransform,
        PoseVel poseVel,
        WingManagerImpl wingManager,
        Matrix3dc momentOfInertia
    ) {
        try {
            return instance.applyWingForces(shipTransform, poseVel, wingManager, momentOfInertia);
        } catch (Exception e) {
            EzDebug.warn("During applying force catch exception:" + e.toString());
            e.printStackTrace();
            return new Pair<>(new Vector3d(), new Vector3d());  //return 0 force and 0 torque for safe
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/api/ships/ShipForcesInducer;applyForces(Lorg/valkyrienskies/core/api/ships/PhysShip;)V"
        ),
        remap = false
    )
    private void safeForceInduce(ShipForcesInducer instance, PhysShip physShip) {
        try {
            instance.applyForces(physShip);
        } catch (Exception e) {
            EzDebug.warn("During applying force catch exception:" + e.toString());
            e.printStackTrace();
        }
    }

    @Redirect(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/valkyrienskies/core/api/ships/ShipForcesInducer;applyForcesAndLookupPhysShips(Lorg/valkyrienskies/core/api/ships/PhysShip;Lkotlin/jvm/functions/Function1;)V"
        ),
        remap = false
    )
    private void safeLookupForceInduce(ShipForcesInducer instance, PhysShip physShip, Function1<? super Long, ? extends PhysShip> lookupPhysShip) {
        try {
            instance.applyForcesAndLookupPhysShips(physShip, lookupPhysShip);
        } catch (Exception e) {
            EzDebug.warn("During applying force catch exception:" + e.toString());
            e.printStackTrace();
        }
    }*/

    /*@Inject(
        method = "a(Lorg/joml/Vector3dc;DZ)Lorg/valkyrienskies/core/impl/shadow/Ao;",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    public final void a(Vector3dc vector3dc, double d, boolean z2, CallbackInfoReturnable<Ao> cir) {
        if (!WapConfig.vsPhysSafeThread)
            return;

        Function2<CoroutineScope, Continuation<? super Unit>, Object> eInstance;
        try {
            if (eConstructior == null) {
                Class<?> eClass = Class.forName("org.valkyrienskies.core.impl.shadow.Aq$e");

                // 获取构造函数
                eConstructior = eClass.getDeclaredConstructor(
                    Aq.class, // 外部类实例（如果是非静态内部类）
                    Vector3dc.class,
                    double.class,
                    boolean.class,
                    Continuation.class
                );

                // 设置可访问
                eConstructior.setAccessible(true);
            }


            // 创建实例（注意：如果 e 是非静态内部类，需要提供 Aq 实例）
            eInstance = (Function2<CoroutineScope, Continuation<? super Unit>, Object>)eConstructior.newInstance(
                null, // 如果 e 是静态内部类则为 null
                vector3dc,
                d,
                z2,
                null  // continuation
            );
        } catch (Exception e) {
            EzDebug.warn("fail to refelect. use origin impl");
            return;
        }



        while (true) {
            try {
                //PhysItemAccessor ac = (PhysItemAccessor)this;
               // var phyFrames = ac.getC();
                var phyFrames = this.getPhysFrames();

                if (phyFrames.isEmpty())
                    break;

                Aj remove = phyFrames.remove();
                b(remove);
            } catch (Exception e) {
                EzDebug.warn("exception during generate phys frame(maybe?)" + e.toString());
                e.printStackTrace();
                break;
            }
        }

        this.getMaybeEachLevelShips().values().stream()
            .flatMap(x -> x.values().stream())
            .forEach(s -> s.setPoseVel(s.getRigidBodyReference().getPoseVel()));

        for (var shipMap : this.getMaybeEachLevelShips().values()) {
            shipMap.values().stream().forEach(s -> {
                try {
                    if (s.isStatic())
                        return;

                    s.getForceInducers().forEach(f -> {
                        f.applyForces(s);
                        f.applyForcesAndLookupPhysShips(s, new Function1<Long, PhysShip>() {
                            @Override
                            public PhysShip invoke(Long aLong) {
                                return this.a(aLong);
                            }

                            public PhysShip a(long var1) {
                                return shipMap.get(var1);
                            }
                        });
                    });

                    Pair<Vector3dc, Vector3dc> applyWingForces = WingPhysicsSolver.INSTANCE.applyWingForces(s.getTransform(), s.getPoseVel(), s.getWingManager$impl(), s.get_inertia().getMomentOfInertiaTensor());
                    s.applyInvariantForce(applyWingForces.component1());
                    s.applyInvariantTorque(applyWingForces.component2());
                    s.applyQueuedForces();
                } catch (Exception e) {
                    EzDebug.warn("Fail during apply force and torque:" + e.toString());
                    e.printStackTrace();
                }
            });
        }

        //BuildersKt.runBlocking$default((CoroutineContext) null, new e(this, vector3dc, d, z2, (Continuation) null), 1, (Object) null);
        //((Continuation)this.a());


        try {
            corou
            BuildersKt.runBlocking(
                EmptyCoroutineContext.INSTANCE,
                eInstance//,
                //1,
                //(Object) null
            );
            //Assertions.assertEquals(WELCOME, suspendResult.get());
        } catch (Exception e) {
            EzDebug.error("Interrupt exception!:" + e.toString());
            e.printStackTrace();
        }



        cir.setReturnValue(h());
    }*/







}
