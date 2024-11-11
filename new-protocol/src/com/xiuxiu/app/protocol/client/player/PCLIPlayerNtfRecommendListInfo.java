package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfRecommendListInfo {
    public static class RecommendInfo {
        public long playerUid;
        public String playerName;
        public String playerIcon;
        public int diamond;             // 房卡
        public boolean friend;
        public long bindingTime;        // 绑定的时间
        public boolean isAchieve;       // 是否完成

        @Override
        public String toString() {
            return "RecommendInfo{" +
                    "playerUid=" + playerUid +
                    ", playerName=" + playerName +
                    ", diamond=" + diamond +
                    ", friend=" + friend +
                    ", isAchieve=" + isAchieve +
                    ", bindingTime=" + bindingTime +
                    '}';
        }
    }

    public List<RecommendInfo> recommendList = new ArrayList<>();
    public int page;
    public boolean next;
    public int diamondSum;      // 领取房卡数量
    public int residueSum;      // 剩余房卡数量


    @Override
    public String toString() {
        return "PCLIPlayerNtfRecommendListInfo{" +
                "recommendList=" + recommendList +
                ", diamondSum=" + diamondSum +
                ", residueSum=" + residueSum +
                ", page=" + page +
                ", next=" + next +
                '}';
    }
}
