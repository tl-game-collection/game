package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaiGowSendCardRecordAction extends RecordAction {
    protected HashMap<Long, List<Byte>> allCard = new HashMap<>();

    public PaiGowSendCardRecordAction() {
        super(EActionOp.SEND_CARD, -1);
    }

    public void addCard(long playerUid, List<Byte> card){
        List<Byte> temp = new ArrayList<>(card);
        this.allCard.put(playerUid, temp);
    }

    public HashMap<Long, List<Byte>> getAllCard() {
        return allCard;
    }
}

