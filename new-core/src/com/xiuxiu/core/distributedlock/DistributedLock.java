package com.xiuxiu.core.distributedlock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {
    <T> T lock(DistributedLockCallBack<T> callBack);

    <T> T lock(DistributedLockCallBack<T> callBack, long overTime, TimeUnit unit);

    <T> T lock(DistributedLockCallBack<T> callBack, long waitTime, long overTime, TimeUnit unit);
}
