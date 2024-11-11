package com.xiuxiu.app.protocol.api.temp.player;

// 获取群管理下级成员信息
public class GetClubMemberDownLine {
    public long clubUid;
    public long managerUid;
    public String sign; // md5(clubUid + managerUid + key)

    @Override
    public String toString() {
        return "GetClubMemberDownLine{" +
                "clubUid=" + clubUid +
                ", managerUid=" + managerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
