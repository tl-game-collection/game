package com.xiuxiu.core.thread;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConsumeThread<T> extends BaseThread {
    private static final AtomicInteger INDEX = new AtomicInteger(0);
    protected BlockingQueue<T> queue;
    protected int max = -1;

    public ConsumeThread() {
        this(-1, "ConsumeThread-" + INDEX.getAndIncrement());
    }

    public ConsumeThread(String name) {
        this(-1, name);
    }

    public ConsumeThread(String name, boolean daemon) {
        this(-1, name, daemon);
    }

    public ConsumeThread(int max, String name) {
        this(max, name, true);
    }

    public ConsumeThread(int max, String name, boolean daemon) {
        super(name, daemon);
        this.max = max;
        if (this.max < 1) {
            this.queue = new LinkedBlockingQueue<>();
        } else {
            this.queue = new LinkedBlockingQueue<>(this.max);
        }
    }

    public void add(T value) {
        if (null == value) {
            return;
        }
        try {
            this.queue.put(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        while (this.start.get()) {
            try {
                T value = this.queue.poll(this.delay, this.timeUnit);
                if (null != value) {
                    this.exec(value);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.err.println("ThreadName:" + this.thread.getName() + " interrupted exception");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("ThreadName:" + this.thread.getName() + " exec exception");
            }
            if (!this.tick.isEmpty()) {
                long now2 = System.currentTimeMillis();
                Iterator<Tick> it = this.tick.iterator();
                while (it.hasNext()) {
                    Tick tick = it.next();
                    try {
                        tick.tick(now2, now2 - now);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("ThreadName:" + this.thread.getName() + " exec tick exception");
                    }
                }
                now = now2;
            }
        }
        while (!this.queue.isEmpty()) {
            T value = this.queue.poll();
            if (null != value) {
                try {
                    this.exec(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("ThreadName:" + this.thread.getName() + " exec exception");
                }
            }
        }
    }

    protected abstract void exec(T value);
}
