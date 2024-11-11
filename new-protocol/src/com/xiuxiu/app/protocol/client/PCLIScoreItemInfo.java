package com.xiuxiu.app.protocol.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCLIScoreItemInfo {
    public long playerUid;
    public String playerName;
    public String icon;
    public String score;
    public int likeCnt;
    public HashSet<Long> like = new HashSet<>();
    public List<String> allVisitCard = new ArrayList<>();
    public int cardType;
    public List<Byte> card = new ArrayList();//手牌

    @Override
    public String toString() {
        return "PCLIScoreItemInfo{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", icon='" + icon + '\'' +
                ", score='" + score + '\'' +
                ", likeCnt=" + likeCnt +
                ", like=" + like +
                ", allVisitCard=" + allVisitCard +
                ", cardType=" + cardType +
                ", card=" + card +
                '}';
    }
}
