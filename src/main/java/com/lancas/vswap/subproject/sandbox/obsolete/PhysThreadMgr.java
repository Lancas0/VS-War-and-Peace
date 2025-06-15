package com.lancas.vswap.subproject.sandbox.obsolete;

/*
import com.lancas.vs_wap.ModMain;

import java.util.concurrent.atomic.AtomicBoolean;

public class PhysThreadMgr {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread physicsThread;
    private static final long UPDATE_INTERVAL_NS = 16_666_666; // ≈16.67ms (60Hz)

    public void start() {
        if (running.get()) return;
        running.set(true);
        physicsThread = new Thread(this::runPhysicsLoop, ModMain.MODID + "SandBox-Physics-Thread");
        physicsThread.start();
    }

    public void stop() {
        running.set(false);
        if (physicsThread != null) {
            physicsThread.interrupt();
            try {
                physicsThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void runPhysicsLoop() {
        long lastTime = System.nanoTime();
        while (running.get()) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;

            // 执行物理计算
            if (elapsed >= UPDATE_INTERVAL_NS) {
                performPhysicsUpdate();
                lastTime = now;
            }

            // 精确休眠剩余时间
            long sleepTime = UPDATE_INTERVAL_NS - elapsed;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                } catch (InterruptedException e) {
                    if (!running.get()) break;
                }
            }
        }
    }

    private void performPhysicsUpdate() {
        // 在此处执行物理计算
    }
}
*/