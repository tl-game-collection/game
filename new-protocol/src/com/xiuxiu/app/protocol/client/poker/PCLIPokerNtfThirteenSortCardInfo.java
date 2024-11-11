package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfThirteenSortCardInfo {
    public static class cardType {
        public List<Byte> cards = new ArrayList<>();    // 13张，头墩3张，中墩5张，尾墩5张
        public int headType;                            // 头墩牌类型
        public int mediumType;                          // 中墩牌类型
        public int tailType;                            // 尾墩牌类型
        public List<Byte>  headCard = new ArrayList<>();                            // 头墩牌
        public List<Byte>  mediumCard = new ArrayList<>();                          // 中墩牌
        public List<Byte>  tailCard = new ArrayList<>();                            // 尾墩牌

        @Override
        public String toString() {
            return "cardType{" +
                    "cards=" + cards +
                    ", headType=" + headType +
                    ", mediumType=" + mediumType +
                    ", tailType=" + tailType +
                    ", headCard=" + headCard +
                    ", mediumCard=" + mediumCard +
                    ", tailCard=" + tailCard +
                    '}';
        }
    }
        public int monsterType;                         // 怪物牌型，无为 -1
        public List<cardType> list = new ArrayList<>(); // 推荐牌型

    @Override
    public String toString() {
        return "PCLIPokerNtfThirteenSortCardInfo{" +
                "monsterType=" + monsterType +
                ", list=" + list +
                '}';
    }
}
