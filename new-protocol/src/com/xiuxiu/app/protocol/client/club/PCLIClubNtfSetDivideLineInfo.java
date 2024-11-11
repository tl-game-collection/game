package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfSetDivideLineInfo {
    public long clubUid;           // 群uid
    public long tagPlayerUid;       // 目标玩家uid
    public int divide;              // 分成比例，针对成员
    public int divideLine;          // 竞技场分成比例，针对一条线

    public PCLIClubNtfSetDivideLineInfo() {
    }

    public PCLIClubNtfSetDivideLineInfo(long clubUid, long tagPlayerUid, int divide, int divideLine) {
        this.clubUid = clubUid;
        this.tagPlayerUid = tagPlayerUid;
        this.divide = divide;
        this.divideLine = divideLine;
    }

    @Override
    public String toString() {
        return "PCLIClubNtfSetDivideLineInfo{" +
                "clubUid=" + clubUid +
                ", tagPlayerUid=" + tagPlayerUid +
                ", divide=" + divide +
                ", divideLine=" + divideLine +
                '}';
    }
}
