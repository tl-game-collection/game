package com.xiuxiu.app.protocol.client.rank;

public class PCLIRankReqRankList {
    public int rankType;        // 排行榜类型 ERankType 0 圈游戏次数排行榜 1 圈游戏分数排行榜 2 圈游戏大赢家排行榜 3 大圈游戏次数排行榜 4 大圈游戏分数排行榜 5 大圈游戏大赢家排行榜
    public long fromUid;        // 对于类型的uid
    public int type;            // 0 今日排行榜 1 昨日排行榜 2 前日排行榜
    public int page;            // 从0开始

    @Override
    public String toString() {
        return "PCLIRankReqInfo{" +
                "rankType=" + rankType +
                ", fromUid=" + fromUid +
                ", type=" + type +
                ", page=" + page +
                '}';
    }
}
