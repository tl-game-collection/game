package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetDivideLine {
    public long clubUid;           // 群uid
    public long tagPlayerUid;       // 目标玩家uid
    public int divide;              // 直属分成比例
    public int divideLine;          // 一条线分成比例

    @Override
    public String toString() {
        return "PCLIClubReqSetDivideLine{" +
                "clubUid=" + clubUid +
                ", tagPlayerUid=" + tagPlayerUid +
                ", divide=" + divide +
                ", divideLine=" + divideLine +
                '}';
    }
}
