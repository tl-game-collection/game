package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaiGowOpenRecordAction extends RecordAction {

    public HashMap<Long,List<Byte>> cardMap = new HashMap<>();

    public PaiGowOpenRecordAction() {
        super(EActionOp.OPEN_CARD, -1);
    }

    public void addOpenCard(Long playerUid, List<Byte> card){
        List<Byte> cardList = this.cardMap.get(playerUid);
        if(null == cardList){
            cardList = new ArrayList<>();
            cardMap.put(playerUid,cardList);
        }
        cardList.addAll(card);
    }

    public HashMap<Long,List<Byte>> getCardMap(){
        return this.cardMap;
    }


}
