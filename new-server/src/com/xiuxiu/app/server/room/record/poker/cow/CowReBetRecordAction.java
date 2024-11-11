package com.xiuxiu.app.server.room.record.poker.cow;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/6 18:24
 * @comment:
 */
public class CowReBetRecordAction extends RecordAction {
    private List<KeyValue<Long, Integer>> allRebet = new ArrayList<>();

    public CowReBetRecordAction() {
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
