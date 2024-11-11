package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIClubNtfActivityGoldInfo {

    public static class ClubActivityGoldInfo {
        public List<PCLIClubActivityGold> items = new ArrayList<>(); // 每项数据
        public int period; // 活动周期
        public long startTime; // 活动开始时间
        public int gameType; // 游戏类型
        public int gameSubType; // 游戏子类型

        @Override
        public String toString() {
            return "ClubActivityGoldInfo{" + "items=" + items + ", period=" + period + ", startTime=" + startTime + ", gameType="
                    + gameType + ", gameSubType=" + gameSubType + '}';
        }
    }

    public HashMap<Long, ClubActivityGoldInfo> data = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIClubNtfActivityGoldInfo{" + "data=" + data + '}';
    }

}
