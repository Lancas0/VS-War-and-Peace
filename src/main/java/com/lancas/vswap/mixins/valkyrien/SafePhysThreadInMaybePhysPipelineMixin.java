package com.lancas.vswap.mixins.valkyrien;

import com.lancas.vswap.WapConfig;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixinfriend.ManualRemapPhysThread;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.impl.shadow.Ap;
import kotlin.Unit;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

@Mixin(Ap.class)
public abstract class SafePhysThreadInMaybePhysPipelineMixin implements ManualRemapPhysThread {

    @Inject(
        method = "run",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    public void run(CallbackInfo ci) {
        if (!WapConfig.vsPhysSafeThread)
            return;

        boolean var5;
        try {
            label220:
            while(true) {
                while(true) {
                    try {
                        if (this.getShouldStop()) {
                            break label220;
                        }

                        AtomicBoolean shouldExit = new AtomicBoolean(false);
                        physOnce(shouldExit);
                        if (shouldExit.get()) {
                            break label220;
                        }
                    } catch (Exception e) {
                        EzDebug.error("during phys thread exception:" + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception var19) {
            //Ap.a.a().error("Error in physics pipeline background task", (Throwable)var19);
            EzDebug.error("Error in physics pipeline background task:" + var19.toString());
            var19.printStackTrace();

            byte var2 = 10;

            for(int var3 = 0; var3 < var2; ++var3) {
                var5 = false;
                //a.a().error("!!!!!!! VS PHYSICS THREAD CRASHED !!!!!!!");
                EzDebug.fatal("!!!!!!! VS PHYSICS THREAD CRASHED !!!!!!!");
            }
        }

        //a.a().warn("Physics pipeline ending");
        EzDebug.warn("Physics pipeline ending");
        ci.cancel();
    }

    @Shadow(remap = false) @Final protected abstract void j();
    @Shadow(remap = false) @Final protected abstract void a(double a, long b);

    @Unique
    private void physOnce(AtomicBoolean shouldEndPhys) {
        if (this.getPhysItem().b()) {
            int var21 = VSCoreConfig.SERVER.getPt().getPhysicsTicksPerGameTick();
            double var24 = 1.0D / (double)(20 * var21) * VSCoreConfig.SERVER.getPhysics().getPhysicsSpeed();
            Lock var4 = (Lock)this.getTickerLock();
            var4.lock();

            try {
                //var5 = false; ?? why here is a unused ver5

                while(this.getPhysTickCnt() >= var21) {
                    this.getPhysTickAvailable().await();
                }

                this.getPhysItem().a(this.getPhysItem().d(), var24);
                int var6 = this.getAndIncPhysTickCnt();//this.g++;
                if (this.getPhysTickCnt() >= var21) {
                    this.getPhysTickFull().signal();
                }

                Unit var27 = Unit.INSTANCE;
            } catch (Exception e) {
                EzDebug.warn("Inside PhysOnce there is a exception: " + e.toString());
                e.printStackTrace();
            } finally {
                var4.unlock();
            }
        } else {
            if (this.getPhysItem().getDeleteResources()) {
                this.getPhysItem().c();
                //break label220;
                shouldEndPhys.set(true);
                return;
            }

            if (!this.getPhysItem().getArePhysicsRunning()) {
                this.setPostponeNano(0);
                this.getTimestampQueue().clear();
                Lock var1 = (Lock)this.getPhysLock();
                var1.lock();

                try {
                    boolean var22 = false;

                    while(!this.getPhysItem().getArePhysicsRunning()) {
                        this.getPhysCondition().await();
                    }

                    Unit var23 = Unit.INSTANCE;
                } catch (Exception e) {
                    EzDebug.warn("Inside PhysOnce there is a exception: " + e.toString());
                    e.printStackTrace();
                } finally {
                    var1.unlock();
                }
            }

            double var20 = 1.0E9D / (double)this.getTargetFrame();
            double var25 = var20 / 1.0E9D;
            boolean var7 = false;
            long var8 = System.nanoTime();
            boolean var10 = false;
            this.getPhysItem().a(this.getPhysItem().d(), var25);  //d() is gravity
            long var26 = System.nanoTime() - var8;
            this.j();
            this.a(var20, var26);
        }
    }


}
