package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetGoldInfo {
    public long clubUid;      //亲友圈uid
    public long playerUid;      //上分下分玩家uid
    public long optClubUid;  //从哪个圈进行的操作
    public int changeArenaValue; //修改值

    @Override
    public String toString() {
        return "PCLIGroupReqChangeArenaInfo{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", optClubUid=" + optClubUid +
                ", changeArenaValue=" + changeArenaValue +
                '}';
    }
}
