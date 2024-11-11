package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfBlackJackDeskInfo extends PCLIRoomDeskInfo {
    public HashMap<Long, BlackJackPlayerInfo> blackJackPlayerInfo = new HashMap<>();
    public long curOpUid;
    public long timeout;                                 // 倒计时

    public static class BlackJackPlayerInfo {
        public long playerUid;
        public List<Byte> handCard = new ArrayList<>();     // 玩家手牌
        public boolean isBust;                              // 是否爆掉了
        public boolean isAddMul;                            // 是否加倍
        public boolean isBuyInsurance;                      // 是否买保险
        public int cardType;                                // 牌型 0 没有牌型 1 BlackJack 2 五小龙
        public int points;                                  // 点数
        public String score;                                // 分数
        public boolean onlineState;                         // 在线状态
        public int rebetNum;                                // 下注

        @Override
        public String toString() {
            return "BlackJackPlayerInfo{" +
                    "playerUid=" + playerUid +
                    ", isBust=" + isBust +
                    ", isAddMul=" + isAddMul +
                    ", isBuyInsurance=" + isBuyInsurance +
                    ", handCard=" + handCard +
                    ", cardType=" + cardType +
                    ", points=" + points +
                    ", onlineState=" + onlineState +
                    ", rebetNum=" + rebetNum +
                    ", score=" + score +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackDeskInfo{" +
                "roomInfo=" + roomInfo +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                ", bankerIndex=" + bankerIndex +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", curOpUid=" + curOpUid +
                ", timeout=" + timeout +
                ", blackJackPlayerInfo=" + blackJackPlayerInfo +
                '}';
    }
}
