package com.xiuxiu.core.thread;

import com.xiuxiu.core.ds.ConcurrentHashSet;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseThread implements Runnable {
    protected Thread thread;
    protected final AtomicBoolean start = new AtomicBoolean(false);
    protected long delay = 1;
    protected TimeUnit timeUnit = TimeUnit.SECONDS;
    protected ConcurrentHashSet<Tick> tick = new ConcurrentHashSet<>();

    public BaseThread() {
        this("BaseThread", true);
    }

    public BaseThread(String threadName) {
        this(threadName, true);
    }

    public BaseThread(String threadName, boolean daemon) {
        this.thread = new Thread(this, threadName);
        this.thread.setDaemon(daemon);
    }

    public void attackTick(Tick tick) {
        this.tick.add(tick);
    }

    public void deAttackTick(Tick tick) {
        this.tick.remove(tick);
    }

    public void setDelay(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public boolean start() {
        if (this.start.compareAndSet(false, true)) {
            this.thread.start();
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (this.start.compareAndSet(true, false)) {
            this.tick.clear();
            this.thread.interrupt();
            return true;
        }
        return false;
    }

    public void join() throws InterruptedException {
        if (!this.start.get()) {
            return;
        }
        if (this.thread.isDaemon()) {
            return;
        }
        this.thread.join();
    }

    public boolean isStart() {
        return this.start.get();
    }
}
