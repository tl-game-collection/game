package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomReqScoreRecordInfo {
    public int page;                    // 从0开始, 页码
    //public long groupUid;               // 群uid, -1: 为全部
    public int beforeDay;               //
    public boolean curRoom = false;     // 是否是当前房间
    public int gameType;// 游戏大类型
    public int gameSubType;// 游戏小类型
    @Override
    public String toString() {
        return "PCLIRoomReqScoreRecordInfo{" +
                "page=" + page +
                ", beforeDay=" + beforeDay +
                ", curRoom=" + curRoom +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                '}';
    }
}
