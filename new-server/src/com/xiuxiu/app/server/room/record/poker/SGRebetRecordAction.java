package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class SGRebetRecordAction extends RecordAction {
    protected List<KeyValue<Long, Integer>> allRebet = new ArrayList<>();

    public SGRebetRecordAction() {
        super(EActionOp.REBET, -1);
    }

    public void addRebet(long playerUid, int value) {
        this.allRebet.add(new KeyValue<>(playerUid, value));
    }

    public List<KeyValue<Long, Integer>> getAllRebet() {
        return allRebet;
    }

    public void setAllRebet(List<KeyValue<Long, Integer>> allRebet) {
        this.allRebet = allRebet;
    }
}
