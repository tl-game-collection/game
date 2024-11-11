package com.xiuxiu.app.protocol.client;

import java.util.List;

public class PCLIPaiCowScoreInfo {
    public long time;
    public List<PCLIPaiCowScoreItemInfo> score;
    public long boxUid;

    @Override
    public String toString() {
        return "PCLIPaiCowScoreInfo{" +
                "time=" + time +
                ", score=" + score +
                ", boxUid=" + boxUid +
                '}';
    }
}
