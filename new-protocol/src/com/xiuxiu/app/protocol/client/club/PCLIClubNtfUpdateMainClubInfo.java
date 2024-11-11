package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfUpdateMainClubInfo {
    public long clubUid;
    public PCLIClubSingleInfo mainClubInfo;

    @Override
    public String toString() {
        return "PCLIClubNtfUpdateMainClubInfo{" +
                "newClubUid=" + clubUid +
                ", toClubInfo=" + mainClubInfo +
                '}';
    }
}
