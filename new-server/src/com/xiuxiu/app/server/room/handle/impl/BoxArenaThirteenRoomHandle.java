package com.xiuxiu.app.server.room.handle.impl;

import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.player.poker.ThirteenPlayer;
import com.xiuxiu.app.server.score.ScoreItemInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BoxArenaThirteenRoomHandle extends AbstractBoxArenaRoomHandle {

    public BoxArenaThirteenRoomHandle(IRoom room, Box box) {
        super(room, box);
    }

    @Override
    protected ScoreItemInfo buildScoreItemInfo(IRoomPlayer temp) {
        ScoreItemInfo itemInfo = new ScoreItemInfo();
        itemInfo.setScore(this.room.getRecordScore(temp));
        itemInfo.setPlayerUid(temp.getUid());
        ThirteenPlayer thirteenPlayer = (ThirteenPlayer)temp;
        itemInfo.setMonsterType(thirteenPlayer.getMonsterType().getValue());
        if (thirteenPlayer.getHandCard() != null) {
            itemInfo.getCard().addAll(thirteenPlayer.getHandCard());
        } if (thirteenPlayer.getHeadCard()!= null) {
            itemInfo.getHeadCard().addAll(thirteenPlayer.getHeadCard());
        } if (thirteenPlayer.getMediumCard() != null) {
            itemInfo.getMediumCard().addAll(thirteenPlayer.getMediumCard());
        } if (thirteenPlayer.getTailCard() != null) {
            itemInfo.getTailCard().addAll(thirteenPlayer.getTailCard());
        }
        return itemInfo;
    }
}
