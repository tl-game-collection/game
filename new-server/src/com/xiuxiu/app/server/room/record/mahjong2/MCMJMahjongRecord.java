package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.ArrayList;
import java.util.List;

import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongLaiZi;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;

public class MCMJMahjongRecord extends MahjongRecord {
    private List<Integer> cunList = new ArrayList<>();
    private List<Integer> fanList = new ArrayList<>();

    public MCMJMahjongRecord(IMahjongRoom room) {
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
        ResultRecordAction action = new MCMJResultRecordAction();
        this.addAction(action);
        return action;
    }

    public List<Integer> getCunList() {
        return cunList;
    }

    public void setCunList(List<Integer> cunList) {
        this.cunList.clear();
        this.cunList.addAll(cunList);
    }

    public List<Integer> getFanList() {
        return fanList;
    }

    public void setFanList(List<Integer> fanList) {
        this.fanList.clear();
        this.fanList.addAll(fanList);
    }
}
