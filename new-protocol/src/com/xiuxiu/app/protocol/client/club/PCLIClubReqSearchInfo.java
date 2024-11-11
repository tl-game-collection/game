package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqSearchInfo {
    public long clubUid;                // 俱乐部Uid
    public int clubType;                // 俱乐部类型

    @Override
    public String toString() {
        return "PCLIClubReqSearchInfo{" +
                "clubUid=" + clubUid +
                "clubType=" + clubType +
                '}';
    }
}
