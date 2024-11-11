package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongLaiZi;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;

public class DYMJMahjongRecord extends MahjongRecord {
    public DYMJMahjongRecord(IMahjongRoom room) {
        super();
        this.roomInfo = new RecordWHMJMahjongRoomBriefInfo();
        this.roomInfo.setRoomId(room.getRoomId());
        this.roomInfo.setBankerIndex(room.getBankerIndex());
        this.roomInfo.setRoomType(room.getRoomType().ordinal());
        this.roomInfo.setGameType(room.getGameType());
        this.roomInfo.setGameSubType(room.getGameSubType());
        this.roomInfo.setRule(room.getRule());
        ((RecordWHMJMahjongRoomBriefInfo) this.roomInfo).setCrap1(room.getCrap1());
        ((RecordWHMJMahjongRoomBriefInfo) this.roomInfo).setCrap2(room.getCrap2());
        if (room instanceof IMahjongLaiZi) {
            ((RecordWHMJMahjongRoomBriefInfo) this.roomInfo).setLaiZiCard(((IMahjongLaiZi) room).getLaiZi());
            ((RecordWHMJMahjongRoomBriefInfo) this.roomInfo).getPiList().addAll(((IMahjongLaiZi) room).getPiList());
        }
    }

    public void setBaoZiF(boolean baoZiF) {
        ((RecordWHMJMahjongRoomBriefInfo) this.roomInfo).setBaoZiF(baoZiF);
    }

    public void setJFYLF(boolean JFYLF) {
        ((RecordWHMJMahjongRoomBriefInfo) this.roomInfo).setJFYLF(JFYLF);
    }

    public void setJ258F(boolean j258F) {
        ((RecordWHMJMahjongRoomBriefInfo) this.roomInfo).setJ258F(j258F);
    }

    @Override
    public ResultRecordAction addResultRecordAction() {
        ResultRecordAction action = new DYMJResultRecordAction();
        this.addAction(action);
        return action;
    }
}
