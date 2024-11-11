package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqGetMemberList {
    public long clubUid;
    public int page;
    public int pageSize;
    public boolean onlyDownLine; //只看下级

    @Override
    public String toString() {
        return "PCLIClubReqGetMemberList{" +
                "clubUid=" + clubUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", onlyDownLine=" + onlyDownLine +
                '}';
    }
}
