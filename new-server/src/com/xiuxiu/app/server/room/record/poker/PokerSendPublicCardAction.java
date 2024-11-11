package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.List;

public class PokerSendPublicCardAction extends RecordAction {

    private List<Byte> publicCards = new ArrayList<>();

    public PokerSendPublicCardAction(List<Byte> cards) {
        super(EActionOp.FUMBLE, -1);
        this.publicCards = cards;
    }
}
