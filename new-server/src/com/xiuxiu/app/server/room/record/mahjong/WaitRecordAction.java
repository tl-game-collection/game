package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;
import java.util.List;

public class WaitRecordAction extends RecordAction {
    private static final int BUMP = 0x01;
    private static final int BAR = 0x02;
    private static final int HU = 0x04;

    protected HashMap<Long, Integer> allWaitInfo = new HashMap<>();

    public WaitRecordAction() {
        super(EActionOp.WAIT, -1);
    }

    public void addWaitInfo(long playerUid, boolean bump, List<Byte> bar, boolean hu) {
        int value = 0;
        if (bump) {
            value |= BUMP;
        }
        if (null != bar) {
            value |= BAR;
        }
        if (hu) {
            value |= HU;
        }

        this.allWaitInfo.put(playerUid, value);
    }

    public HashMap<Long, Integer> getAllWaitInfo() {
        return allWaitInfo;
    }

    public void setAllWaitInfo(HashMap<Long, Integer> allWaitInfo) {
        this.allWaitInfo = allWaitInfo;
    }
}
