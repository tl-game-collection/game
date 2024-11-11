package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetClubInfo {
    public long clubUid;
    public String name;
    public String desc;
    public String icon;
    public String gameDesc;

    @Override
    public String toString() {
        return "PCLIClubReqSetClubInfo{" +
                "clubUid=" + clubUid +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", icon='" + icon + '\'' +
                ", gameDesc='" + gameDesc + '\'' +
                '}';
    }
}
