package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxReqScoreInfo {
    public long clubUid;
    public long playerUid;//查看的玩家uid
    public int beforeDay;
    public int page;//当前页码
    public int size;//每页多少数据
    public int roomId = -1;// 房间id
    
    public int gameType;// 游戏大类型
    public int gameSubType;// 游戏小类型

    @Override
    public String toString() {
        return "PCLIBoxReqScoreInfo{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", beforeDay=" + beforeDay +
                ", page=" + page +
                ", size=" + size +
                ", roomId=" + roomId +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                '}';
    }
}
