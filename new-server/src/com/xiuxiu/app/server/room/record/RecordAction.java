package com.xiuxiu.app.server.room.record;

import com.xiuxiu.app.server.room.normal.action.EActionOp;

public class RecordAction {
    protected EActionOp op;
    protected long playerUid;

    public RecordAction(EActionOp op, long playerUid) {
        this.op = op;
        this.playerUid = playerUid;
    }

    public EActionOp getOp() {
        return op;
    }

    public void setOp(EActionOp op) {
        this.op = op;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }
}
