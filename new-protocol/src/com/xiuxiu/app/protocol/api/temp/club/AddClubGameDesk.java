package com.xiuxiu.app.protocol.api.temp.club;

public class AddClubGameDesk {
    public long playerUid;
    public long clubUid;
    public long floorUid;
    public int type; //1.2人场 2.3人场
    public int robotDeskMin;    //最小桌子数
    public int robotDeskMax;    //最大桌子数
    public int randomTime;      //随机时间
    public String sign;                     // md5(playerUid + clubUid + floorUid + type + robotDeskMin + robotDeskMax + randomTime + key)

    @Override
    public String toString() {
        return "AddClubGameDesk{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", floorUid=" + floorUid +
                ", type=" + type +
                ", robotDeskMin=" + robotDeskMin +
                ", robotDeskMax=" + robotDeskMax +
                ", randomTime=" + randomTime +
                ", sign='" + sign + '\'' +
                '}';
    }
}
