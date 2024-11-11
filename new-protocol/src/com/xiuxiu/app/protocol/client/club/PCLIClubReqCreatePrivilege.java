package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqCreatePrivilege {
    public int clubType;    // 类型 1 房卡俱乐部 2 金币俱乐部

    @Override
    public String toString() {
        return "PCLIClubReqCreatePrivilege{" +
                "clubType=" + clubType +
                '}';
    }
}
