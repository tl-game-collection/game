package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubNtfApplyInfo {
    public long playerUid;
    public String icon;
    public String name;
    public int state;
    public String opName;
    public long opUid;
    public int memberType;
    public long opTime;

    @Override
    public String toString() {
        return "PCLIClubNtfApplyInfo{" +
                "playerUid=" + playerUid +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", opName='" + opName + '\'' +
                ", memberType=" + memberType +
                ", opTime=" + opTime +
                ", opUid=" + opUid +
                '}';
    }
}
