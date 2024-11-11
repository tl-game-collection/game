package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfGetClubGoldRecord {
    public static class ClubRecord {
        public long uid;                 // 记录Uid
        public long playerUid;           // 玩家Uid
        public long amount;               // 操作金额
        public int action;               // 操作类型
        public long month;               // 本月时间戳
        public long inMoney;             // 收入金额
        public long outMoney;            // 支出金额
        public long beginAmount;         // 操作前的金额
        public long optPlayer;           // 操作人Uid
        public String optIcon;           // 操作人头像
        public String optName;           // 操作人昵称
        public long createdAt;           // 操作时间(当天零点)
        public long optTime;             // 具体操作时间

        @Override
        public String toString() {
            return "ClubRecord{" +
                    "uid=" + uid +
                    ", playerUid=" + playerUid +
                    ", amount=" + amount +
                    ", action=" + action +
                    ", month=" + month +
                    ", inMoney=" + inMoney +
                    ", outMoney=" + outMoney +
                    ", beginAmount=" + beginAmount +
                    ", optPlayer=" + optPlayer +
                    ", optIcon='" + optIcon + '\'' +
                    ", optName='" + optName + '\'' +
                    ", createdAt=" + createdAt +
                    ", optTime=" + optTime +
                    '}';
        }
    }

    public List<ClubRecord> list = new ArrayList<>();       // 记录列表
    public int page;                                          // 当前页数
    public boolean next;                                      // 下一页还有没有
    public long totalReward;                                     //圈奖励分总值
    public long totalGold;                                       //圈竞技分总值
    public long totalRewardMainClub;                             //总圈奖励分总值
    public long totalGoldMainClub;                               //总圈竞技分总值
    public long playerUid;                                      //请求查询对象的uid
    public String icon;                                         //请求查询对象的头像
    public String name;                                         //请求查询对象的name
    public long totalScore;                                     //总竞技分
    public long upTotalScore;                                   //总上分数
    public long downTotalScore;                                 //总下分数

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetClubGoldRecord{" +
                "list=" + list +
                ", page=" + page +
                ", next=" + next +
                ", totalReward=" + totalReward +
                ", totalGold=" + totalGold +
                ", totalRewardMainClub=" + totalRewardMainClub +
                ", totalGoldMainClub=" + totalGoldMainClub +
                ", playerUid=" + playerUid +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", totalScore=" + totalScore +
                ", upTotalScore=" + upTotalScore +
                ", downTotalScore=" + downTotalScore +
                '}';
    }
}
