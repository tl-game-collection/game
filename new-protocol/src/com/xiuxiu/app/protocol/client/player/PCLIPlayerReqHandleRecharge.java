package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqHandleRecharge {
    public long playerUid;        // 要充值的玩家Uid
    public long amount;           // 充值的金额（单位：分）;
    public int type;              // 充值类型 1:钱包; 2-星币

    @Override
    public String toString() {
        return "PCLIPlayerReqHandleRecharge{" +
                "playerUid=" + playerUid +
                ", amount=" + amount +
                ", type=" + type +
                '}';
    }
}
