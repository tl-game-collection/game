package com.xiuxiu.app.protocol.api;

public class GetPlayerMoneyExpendRecord {
    public long playerUid;
    public String sign; // med(playerUid, key)

    @Override
    public String toString() {
        return "GetPlayerMoneyExpendRecord{" +
                "playerUid=" + playerUid +
                "sign=" + sign +
                '}';
    }
}
