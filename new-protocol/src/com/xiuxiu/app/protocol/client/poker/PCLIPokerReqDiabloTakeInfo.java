package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerReqDiabloTakeInfo {
    public List<Byte> cards = new ArrayList<>();    // 13张，头墩3张，中墩5张，尾墩5张
    public int headType;                            // 头墩牌类型
    public int mediumType;                          // 中墩牌类型
    public int tailType;                            // 尾墩牌类型
    public int monsterType;                         // 怪物牌型，无为 -1

    @Override
    public String toString() {
        return "PCLIPokerReqDiabloTakeInfo{" +
                "cards=" + cards +
                ", headType=" + headType +
                ", mediumType=" + mediumType +
                ", tailType=" + tailType +
                ", monsterType=" + monsterType +
                '}';
    }
}

