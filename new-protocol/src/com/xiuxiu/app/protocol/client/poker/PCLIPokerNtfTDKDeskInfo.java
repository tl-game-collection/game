package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfTDKDeskInfo extends PCLIRoomDeskInfo {
    public int base; // 底注类型，1/2/3
    public boolean languo; // 上一把是否烂锅
    public Round round = new Round(); // 当前轮次
    public List<PlayerInfo> players = new ArrayList<>(); // 玩家信息
    public Next next = new Next();

    @Override
    public String toString() {
        return "PCLIPokerNtfTDKDeskInfo{" +
                "base=" + base +
                ", languo=" + languo +
                ", round=" + round +
                ", players=" + players +
                ", next=" + next +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }

    public static class Round {
        public int round; // 第几轮，1/2/3
        public int bet; // 下注多少

        @Override
        public String toString() {
            return "Round{" +
                    "round=" + round +
                    ", bet=" + bet +
                    '}';
        }
    }

    public static class PlayerInfo {
        public long uid; // 玩家的UID
        public List<Byte> cards = new ArrayList<>(); // 持有的牌，如果不是自己，则不包含暗牌
        public int bet; // 已下注数量
        public boolean gaveUp; // 是否已弃牌

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "uid=" + uid +
                    ", cards=" + cards +
                    ", bet=" + bet +
                    ", gaveUp=" + gaveUp +
                    '}';
        }
    }

    public static class Next {
        public int action; // 动作，1-下注，2-跟注，3-踢，4-反踢
        public long playerUid;

        @Override
        public String toString() {
            return "Next{" +
                    "action=" + action +
                    ", playerUid=" + playerUid +
                    '}';
        }
    }
}
