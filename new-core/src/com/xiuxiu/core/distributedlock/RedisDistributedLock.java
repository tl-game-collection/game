package com.xiuxiu.core.distributedlock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class RedisDistributedLock implements DistributedLock {
    private RedissonClient redisson;

    public RedisDistributedLock() {

    }

    public RedisDistributedLock(RedissonClient redisson) {
        this.redisson = redisson;
    }

    public void setRedisClient(RedissonClient redisson) {
        this.redisson = redisson;
    }

    public <T> T lock(DistributedLockCallBack<T> callBack) {
        return this.lock(callBack, 150, TimeUnit.SECONDS);
    }

    public <T> T lock(DistributedLockCallBack<T> callBack, long overTime, TimeUnit unit) {
        return this.lock(callBack, 0, overTime, unit);
    }

    public <T> T lock(DistributedLockCallBack<T> callBack, long waitTime, long overTime, TimeUnit unit) {
        RLock lock = null;
        try {
            lock = this.redisson.getLock(callBack.getLockName());
            boolean succ = lock.tryLock(waitTime, overTime, unit);
            try {
                return callBack.process(succ);
            } finally {
                if (succ) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }
}
