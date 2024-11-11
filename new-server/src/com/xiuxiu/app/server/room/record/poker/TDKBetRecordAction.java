package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class TDKBetRecordAction extends RecordAction {
    private int bet;

    public TDKBetRecordAction(long playerUid, int bet) {
        super(EActionOp.REBET, playerUid);
        this.bet = bet;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
