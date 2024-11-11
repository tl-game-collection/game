package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqArenaValueWithdraw {
    public long groupUid;               // 群UID
    public int withdrawType;            // 提现方式
    public long withdrawMoney;          // 提现金额
    public String withdrawPasswd;       // 提现密码

    @Override
    public String toString() {
        return "PCLIPlayerReqArenaValueWithdraw{" +
                "groupUid=" + groupUid +
                ", withdrawType=" + withdrawType +
                ", withdrawMoney=" + withdrawMoney +
                ", withdrawPasswd='" + withdrawPasswd + '\'' +
                '}';
    }
}
