package com.xiuxiu.app.protocol.client;

import java.util.ArrayList;
import java.util.List;

public class PCLISGScoreItemInfo {
    public long playerUid;
    public String playerName;
    public String  icon;
    public String score;
    public int cardType;
    public int cardTypeExtra;
    public List<Byte> card = new ArrayList();//手牌
    public boolean isBnaker=false;//是否庄家（
    @Override
    public String toString() {
        return "PCLISGScoreItemInfo{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", icon='" + icon + '\'' +
                ", score='" + score + '\'' +
                ", cardType=" + cardType +
                ", card=" + card +
                ", isBnaker=" + isBnaker +
                '}';
    }
}
