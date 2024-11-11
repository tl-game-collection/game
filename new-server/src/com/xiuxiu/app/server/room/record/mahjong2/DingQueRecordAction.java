package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class DingQueRecordAction extends RecordAction {
    protected HashMap<Long, Integer> color = new HashMap<>();

    public DingQueRecordAction() {
        super(EActionOp.DING_QUE, -1);
    }

    public void addDingQue(long playerUid, int color) {
        this.color.put(playerUid, color);
    }

    public HashMap<Long, Integer> getColor() {
        return color;
    }

    public void setColor(HashMap<Long, Integer> color) {
        this.color = color;
    }
}
