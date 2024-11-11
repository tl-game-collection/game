package com.xiuxiu.app.protocol.client.player;
public class PCLIPlayerReqGetLeagueRecord {
    public long leagUid;//
    public int page;                // 分页, 从0开始
    public long oPlayerUid;         //otherPlayerUid 要查询的其他玩家uid

    @Override
    public String toString() {
        return "PCLIPlayerReqGetLeagueRecord{" +
                "leagUid=" + leagUid +
                ", page=" + page +
                ", oPlayerUid=" + oPlayerUid +
                '}';
    }
}
