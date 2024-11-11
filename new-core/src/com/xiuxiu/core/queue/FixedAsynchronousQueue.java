package com.xiuxiu.core.queue;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 定长异步队列 支持多线程 读 取 循环 定长 不支持 自动扩容
 */
public class FixedAsynchronousQueue<T extends SupportAsynchronous> {

    protected AsynchronousQueueLock<T>[] fixedAsynchronousSource = null;

    @SuppressWarnings("unchecked")
    public FixedAsynchronousQueue(int size) {
        fixedAsynchronousSource = new AsynchronousQueueLock[size];
        for (int i = 0, j = fixedAsynchronousSource.length; i < j; i++) {
            fixedAsynchronousSource[i] = new AsynchronousQueueLock<T>();
        }
    }

    public void register(T value) {
        for (int i = 0, size = fixedAsynchronousSource.length; i < size; i++) {
            AsynchronousQueueLock<T> asynchronousQueueLock = fixedAsynchronousSource[i];
            ReentrantLock spaceLock = asynchronousQueueLock.getSpaceLock();
            try {
                if (spaceLock.tryLock()) {
                    // 如果得到锁
                    if (asynchronousQueueLock.get() == null) {
                        asynchronousQueueLock.set(value);
                        value.setIndex(i);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (spaceLock.isHeldByCurrentThread()) {
                    spaceLock.unlock();
                }
            }
        }
    }

    public void remove(T value) {
        // 如果快速删除没有被删掉
        if (!fastRemove(value)) {
            for (int i = 0; i < fixedAsynchronousSource.length; i++) {
                AsynchronousQueueLock<T> asynchronousQueueLock = fixedAsynchronousSource[i];
                T source = asynchronousQueueLock.get();
                ReentrantLock spaceLock = asynchronousQueueLock.getSpaceLock();
                if (source != null && value.equals(source)) {
                    // 加锁
                    try {
                        spaceLock.lock();
                        // 删除
                        asynchronousQueueLock.remove(); 
                        value = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (spaceLock.isHeldByCurrentThread()) {
                            spaceLock.unlock();
                        }
                    }
                }
            }
        }

    }

    /**
     * 快速删除
     * @param value
     * @return
     */
    private boolean fastRemove(T value) {

        int index = value.getIndex();
        AsynchronousQueueLock<T> asynchronousQueueLock = fixedAsynchronousSource[index];
        ReentrantLock spaceLock = asynchronousQueueLock.getSpaceLock();
        try {
            spaceLock.lock();
            T source = asynchronousQueueLock.get();
            if (source != null && value.equals(source)) {
                asynchronousQueueLock.remove();
                value = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (spaceLock.isHeldByCurrentThread()) {
                spaceLock.unlock();
            }
        }
    }

    public AsynchronousQueueLock<T>[] getSource() {
        return this.fixedAsynchronousSource;
    }

}
