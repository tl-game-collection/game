package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetClubRobotNumResp extends ErrorMsg {
    public long clubUid;
    public long floorUid;
    public long playerUid;
    public int curRobotDesk2;// 当前已有2人场机器人桌子数量
    public int curRobotDesk3;// 当前已有3人场机器人桌子数量
    public int curRobotDesk4;// 当前已有4人场机器人桌子数量
    @Override
    public String toString() {
        return "GetClubRobotNumResp [clubUid=" + clubUid + ", floorUid=" + floorUid + ", playerUid=" + playerUid
            + ", curRobotDesk2=" + curRobotDesk2 + ", curRobotDesk3=" + curRobotDesk3 + ", curRobotDesk4="
            + curRobotDesk4 + ", ret=" + ret + ", msg=" + msg + ", toString()=" + super.toString() + ", getClass()="
            + getClass() + ", hashCode()=" + hashCode() + "]";
    }
    
    

   
}
