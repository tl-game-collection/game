package com.xiuxiu.app.protocol.client;

import java.util.ArrayList;
import java.util.List;

public class PCLIThirteenScoreItemInfo {
    public long playerUid;
    public String playerName;
    public String  icon;
    public String score;
    public List<Byte> card = new ArrayList();//手牌
    public int monsterType;//怪物牌型（十三水）
    /**
     * 头墩牌
     */
    public List<Byte> headCard = new ArrayList<>(3);
    /**
     * 中墩牌
     */
    public List<Byte> mediumCard = new ArrayList<>(5);
    /**
     * 尾墩牌
     */
    public List<Byte> tailCard = new ArrayList<>(5);

    @Override
    public String toString() {
        return "PCLIThirteenScoreItemInfo{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", icon='" + icon + '\'' +
                ", score='" + score + '\'' +
                ", monsterType=" + monsterType +
                ", card=" + card +
                ", headCard=" + headCard +
                ", mediumCard=" + mediumCard +
                ", tailCard=" + tailCard +
                '}';
    }
}
