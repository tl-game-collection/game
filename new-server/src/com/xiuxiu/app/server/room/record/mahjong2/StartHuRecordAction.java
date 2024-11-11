package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class StartHuRecordAction extends RecordAction {
    protected HashMap<Long, Boolean> value = new HashMap<>();

    public StartHuRecordAction() {
        super(EActionOp.START_HU, -1);
    }

    public void addStartHu(long playerUid, boolean hu) {
        this.value.put(playerUid, hu);
    }

    public HashMap<Long, Boolean> getValue() {
        return value;
    }

    public void setValue(HashMap<Long, Boolean> value) {
        this.value = value;
    }
}
