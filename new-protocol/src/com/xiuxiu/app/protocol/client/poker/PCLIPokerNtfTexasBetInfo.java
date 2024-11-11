package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCLIPokerNtfTexasBetInfo {
    public long playerUid; // 下注的玩家UID
    public int type; // 下注的类型，1-下注，2-跟注，3-加注，4-过，5-梭哈，6-放弃
    public int bet; // 下注的筹码数量

    public long bossUid; // 本轮说话人的UID
    public int bossBetType; // 本轮说话人的下注类型，1-下注，3-加注，4-过，6-放弃，优先级：加注 > 下注 > 过 = 放弃
    public int bossBet; // 本轮说话人在本轮的总下注值
    
    public Next next = new Next(); // 下一个动作
    public Interest interest; //保险部分
    
    public static class Interest {
    	public int breakevenValue;//保本金额
        public int equalInterestValue;//等利金额
        public int breakevenPayValue;//保本赔付金额
        public int equalInterestPayValue;//等利赔付金额
        //反超率
        public int surpassOdds;
        //反超牌
        public List<Byte> surpassCards=new ArrayList<>();
        //玩家手牌
        public Map<Long,List<Byte>> cards=new HashMap<>();
        @Override
        public String toString() {
            return "Interest{" +
                    "breakevenValue=" + breakevenValue +
                    ", equalInterestValue=" + equalInterestValue +
                    ", surpassOdds=" + surpassOdds +
                    ", surpassCards=" + surpassCards +
                    ", breakevenPayValue=" + breakevenPayValue +
                    ", equalInterestPayValue=" + equalInterestPayValue +
                    ", cards=" + cards +
                    '}';
        }
    }
    public static class Next {
        public long playerUid; // 玩家UID
        public int action; // 0 - 无动作

        @Override
        public String toString() {
            return "Next{" +
                    "playerUid=" + playerUid +
                    ", action=" + action +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfTexasBetInfo{" +
                "playerUid=" + playerUid +
                ", type=" + type +
                ", bet=" + bet +
                ", bossUid=" + bossUid +
                ", bossBetType=" + bossBetType +
                ", bossBet=" + bossBet +
                ", next=" + next +
                ", interest=" + interest +
                '}';
    }
}
