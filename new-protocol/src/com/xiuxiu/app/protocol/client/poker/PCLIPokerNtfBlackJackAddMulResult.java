package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfBlackJackAddMulResult {
    public long playerUid;              // 玩家ID
    public boolean isAddMul;            // 是否买保险
    public List<Byte> handCard = new ArrayList<>();         // 手牌
    public boolean isBust;                                  // 是否爆掉了(点数大于21)
    public int cardType;                                    // 牌型 0 没有牌型 1 BlackJack 2 五小龙
    public int points;                                      // 点数

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackAddMulResult{" +
                "playerUid = " + playerUid +
                ",handCard = " + handCard +
                ",isAddMul = " + isAddMul +
                ",isBust = " + isBust +
                ",cardType = " + cardType +
                ",points = " + points +
                "}";
    }

}
