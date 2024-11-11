package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.List;

public class HuRecordAction extends RecordAction {
    protected List<Long> allHu = new ArrayList<>();

    public HuRecordAction() {
        super(EActionOp.HU, -1);
    }

    public void addHu(long playerUid) {
        this.allHu.add(playerUid);
    }

    public List<Long> getAllHu() {
        return allHu;
    }

    public void setAllHu(List<Long> allHu) {
        this.allHu = allHu;
    }
}
