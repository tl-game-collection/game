package com.xiuxiu.app.protocol.api.temp.club;

/**
 *
 */
public class CreateClub {
    public long clubUid;            // 俱乐部UID
    public int clubType;            // 俱乐部类型(1 : 亲友圈 、 2 : 比赛场)
    public String clubName;         // 俱乐部名称
    public String clubDesc;         // 俱乐部描述
    public String gameDesc;         // 俱乐部游戏描述
    public long playerUid;          // 玩家UID
    public String sign;             // MD5(clubUid + clubType + clubName + clubDesc + gameDesc + playerUid + key)

    @Override
    public String toString() {
        return "CreateClub{" +
                "clubUid=" + clubUid +
                ", clubType=" + clubType +
                ", clubName='" + clubName + '\'' +
                ", clubDesc='" + clubDesc + '\'' +
                ", gameDesc='" + gameDesc + '\'' +
                ", playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
