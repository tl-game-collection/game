package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class FlutterRecordAction extends RecordAction {
    protected HashMap<Long, Integer> flutter = new HashMap<>();

    public FlutterRecordAction() {
        super(EActionOp.FLUTTER, -1);
    }

    public void addFlutter(long playerUid, int flutter) {
        this.flutter.put(playerUid, flutter);
    }

    public HashMap<Long, Integer> getFlutter() {
        return flutter;
    }

    public void setFlutter(HashMap<Long, Integer> flutter) {
        this.flutter = flutter;
    }
}
