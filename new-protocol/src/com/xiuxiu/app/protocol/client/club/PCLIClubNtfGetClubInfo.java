package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfGetClubInfo {
    public long clubUid;
    public long parentUid;
    public List<Long> childUidList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIClubNtfGetClubInfo{" +
                "clubUid=" + clubUid +
                ", parentUid=" + parentUid +
                ", childUidList=" + childUidList +
                '}';
    }
}
