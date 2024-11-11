package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfFGFCompareResultInfo {
    public long winPlayerUid;                   // 比牌赢玩家uid
    public long lostPlayerUid;                  // 比牌输玩家uid
    public long initiaorPlayerUid = -1;         // 发起方玩家uid
    public int note = -1;                       // 下注
    public int compareWithLoser;                // 收到消息的玩家与输家进行比较的结果，-1：输，1：赢，0：未比较

    @Override
    public String toString() {
        return "PCLIPokerNtfFGFCompareResultInfo{" +
                "winPlayerUid=" + winPlayerUid +
                ", lostPlayerUid=" + lostPlayerUid +
                ", initiaorPlayerUid=" + initiaorPlayerUid +
                ", note=" + note +
                ", compareWithLoser=" + compareWithLoser +
                '}';
    }
}
