package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfCheLaKeSortBeginInfo {
    public int monsterType;                         // 拉克牌型，无为 -1
    public List<CardType> list = new ArrayList<>(); // 推荐牌型
    public int remain;

    public static class CardType {
        public List<Byte> cards = new ArrayList<>();    // 13张，头墩3张，中墩5张，尾墩5张
        public int headType;                            // 头墩牌类型
        public int mediumType;                          // 中墩牌类型
        public int tailType;                            // 尾墩牌类型

        @Override
        public String toString() {
            return "cardType{" +
                    "cards=" + cards +
                    ", headType=" + headType +
                    ", mediumType=" + mediumType +
                    ", tailType=" + tailType +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfCheLaKeSortBeginInfo{" +
                "monsterType=" + monsterType +
                ", list=" + list +
                ", remain=" + remain +
                '}';
    }
}
