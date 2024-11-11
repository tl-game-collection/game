package com.xiuxiu.app.protocol.client;

import java.util.ArrayList;
import java.util.List;

public class PCLIPaiCowScoreItemInfo {
    public long playerUid;
    public String playerName;
    public String  icon;
    public String score;
    public List<Byte> card = new ArrayList<Byte>();//手牌
    public int[] cardType;
   
    @Override
    public String toString() {
        return "PCLICowScoreItemInfo{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", icon='" + icon + '\'' +
                ", score='" + score + '\'' +
                ", card=" + card +
                '}';
    }
}
