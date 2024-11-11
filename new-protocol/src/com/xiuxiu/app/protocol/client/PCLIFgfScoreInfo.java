package com.xiuxiu.app.protocol.client;

import java.util.List;

public class PCLIFgfScoreInfo {
    public long time;
    public List<PCLIFgfScoreItemInfo> score;
    public long boxUid;
    
    @Override
    public String toString() {
        return "PCLIFgfScoreInfo{" +
                "time=" + time +
                ", score=" + score +
                ", boxUid=" + boxUid +
                '}';
    }
}
