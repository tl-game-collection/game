package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomInfo {
    public int roomId;
    public long ownerPlayerUid;
    public int placeType; //EPlaceType
    public long placeUid; //和placeType对应，如果placeType==LEAGUE:placeUid==leagueUid,roomType==GROUP:placeUid==groupUid
    public PCLIRoomBriefInfo roomBriefInfo;
    public List<PCLIRoomPlayerInfo> players = new ArrayList<>();
    public List<Long> readyPlayers=new ArrayList<>();//准备列表
    public String remarks;
    public long readyTime; //牛牛金花准备时间
    @Override
    public String toString() {
        return "PCLIRoomInfo{" +
                "roomId=" + roomId +
                ", ownerPlayerUid=" + ownerPlayerUid +
                ", roomBriefInfo=" + roomBriefInfo +
                ", players=" + players +
                ", readyPlayers=" + readyPlayers +
                ", placeType=" + placeType +
                ", placeUid=" + placeUid +
                ", remarks=" + remarks +
                ", readyTime=" + readyTime +
                '}';
    }
}
