package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxReqLikeInfo {
    public long groupUid;           // 群uid
    public long boxScoreUid;        // 包厢战绩uid
    public long likePlayerUid;      // 被点赞玩家uid

    @Override
    public String toString() {
        return "PCLIBoxReqLikeInfo{" +
                "groupUid=" + groupUid +
                ", boxScoreUid=" + boxScoreUid +
                ", likePlayerUid=" + likePlayerUid +
                '}';
    }
}
