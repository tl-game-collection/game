package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfSetWalletWithdrawTypeStatus {
    public int withdrawType;        // 提现方式Uid

    @Override
    public String toString() {
        return "PCLIPlayerNtfSetWalletWithdrawTypeStatus{" +
                "withdrawType=" + withdrawType +
                '}';
    }
}
