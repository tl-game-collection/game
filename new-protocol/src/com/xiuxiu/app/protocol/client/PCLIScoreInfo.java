package com.xiuxiu.app.protocol.client;

import java.util.List;

public class PCLIScoreInfo {
    public long time;
    public long recordUid;
    public List<PCLIScoreItemInfo> score;
    public long destroyUid;
    public int destroyType;

    @Override
    public String toString() {
        return "ScoreInfo{" +
                "time=" + time +
                ", recordUid=" + recordUid +
                ", destroyUid=" + destroyUid +
                ", destroyType=" + destroyType +
                ", score=" + score +
                '}';
    }
}
