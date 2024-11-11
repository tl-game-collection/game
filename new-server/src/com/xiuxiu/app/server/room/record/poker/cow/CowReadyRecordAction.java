package com.xiuxiu.app.server.room.record.poker.cow;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashSet;

public class CowReadyRecordAction extends RecordAction {
    protected HashSet<Long> allReady = new HashSet<>();

    public CowReadyRecordAction() {
        super(EActionOp.REBET, -1);
    }

    public void addReady(long playerUid){
        this.allReady.add(playerUid);
    }

    public HashSet<Long> getAllReady() {
        return allReady;
    }
}

