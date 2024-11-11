package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class AddClubGameDeskResp extends ErrorMsg {
    public long clubUid;
    public long floorUid;
    public int type;//1.2人场 2.3人场
    public int robotDeskMin;    //最小桌子数
    public int robotDeskMax;    //最大桌子数
    public int randomTime;      //随机时间

    @Override
    public String toString() {
        return "AddClubGameDeskResp{" +
                "clubUid=" + clubUid +
                ", floorUid=" + floorUid +
                ", type=" + type +
                ", robotDeskMin=" + robotDeskMin +
                ", robotDeskMax=" + robotDeskMax +
                ", randomTime=" + randomTime +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
