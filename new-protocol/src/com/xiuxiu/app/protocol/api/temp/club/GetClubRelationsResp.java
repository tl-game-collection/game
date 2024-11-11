package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetClubRelationsResp extends ErrorMsg {
    public static class clubRelations {
        public long clubUid;
        public String clubName;
        public long parentUid;
        public long ownerUid;
        public String ownerName;
        public long time;
        public long totalGoldValue;                                // 总竞技分
        public long totalRewardValue;                              // 总奖励分

        @Override
        public String toString() {
            return "clubRelations{" +
                    "clubUid=" + clubUid +
                    ", clubName='" + clubName + '\'' +
                    ", parentUid=" + parentUid +
                    ", ownerUid=" + ownerUid +
                    ", totalGoldValue=" + totalGoldValue +
                    ", totalRewardValue=" + totalRewardValue +
                    ", ownerName='" + ownerName + '\'' +
                    ", time=" + time +
                    '}';
        }
    }

    public List<clubRelations> list = new ArrayList<>();

    @Override
    public String toString() {
        return "GetClubRelationsResp{" +
                "list=" + list +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
