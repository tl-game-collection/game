package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;
import java.util.List;

public class ShuaiPaiRecordAction extends RecordAction {
    protected HashMap<Long, List<Byte>> shuaiPai = new HashMap<>();

    public ShuaiPaiRecordAction() {
        super(EActionOp.SHUAI_PAI, -1);
    }

    public void addShuaiPai(long playerUid, List<Byte> card) {
        this.shuaiPai.put(playerUid, card);
    }

    public HashMap<Long, List<Byte>> getShuaiPai() {
        return shuaiPai;
    }

    public void setShuaiPai(HashMap<Long, List<Byte>> shuaiPai) {
        this.shuaiPai = shuaiPai;
    }
}
