package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class ArchBidRecordAction extends RecordAction {
    private List<KeyValue<Long, Integer>> records = new ArrayList<>();
    private int finalContract;

    public ArchBidRecordAction() {
        super(EActionOp.CALL_SCORE, -1);
    }

    public void clear() {
        this.records.clear();
    }

    public void addRecord(long playerUid, int score) {
        this.records.add(new KeyValue<>(playerUid, score));
    }

    public List<KeyValue<Long, Integer>> getRecords() {
        return records;
    }

    public void setRecords(List<KeyValue<Long, Integer>> records) {
        this.records = records;
    }

    public int getFinalContract() {
        return finalContract;
    }

    public void setFinalContract(int finalContract) {
        this.finalContract = finalContract;
    }
}
