package com.xiuxiu.app.server.room.normal.action;

import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.room.normal.IRoom;

public abstract class BaseAction implements IAction {
    protected final IRoom room;
    protected final EActionOp op;
    protected long sourceTimeout;
    protected long timeout;
    protected long startTime;
    protected long useTime;
    protected boolean active = true;

    public BaseAction(IRoom room, EActionOp op, long timeout) {
        this.room = room;
        this.op = op;
        if (timeout < 0) {
            timeout = -1;
        }
        this.startTime = System.currentTimeMillis();
        this.sourceTimeout = timeout;
        this.timeout = timeout;
    }

    @Override
    public void pause() {
        this.useTime += System.currentTimeMillis() - this.startTime;
        this.active = false;
    }

    @Override
    public void recover() {
        this.startTime = System.currentTimeMillis();
        if (!this.active) {
            this.doRecover();
        }
        this.active = true;
    }

    protected abstract void doRecover();

    @Override
    public boolean canAction(long curTime) {
        if (!this.active) {
            return false;
        }
        //if (-1 == this.timeout) {
            if (curTime - this.startTime + this.useTime >= Constant.ROOM_TAKE_TIMEOUT) {
                this.operationTimeout();
                if (-1 == this.timeout) {
                    return true;
                }
            }
        //}
        if (-1 == curTime) {
            return true;
        }
        if (-1 == this.timeout || (curTime - this.startTime + this.useTime) < this.timeout) {
            return false;
        }
        return true;
    }

    public long getRemain() {
        if (-1 == this.timeout) {
            return -1;
        }
        return this.timeout - (System.currentTimeMillis() - this.startTime + this.useTime);
    }

    protected abstract void operationTimeout();
}
