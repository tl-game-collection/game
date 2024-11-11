package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfRedEnvelopeGameOverInfo extends PCLIRoomGameOverInfo {
   public static class TotalCnt {
        public int totalScore;             //总金额
        public int pokerRedEnvelopeSendNum; //发红包数量
        public int pokerRedEnvelopeSendSum; //发红包总金额
        public int pokerRedEnvelopeRobNum;//抢红包数量
        public int pokerRedEnvelopeRobSum; //抢红包总金额
        public int pokerRedEnvelopeWinReparation;//赢红包赔偿
        public int pokerRedEnvelopeLostReparation;//输红包赔偿
        public int pokerRedEnvelopeThunderNum;//中雷次数

        @Override
        public String toString() {
            return "TotalCnt{" +
                    "totalScore=" + totalScore +
                    ", pokerRedEnvelopeSendNum=" + pokerRedEnvelopeSendNum +
                    ", pokerRedEnvelopeSendSum=" + pokerRedEnvelopeSendSum +
                    ", pokerRedEnvelopeRobNum=" + pokerRedEnvelopeRobNum +
                    ", pokerRedEnvelopeRobSum=" + pokerRedEnvelopeRobSum +
                    ", pokerRedEnvelopeWinReparation=" + pokerRedEnvelopeWinReparation +
                    ", pokerRedEnvelopeLostReparation=" + pokerRedEnvelopeLostReparation +
                    ", pokerRedEnvelopeThunderNum=" + pokerRedEnvelopeThunderNum +
                    '}';
        }
    }

    public  static class  RedEnvelopeInfo {
        public long PlayerUid;           // 当前轮抢红包玩家ID
        public long sum;                 // 当前轮红包金额
        public PCLIPokerNtfRedEnvelopeGameOverInfo.TotalCnt totalCnt;
        public List<RedEnvelopeInfo> redEnvelopeList = new ArrayList<>();

        @Override
        public String toString() {
            return "RedEnvelopeInfo{" +
                    "PlayerUid=" + PlayerUid +
                    ", sum=" + sum +
                    ", totalCnt=" + totalCnt +
                    '}';
        }
    }
    public List<RedEnvelopeInfo> redEnvelopeList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfRedEnvelopeGameOverInfo{" +
                "redEnvelopeList=" + redEnvelopeList +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
