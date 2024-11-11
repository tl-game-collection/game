package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;
import java.util.List;

public class YangPaiRecordAction extends RecordAction {
    protected HashMap<Long, List<Byte>> yangPai = new HashMap<>();

    public YangPaiRecordAction() {
        super(EActionOp.YANG_PAI, -1);
    }

    public void addYangPai(long playerUid, List<Byte> card) {
        this.yangPai.put(playerUid, card);
    }

    public HashMap<Long, List<Byte>> getYangPai() {
        return yangPai;
    }

    public void setYangPai(HashMap<Long, List<Byte>> yangPai) {
        this.yangPai = yangPai;
    }
}
