package com.xiuxiu.app.protocol.client.club;

public class PCLIPlayerReqGetClubGoldRecord {
    public long clubUid;
    public int page;                // 分页, 从0开始
    public long playerUid;         //otherPlayerUid 要查询的其他玩家uid

    @Override
    public String toString() {
        return "PCLIPlayerReqGetClubGoldRecord{" +
                "clubUid=" + clubUid +
                ", page=" + page +
                ", playerUid=" + playerUid +
                '}';
    }
}
