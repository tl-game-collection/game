package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfValueChange {
    public long clubUid; //亲友圈uid
    public long pUid;  //玩家uid
    public long gold; //竞技值
    public long rv;   //奖励分
    public long addGold;   //添加或减少的竞技分

    @Override
    public String toString() {
        return "PCLIClubNtfValueChange{" +
                "clubUid=" + clubUid +
                ", pUid=" + pUid +
                ", gold=" + gold +
                ", rv=" + rv +
                ", addGold=" + addGold +
                '}';
    }
}
