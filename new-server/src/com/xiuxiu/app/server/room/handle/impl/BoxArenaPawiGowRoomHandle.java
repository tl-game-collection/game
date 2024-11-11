package com.xiuxiu.app.server.room.handle.impl;

import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.player.poker.PaiGowPlayer;
import com.xiuxiu.app.server.score.ScoreItemInfo;

public class BoxArenaPawiGowRoomHandle extends AbstractBoxArenaRoomHandle {

    public BoxArenaPawiGowRoomHandle(IRoom room, Box box) {
        super(room, box);
    }

    @Override
    protected ScoreItemInfo buildScoreItemInfo(IRoomPlayer temp) {
        ScoreItemInfo itemInfo = new ScoreItemInfo();
        itemInfo.setScore(this.room.getRecordScore(temp));
        itemInfo.setPlayerUid(temp.getUid());
        PaiGowPlayer paiGowPlayer = (PaiGowPlayer)temp;
        int[] cardTypes = paiGowPlayer.getOpenCardType();
        int[] tempCardTypes = new int[cardTypes.length];
        System.arraycopy(cardTypes, 0, tempCardTypes, 0, cardTypes.length);
        itemInfo.setCardTypes(tempCardTypes);
        // 记录开牌记录
        if (paiGowPlayer.getOpenCards() != null) {
            itemInfo.getCard().addAll(paiGowPlayer.getOpenCards());
        } 
        return itemInfo;
    }

}
