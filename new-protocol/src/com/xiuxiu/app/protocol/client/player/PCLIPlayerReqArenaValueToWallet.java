package com.xiuxiu.app.protocol.client.player;

// 竞技值转钱包金额
public class PCLIPlayerReqArenaValueToWallet {
    public int transferAmount;      // 转换金额 * 100
    public long groupUid;           // 群ID

    @Override
    public String toString() {
        return "PCLIPlayerReqArenaValueToWallet{" +
                "transferAmount=" + transferAmount +
                ", groupUid=" + groupUid +
                '}';
    }
}
