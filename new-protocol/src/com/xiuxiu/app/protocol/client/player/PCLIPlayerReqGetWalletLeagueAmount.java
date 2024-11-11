package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqGetWalletLeagueAmount {
    public long playerUid;        // 聊天对象Uid

    @Override
    public String toString() {
        return "PCLIPlayerReqGetWalletLeagueAmount{" +
                "playerUid=" + playerUid +
                '}';
    }
}
