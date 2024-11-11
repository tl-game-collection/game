package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxNtfLikeInfo {
    public long groupUid;           // 群uid
    public long boxScoreUid;        // 包厢战绩uid
    public long likePlayerUid;      // 被点赞玩家uid

    public PCLIBoxNtfLikeInfo() {
    }

    public PCLIBoxNtfLikeInfo(long groupUid, long boxScoreUid, long likePlayerUid) {
        this.groupUid = groupUid;
        this.boxScoreUid = boxScoreUid;
        this.likePlayerUid = likePlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIBoxNtfLikeInfo{" +
                "groupUid=" + groupUid +
                ", boxScoreUid=" + boxScoreUid +
                ", likePlayerUid=" + likePlayerUid +
                '}';
    }
}
