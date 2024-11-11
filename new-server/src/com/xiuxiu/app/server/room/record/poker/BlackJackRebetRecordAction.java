package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlackJackRebetRecordAction extends RecordAction {
    protected HashMap<Long,List<Integer>> allRebet = new HashMap();

    public BlackJackRebetRecordAction() {
        super(EActionOp.REBET, -1);
    }

    public void addRebet(long playerUid, int value){
        List<Integer> list = this.allRebet.get(playerUid);
        if(null == list){
            list = new ArrayList<>();
            this.allRebet.put(playerUid,list);
        }
        list.add(value);
    }

    public HashMap<Long,List<Integer>> getAllRebet() {
        return allRebet;
    }
}
