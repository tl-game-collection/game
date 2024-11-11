package com.xiuxiu.app.protocol.client;

import java.util.List;

public class PCLSGScoreInfo {
    public long time;
    public List<PCLISGScoreItemInfo> score;
    public long boxUid;
    
    @Override
    public String toString() {
        return "PCLSGScoreInfo{" +
                "time=" + time +
                ", score=" + score +
                ", boxUid=" + boxUid +
                '}';
    }
}
