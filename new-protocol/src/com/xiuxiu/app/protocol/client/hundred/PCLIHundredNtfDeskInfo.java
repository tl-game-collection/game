package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIHundredNtfDeskInfo {
    public long boxId;
    public long roomId;
    public long groupUid;
    public int gameType;
    public long bankerPlayerUid;
    public String bankerPlayerName;
    public String bankerPlayerIcon;
    public long bankerUid;
    public String bankerValue;
    public int remainRebValue;
    public int curBureau;
    public HashMap<String, Integer> rule = new HashMap<>();
    public int state;                                   // 0: 初始化, 1: 开始, 2: 下注, 3: 开牌, 4: 结束
    public int remainTime;
    public int ownerType;                                           // 联盟标识 0 普通房间 1 群竞技场 2 联盟竞技场
    public List<PCLIHundredVipSeatInfo> vipSeatInfoList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIHundredNtfDeskInfo{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", groupUid=" + groupUid +
                ", gameType=" + gameType +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerPlayerName='" + bankerPlayerName + '\'' +
                ", bankerPlayerIcon='" + bankerPlayerIcon + '\'' +
                ", bankerUid=" + bankerUid +
                ", bankerValue='" + bankerValue + '\'' +
                ", remainRebValue=" + remainRebValue +
                ", curBureau=" + curBureau +
                ", rule=" + rule +
                ", state=" + state +
                ", remainTime=" + remainTime +
                ", ownerType=" + ownerType +
                ", vipSeatInfoList=" + vipSeatInfoList +
                '}';
    }
}
