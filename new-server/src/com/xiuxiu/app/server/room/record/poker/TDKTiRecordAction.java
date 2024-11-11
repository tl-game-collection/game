package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class TDKTiRecordAction extends RecordAction {
    private int action;
    private int bet;

    public TDKTiRecordAction(long playerUid, int action, int bet) {
        super(EActionOp.ADD_NOTE, playerUid);
        this.action = action;
        this.bet = bet;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
