package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqSetWalletWithdrawTypeStatus {
    public int withdrawType;        // 提现方式Uid

    @Override
    public String toString() {
        return "PCLIPlayerReqSetWalletWithdrawTypeStatus{" +
                "withdrawType=" + withdrawType +
                '}';
    }
}
