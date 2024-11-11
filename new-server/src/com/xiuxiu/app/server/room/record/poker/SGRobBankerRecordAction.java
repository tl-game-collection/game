package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class SGRobBankerRecordAction extends RecordAction {
    protected List<KeyValue<Long, Integer>> allRobBanker = new ArrayList<>();

    public SGRobBankerRecordAction() {
        super(EActionOp.ROB_BANKER, -1);
    }

    public void addRobBanker(long playerUid, int value) {
        this.allRobBanker.add(new KeyValue<>(playerUid, value));
    }

    public List<KeyValue<Long, Integer>> getAllRobBanker() {
        return allRobBanker;
    }

    public void setAllRobBanker(List<KeyValue<Long, Integer>> allRobBanker) {
        this.allRobBanker = allRobBanker;
    }
}
