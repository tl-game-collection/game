package com.xiuxiu.app.server.db;

import com.xiuxiu.app.server.BaseManager;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

public class UIDManager extends BaseManager {
    private static class UIDManagerHolder {
        private static UIDManager instance = new UIDManager();
    }

    public static UIDManager I = UIDManagerHolder.instance;

    private RedissonClient redisClient;

    private RAtomicLong[] allUid = new RAtomicLong[UIDType.values().length];

    private UIDManager() {
    }

    public void init(RedissonClient redisClient) {
        this.redisClient = redisClient;
    }

    public void loadAll() {
        // 加载账号UID
        for (int i = 0, len = UIDType.values().length; i < len; ++i) {
            UIDType type = UIDType.values()[i];
            this.allUid[i] = this.redisClient.getAtomicLong(type.getKey());
            if (type.getInitValue() > 0) {
                this.allUid[i].compareAndSet(0, type.getInitValue());
            }
        }
    }

    public long get(UIDType type) {
        return this.allUid[type.ordinal()].get();
    }

    public long getAndInc(UIDType type) {
        return this.allUid[type.ordinal()].getAndIncrement();
    }

    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }
}
