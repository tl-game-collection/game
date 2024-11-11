package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class XuanZengRecordAction extends RecordAction {
    protected HashMap<Long, Integer> value = new HashMap<>();

    public XuanZengRecordAction() {
        super(EActionOp.XUAN_ZENG, -1);
    }

    public void addXuanZeng(long playerUid, int value) {
        this.value.put(playerUid, value);
    }

    public HashMap<Long, Integer> getValue() {
        return value;
    }

    public void setValue(HashMap<Long, Integer> value) {
        this.value = value;
    }
}
