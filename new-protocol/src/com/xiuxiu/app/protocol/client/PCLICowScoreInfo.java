package com.xiuxiu.app.protocol.client;

import java.util.List;

public class PCLICowScoreInfo {
    public long time;
    public List<PCLICowScoreItemInfo> score;
    public long boxUid;
    
    @Override
    public String toString() {
        return "PCLICowScoreInfo{" +
                "time=" + time +
                ", score=" + score +
                ", boxUid=" + boxUid +
                '}';
    }
}
