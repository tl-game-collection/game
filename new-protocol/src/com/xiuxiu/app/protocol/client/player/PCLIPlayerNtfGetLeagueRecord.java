package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfGetLeagueRecord {
    public static class LeagueRecord {
        public long uid;                    // 星币记录Uid
        public long playerUid;           // 玩家Uid
        public int amount;               // 操作金额
        public int action;               // 操作类型
        public long month;               // 本月时间戳
        public long inMoney;             // 收入金额
        public long outMoney;            // 支出金额
        public long beginAmount;         // 操作前的金额
        public long optPlayer;           // 操作人Uid
        public String optIcon;           // 操作人头像
        public String optName;           // 操作人昵称
        public long createdAt;           // 操作时间

        @Override
        public String toString() {
            return "LeagueRecord{" +
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
                    '}';
        }
    }
    
    public List<LeagueRecord> list = new ArrayList<>();       // 记录列表
    public int page;                                          // 当前页数
    public boolean next;                                      // 下一页还有没有
    public long totalDec;                                      //总消耗
    public long totalInc;                                       //总获得
    public long playerUid;                                      //请求查询对象的uid
    public String icon;                                         //请求查询对象的头像
    public String name;                                         //请求查询对象的name


    @Override
    public String toString() {
        return "PCLIPlayerNtfGetLeagueRecord{" +
                "list=" + list +
                ", page=" + page +
                ", next=" + next +
                ", totalDec=" + totalDec +
                ", totalInc=" + totalInc +
                ", playerUid=" + playerUid +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
