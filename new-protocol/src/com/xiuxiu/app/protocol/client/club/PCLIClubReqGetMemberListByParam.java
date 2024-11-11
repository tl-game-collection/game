package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqGetMemberListByParam {
    public long clubUid;
    public int type;     //0.本圈 1.总圈
    public long param; //查找条件

    @Override
    public String toString() {
        return "PCLIClubReqGetMemberListByParam{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", param=" + param +
                '}';
    }
}
