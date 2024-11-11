package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqMineRedPacketSend {
    public long groupUid;       // 群Uid
    public int count;           // 红包个数
    public int mineMultiple;    // 埋雷倍数 * 10
    public int amount;          // 总金额
    public int mine;            // 埋雷数字

    @Override
    public String toString() {
        return "PCLIPlayerReqMineRedPacketSend{" +
                "groupUid=" + groupUid +
                ", count=" + count +
                ", mineMultiple=" + mineMultiple +
                ", amount=" + amount +
                ", mine=" + mine +
                '}';
    }
}
