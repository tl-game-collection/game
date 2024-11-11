package com.xiuxiu.core.queue;

import java.util.concurrent.locks.ReentrantLock;

public class AsynchronousQueueLock<T> {
    /** 空间锁 */
    private ReentrantLock spaceLock = new ReentrantLock();

    private T value = null;

    public ReentrantLock getSpaceLock() {
        return spaceLock;
    }

    public void setSpaceLock(ReentrantLock spaceLock) {
        this.spaceLock = spaceLock;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void remove() {
        this.value = null;
    }

}
