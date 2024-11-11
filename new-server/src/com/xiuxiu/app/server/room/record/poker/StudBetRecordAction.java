package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

/**
 * 梭哈下注记录
 */
public class StudBetRecordAction extends RecordAction {
    private int type;
    private int bet;

    public StudBetRecordAction(long playerUid, int type, int bet) {
        super(EActionOp.REBET, playerUid);
        this.type = type;
        this.bet = bet;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
