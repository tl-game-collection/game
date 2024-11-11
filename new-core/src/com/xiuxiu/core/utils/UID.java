package com.xiuxiu.core.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class UID {
    private static class UIDHolder {
        private static final UID instance = new UID();
    }

    public static long getUID(int type) {
        return UIDHolder.instance.get(type);
    }

    private static final long epoch = 1514736000000L;                           // 2018-01-01 00:00:00
    private static final long typeBits = 10L;                                   // type bits
    private static final long maxTypeId = ~(-1L << typeBits);                   // max type id -1L ^ (-1L << typeBits)
    private static final long sequenceBits = 12L;                               // sequence bits
    private static final long sequenceMask = ~(-1L ^ sequenceBits);             // sequence mask -1L ^ (-1L ^ sequenceBits)
    private static final long typeShift = sequenceBits;
    private static final long timestampShift = sequenceBits + typeBits;
    private ConcurrentHashMap<Integer, UIDInfo> allUIDInfos = new ConcurrentHashMap<>();

    private UID() {

    }

    private long get(int type) {
        if (type > maxTypeId || type < 0) {
            throw new IllegalArgumentException(String.format("type in range [%d, %d], curType:%d", 0, maxTypeId, type));
        }
        UIDInfo uidInfo = this.allUIDInfos.get(type);
        if (null == uidInfo) {
            this.allUIDInfos.putIfAbsent(type, new UIDInfo(type));
            uidInfo = this.allUIDInfos.get(type);
        }
        return uidInfo.nextUID();
    }

    private class UIDInfo {
        private int type;
        private long lastTimestamp;
        private AtomicLong sequenceId = new AtomicLong(0);

        public UIDInfo(int type) {
            this.type = type;
        }

        public long nextUID() {
            long now = System.currentTimeMillis();
            long uid;
            if (this.lastTimestamp == now) {
                if (this.sequenceId.compareAndSet(sequenceMask, 0)) {
                    while (now <= this.lastTimestamp) {
                        now = System.currentTimeMillis();
                    }
                }
            } else {
                this.sequenceId.set(0);
            }
            this.lastTimestamp = now;
            uid = ((now - epoch) << timestampShift) | (this.type << typeShift) | (this.sequenceId.getAndIncrement());
            return uid;
        }
    }
}
