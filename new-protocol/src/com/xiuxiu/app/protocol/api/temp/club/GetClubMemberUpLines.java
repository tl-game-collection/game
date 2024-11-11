package com.xiuxiu.app.protocol.api.temp.club;

/**
 * 获取玩家上线一条线
 * @auther: luocheng
 * @date: 2020/1/5 10:36
 */
public class GetClubMemberUpLines {
    public long clubUid;
    public long playerUid;
    public String sign;//clubUid + playerUid + key

    @Override
    public String toString() {
        return "GetClubMemberUpLines{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
