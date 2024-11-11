package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfBlackJackHandCardInfo {
    public static class CardInfo {
        public long playerUid;
        public List<Byte> handCard = new ArrayList<>();     // 玩家手牌
        public boolean isBust;                              // 是否爆掉了
        public int cardType;                                // 牌型 0 没有牌型 1 BlackJack 2 五小龙
        public int points;                                  // 点数

        @Override
        public String toString() {
            return "CardInfo{" +
                    "playerUid=" + playerUid +
                    ",handCard=" + handCard +
                    ",isBust=" + isBust +
                    ",cardType=" + cardType +
                    ",points=" + points +
                    '}';
        }
    }

    public HashMap<Long, CardInfo> cardInfo = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackHandCardInfo{" +
                "cardInfo=" + cardInfo +
                '}';
    }
}
