package com.xiuxiu.app.protocol.client;

import java.util.List;

public class PCLIThirteenScoreInfo {
    public long time;
    public List<PCLIThirteenScoreItemInfo> score;
    public long boxUid;

    @Override
    public String toString() {
        return "PCLIThirteenScoreInfo{" +
                "time=" + time +
                ", score=" + score +
                ", boxUid=" + boxUid +
                '}';
    }
}
