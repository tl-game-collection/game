package com.xiuxiu.app.protocol.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCLICowScoreItemInfo {
    public long playerUid;
    public String playerName;
    public String  icon;
    public String score;
    public int cardType;
    public List<Byte> card = new ArrayList();//手牌
    public List<Byte> handCard = new ArrayList<>();
    public boolean isBnaker=false;//是否庄家（牛牛）
    public long bankerMul;//庄家抢庄倍数（牛牛）
    public long pushMul;//闲家推注倍数（牛牛）
    public  byte lastCard = -1;  // 最后一张牌(牛牛)
    public byte laiZiCard = -1; //赖子牌
    @Override
    public String toString() {
        return "PCLICowScoreItemInfo{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", icon='" + icon + '\'' +
                ", score='" + score + '\'' +
                ", cardType=" + cardType +
                ", card=" + card +
                ", isBnaker=" + isBnaker +
                ", bankerMul=" + bankerMul +
                ", pushMul=" + pushMul +
                ", lastCard=" + lastCard +
                '}';
    }
}
