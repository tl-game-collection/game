package com.xiuxiu.app.protocol.api.temp.club;

/**
 * 获取群玩家竞技值
 * @auther: luocheng
 * @date: 2019/12/27 10:40
 */
public class GetClubMemberGold {
    public long clubUid;
    public long playerUid;
    public String sign;         // md5(clubUid + playerUid + key)

    @Override
    public String toString() {
        return "GetClubMemberGold{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
