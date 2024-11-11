package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqGetRewardValueRecord {
    public long clubUid;            // 俱乐部Uid
    public int type;                // 模式, 1: 加载每一天的列表, 2: 加载某一天的详细列表
    public int page;                // 分页, 从0开始
    public long time;

    @Override
    public String toString() {
        return "PCLIClubReqGetRewardValueRecord{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", page=" + page +
                '}';
    }
}
