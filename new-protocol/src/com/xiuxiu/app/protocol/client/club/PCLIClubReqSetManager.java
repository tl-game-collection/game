package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubReqSetManager {
    public long clubUid;
    public long playerUid;
    public List<Long> managerClubList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIClubReqSetManager{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", managerClubList=" + managerClubList +
                '}';
    }
}
