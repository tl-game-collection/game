package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetTreasurer {
    public long clubUid;
    public long playerUid;
    public int type;    //1.上分财务 2.下分财务
    public boolean isSet;    //设置或者取消

    @Override
    public String toString() {
        return "PCLIClubReqSetTreasurer{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", type=" + type +
                ", isSet=" + isSet +
                '}';
    }
}
