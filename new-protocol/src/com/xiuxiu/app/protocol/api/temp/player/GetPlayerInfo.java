package com.xiuxiu.app.protocol.api.temp.player;

/**
 *
 */
public class GetPlayerInfo {
    public long playerUid;      // 搜索玩家Uid
    public long referrerUid;    // 推荐人UID
    public int page;            // 当前页
    public int pageSize;        // 每页显示多少条
    public String sign;         // md5(playerUid + referrerUid + page + pageSize + key)

    @Override
    public String toString() {
        return "GetPlayerInfo{" +
                "playerUid=" + playerUid +
                ", referrerUid=" + referrerUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sign='" + sign + '\'' +
                '}';
    }
}
