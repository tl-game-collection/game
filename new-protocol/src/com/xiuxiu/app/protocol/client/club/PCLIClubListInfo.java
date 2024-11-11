package com.xiuxiu.app.protocol.client.club;



public class PCLIClubListInfo {
    public long clubUid;                                        // 俱乐部Uid
    public long ownerUid;                                       // 俱乐部拥有者ID
    public String name;                                         // 俱乐部名称
    public long parentUid;                                      // 上级uid
    public boolean isIn;                                        // 自己是否在此群中

    @Override
    public String toString() {
        return "PCLIClubSingleInfo{" +
                "clubUid=" + clubUid +
                ", ownerUid=" + ownerUid +
                ", name='" + name + '\'' +
                ", parentUid=" + parentUid +
                ", isIn=" + isIn +
                '}';
    }
}
