package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfGetWalletLeagueAmount {
    public long playerUid;          // 查询的对象Uid
    public long wallet;             // 钱包金额（单位：分）
    public long league;             // 星币金额（单位：分）

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetWalletLeagueAmount{" +
                "playerUid=" + playerUid +
                ", wallet=" + wallet +
                ", league=" + league +
                '}';
    }
}
