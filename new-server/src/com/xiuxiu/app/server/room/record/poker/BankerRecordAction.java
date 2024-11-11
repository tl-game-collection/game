package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class BankerRecordAction extends RecordAction {

    public int bankerLoop;       //端火锅庄进行的轮数
    public int deskNote;         //端火锅桌面筹码数；

    public BankerRecordAction(long playerUid) {
        super(EActionOp.BANKER, playerUid);
    }

    public BankerRecordAction(long playerUid,int bankerLoop,int deskNote){
        super(EActionOp.BANKER, playerUid);
        this.bankerLoop = bankerLoop;
        this.deskNote = deskNote;
    }
}
