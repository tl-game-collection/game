package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;
import java.util.List;

public class ShuKanRecordAction extends RecordAction {
    protected HashMap<Long, List<Integer>> shuKan = new HashMap<>();      // 高16(258将)+低16(1-9点数)

    public ShuKanRecordAction() {
        super(EActionOp.SHUKAN, -1);
    }

    public void addShuKan(long playerUid, List<Integer> point) {
        this.shuKan.put(playerUid, point);
    }

    public HashMap<Long, List<Integer>> getShuKan() {
        return this.shuKan;
    }

    public void setShuKan(HashMap<Long,List<Integer>> shuKan) {
        this.shuKan = shuKan;
    }
}