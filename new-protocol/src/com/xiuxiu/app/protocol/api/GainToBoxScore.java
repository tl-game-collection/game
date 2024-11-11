package com.xiuxiu.app.protocol.api;

public class GainToBoxScore {
    public long groupUid;           // 群uid
    public long boxScoreUid;        // 包厢战绩uid
    public long gainPlayerUid;      // 打赏玩家uid
    public int cnt;                 // 打赏数量
    public long gainTime;           // 打赏时间戳(ms)
    public String sign;             // md5(groupUid + boxScoreUid + gainPlayerUid + cnt + key)

    @Override
    public String toString() {
        return "GainToBoxScore{" +
                "groupUid=" + groupUid +
                ", boxScoreUid=" + boxScoreUid +
                ", gainPlayerUid=" + gainPlayerUid +
                ", cnt=" + cnt +
                ", gainTime=" + gainTime +
                ", sign='" + sign + '\'' +
                '}';
    }
}
