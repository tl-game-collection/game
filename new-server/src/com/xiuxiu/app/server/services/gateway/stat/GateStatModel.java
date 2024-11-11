package com.xiuxiu.app.server.services.gateway.stat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GateStatModel {
    private int commandId;
    private long costTime;
    private long costTimePerHit;
    private int hitCount;
    private int successCount;
    private ReentrantLock locker = new ReentrantLock();

    protected GateStatModel() {
    }


    private GateStatModel(int commandId, long costTime, long costTimePerHit, int hitCount, int successCount) {
        this.commandId = commandId;
        this.costTime = costTime;
        this.costTimePerHit = costTimePerHit;
        this.hitCount = hitCount;
        this.successCount = successCount;
    }

    public long getCostTimePerHit() {
        return costTimePerHit;
    }


    public void add(int commandId, long time, boolean success) {
        try {
            if (locker.tryLock(8, TimeUnit.MILLISECONDS)) {
                this.commandId = commandId;
                this.costTime += time;
                this.successCount++;
                if (success) {
                    this.hitCount++;
                }
            }
        } catch (Exception e) {
        } finally {
            if (locker.isHeldByCurrentThread()) {
                locker.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return "GateStatModel{" +
                "commandId=" + commandId +
                ", costTime=" + costTime +
                ", costTimePerHit=" + costTimePerHit +
                ", hitCount=" + hitCount +
                ", successCount=" + successCount +
                '}';
    }

    public GateStatModel clone4Report() {
        long costTimePerHit = 0;
        if (hitCount > 0) {
            costTimePerHit = costTime / hitCount;
        }
        return new GateStatModel(this.commandId, this.costTime, costTimePerHit, this.hitCount, this.successCount);
    }
}
