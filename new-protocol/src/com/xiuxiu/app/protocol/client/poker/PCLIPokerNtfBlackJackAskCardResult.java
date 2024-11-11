package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfBlackJackAskCardResult {
    public long playerUid;                                  // 玩家ID
    public List<Byte> handCard = new ArrayList<>();         // 手牌
    public boolean isBust;                                  // 是否爆掉了(点数大于21)
    public int cardType;                                    // 牌型 0 没有牌型 1 BlackJack 2 五小龙
    public int points;                                      // 点数

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackAskCardResult{" +
                "playerUid=" + playerUid +
                ", handCard=" + handCard +
                ", isBust=" + isBust +
                ", cardType=" + cardType +
                ",points=" + points +
                '}';
    }
}
