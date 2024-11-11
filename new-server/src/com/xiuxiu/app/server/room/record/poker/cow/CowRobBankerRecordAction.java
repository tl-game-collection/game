package com.xiuxiu.app.server.room.record.poker.cow;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/6 18:26
 * @comment:
 */
public class CowRobBankerRecordAction extends RecordAction {
    private List<KeyValue<Long, Integer>> allRobBanker = new ArrayList<>();

    public CowRobBankerRecordAction() {
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
