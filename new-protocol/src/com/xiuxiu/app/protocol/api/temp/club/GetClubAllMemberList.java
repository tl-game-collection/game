package com.xiuxiu.app.protocol.api.temp.club;

/**
 * 获取某个club所有玩家
 * @date 2020/1/10
 * @author luocheng
 */
public class GetClubAllMemberList {
    public long clubUid;
    public long playerUid;          //筛选的玩家id
    public long upLinePlayerUid;    //筛选的上级玩家id
    public int page;
    public int pageSize;
    public String sign;             // md5(clubUid + playerUid + upLinePlayerUid + page + pageSize + key)

    @Override
    public String toString() {
        return "GetClubAllMemberList{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", upLinePlayerUid=" + upLinePlayerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
