package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;
import java.util.List;

public class HuanPaiRecordAction extends RecordAction {
    protected HashMap<Long, List<Byte>> huanPai = new HashMap<>();
    protected int type;

    public HuanPaiRecordAction() {
        super(EActionOp.HUAN_PAI, -1);
    }

    public void addHuanPai(long playerUid, List<Byte> card) {
        this.huanPai.put(playerUid, card);
    }

    public HashMap<Long, List<Byte>> getHuanPai() {
        return huanPai;
    }

    public void setHuanPai(HashMap<Long, List<Byte>> huanPai) {
        this.huanPai = huanPai;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
