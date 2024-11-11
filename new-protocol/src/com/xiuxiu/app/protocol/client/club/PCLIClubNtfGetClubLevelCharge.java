package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfGetClubLevelCharge {
    public static class ClubLevelChargeInfo {
        public long clubUid;
        public String clubName;
        public long serviceCharge;  //管理费

        @Override
        public String toString() {
            return "ClubLevelChargeInfo{" +
                    "clubUid=" + clubUid +
                    ", clubName='" + clubName + '\'' +
                    ", serviceCharge=" + serviceCharge +
                    '}';
        }
    }

    public List<ClubLevelChargeInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIClubNtfGetClubLevelCharge{" +
                ", list=" + list +
                '}';
    }
}
