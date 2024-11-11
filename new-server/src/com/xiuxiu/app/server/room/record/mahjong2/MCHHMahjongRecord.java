package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongLaiZi;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;

public class MCHHMahjongRecord extends MahjongRecord {
    public MCHHMahjongRecord(IMahjongRoom room) {
        super();
        this.roomInfo = new RecordMahjongRoomBriefInfo();
        this.roomInfo.setRoomId(room.getRoomId());
        this.roomInfo.setBankerIndex(room.getBankerIndex());
        this.roomInfo.setRoomType(room.getRoomType().ordinal());
        this.roomInfo.setGameType(room.getGameType());
        this.roomInfo.setGameSubType(room.getGameSubType());
        this.roomInfo.setRule(room.getRule());
        ((RecordMahjongRoomBriefInfo) this.roomInfo).setCrap1(room.getCrap1());
        ((RecordMahjongRoomBriefInfo) this.roomInfo).setCrap2(room.getCrap2());
        if (room instanceof IMahjongLaiZi) {
            ((RecordMahjongRoomBriefInfo) this.roomInfo).setLaiZiCard(((IMahjongLaiZi) room).getLaiZi());
            ((RecordMahjongRoomBriefInfo) this.roomInfo).getPiList().addAll(((IMahjongLaiZi) room).getPiList());
        }
    }

    @Override
    public ResultRecordAction addResultRecordAction() {
        ResultRecordAction action = new MCHHResultRecordAction();
        this.addAction(action);
        return action;
    }
}
