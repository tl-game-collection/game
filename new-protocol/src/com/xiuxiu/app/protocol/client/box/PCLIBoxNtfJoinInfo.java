package com.xiuxiu.app.protocol.client.box;

import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;

public class PCLIBoxNtfJoinInfo {
    public long groupUid;
    public long boxUid;
    public int roomIndex;
    public int allPlayerCnt;
    public int gameType;
    public int gameSubType;
    public PCLIPlayerBriefInfo playerBriefInfo;

    @Override
    public String toString() {
        return "PCLIBoxNtfJoinInfo{" +
                "groupUid=" + groupUid +
                ", boxUid=" + boxUid +
                ", roomIndex=" + roomIndex +
                ", allPlayerCnt=" + allPlayerCnt +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                ", playerBriefInfo=" + playerBriefInfo +
                '}';
    }
}
