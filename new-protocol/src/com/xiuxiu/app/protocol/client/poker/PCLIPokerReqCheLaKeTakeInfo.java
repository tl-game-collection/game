package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerReqCheLaKeTakeInfo {
    public List<Byte> cards = new ArrayList<>(); // 道排顺序排列后的牌堆
    public int headType; // 头道牌型
    public int middleType; // 中道牌型
    public int tailType; // 尾道牌型
    public int monsterType; // 拉克牌型，无为 -1

    @Override
    public String toString() {
        return "PCLIPokerReqCheLaKeTakeInfo{" +
                "cards=" + cards +
                ", headType=" + headType +
                ", middleType=" + middleType +
                ", tailType=" + tailType +
                ", monsterType=" + monsterType +
                '}';
    }
}
