package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfGetManager {
    public static class mangerInfo {
        public long playerUid;
        public List<Long> managerClubList = new ArrayList<>();

        @Override
        public String toString() {
            return "mangerInfo{" +
                    "playerUid=" + playerUid +
                    ", managerClubList=" + managerClubList +
                    '}';
        }
    }
    public long clubUid;
    public List<mangerInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIClubNtfGetManager{" +
                "clubUid=" + clubUid +
                ", list=" + list +
                '}';
    }
}
