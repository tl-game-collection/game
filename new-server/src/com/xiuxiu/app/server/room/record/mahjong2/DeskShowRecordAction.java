package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class DeskShowRecordAction extends RecordAction {
    protected HashMap<Long, Long> allShow = new HashMap<>();

    public DeskShowRecordAction() {
        super(EActionOp.DESK_SHOW, -1);
    }

    public void addAllShow(HashMap<Long, Long> allShow) {
        this.allShow.clear();
        this.allShow.putAll(allShow);
    }

    public HashMap<Long, Long> getAllShow() {
        return allShow;
    }

    public void setAllShow(HashMap<Long, Long> allShow) {
        this.allShow = allShow;
    }
}
