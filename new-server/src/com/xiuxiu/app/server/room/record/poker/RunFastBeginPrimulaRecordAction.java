package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RunFastBeginPrimulaRecordAction  extends RecordAction {
    protected List<KeyValue<Long, Integer>> allPrimula = new ArrayList<>();

    public RunFastBeginPrimulaRecordAction() {
        super(EActionOp.PRIMULA, -1);
    }

    public void addAllPrimula(long playerUid, int value) {
        this.allPrimula.add(new KeyValue<>(playerUid, value));
    }

    public List<KeyValue<Long, Integer>> getAllPrimula() {
        return allPrimula;
    }

    public void setAllPrimula(List<KeyValue<Long, Integer>> allPrimula) {
        this.allPrimula = allPrimula;
    }
}
