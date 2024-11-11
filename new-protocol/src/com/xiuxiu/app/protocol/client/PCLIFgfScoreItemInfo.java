package com.xiuxiu.app.protocol.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCLIFgfScoreItemInfo {
    public long playerUid;
    public String playerName;
    public String icon;
    public String score;
    public int cardType;
    public List<Byte> card = new ArrayList();//手牌
    public boolean isDiscard = false;//扎金花是否弃牌
    public boolean isWin = false;//扎金花输赢

    @Override
    public String toString() {
        return "PCLIFgfScoreItemInfo{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", icon='" + icon + '\'' +
                ", score='" + score + '\'' +
                ", cardType=" + cardType +
                ", card=" + card +
                ", isDiscard=" + isDiscard +
                ", isWin=" + isWin +
                '}';
    }
}
