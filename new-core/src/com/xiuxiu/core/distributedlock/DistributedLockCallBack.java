package com.xiuxiu.core.distributedlock;

public interface DistributedLockCallBack<T> {
    T process(boolean succ);

    String getLockName();
}
