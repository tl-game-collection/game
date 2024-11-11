package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class WaitSelectRecordAction extends RecordAction {
    protected HashMap<Long, EActionOp> allSelect = new HashMap<>();

    public WaitSelectRecordAction() {
        super(EActionOp.WAIT_SELECT, -1);
    }

    public void addSelect(long playerUid, EActionOp op) {
        this.allSelect.put(playerUid, op);
    }

    public HashMap<Long, EActionOp> getAllSelect() {
        return allSelect;
    }

    public void setAllSelect(HashMap<Long, EActionOp> allSelect) {
        this.allSelect = allSelect;
    }
}
