package com.xiuxiu.app.protocol.client.club;

/**
 * 请求创建俱乐部
 */
public class PCLIClubReqClubCaeateClub {
    public String name;         // 俱乐部名称
    public String desc;         // 俱乐部描述
    public String gameDesc;     // 俱乐部游戏描述
    public String icon;         // 俱乐部头像
    public int clubType;        // 俱乐部类型 1 房卡亲友圈 2 金币亲友圈
    public long clubUid;        // 俱乐部Uid

    @Override
    public String toString() {
        return "PCLIClubReqClubCaeateClub{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", gameDesc='" + gameDesc + '\'' +
                ", icon='" + icon + '\'' +
                ", clubType='" + clubType +
                ", clubUid='" + clubUid +
                '}';
    }
}
