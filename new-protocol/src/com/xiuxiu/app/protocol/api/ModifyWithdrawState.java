package com.xiuxiu.app.protocol.api;

import java.sql.Timestamp;

public class ModifyWithdrawState {
    public long playerUid;                      // 玩家UID
    public int withdrawType;                    // 提现, 1: 微信, 2: 支付宝, 3: 银行卡
    public String account;                      // 提现账号
    public long withdrawAmount;                 // 提现金额, 单位：分
    public Timestamp applyTime;                 // 申请提现时间
    public int state;                           // 处理结果 0-申请中, 1-提现成功, 2-提现被拒绝
    public String sign;                         // md5(playerUid + withdrawType + account + withdrawAmount + applyTime + state + key)

    @Override
    public String toString() {
        return "ModifyWithdrawState{" +
                "playerUid=" + playerUid +
                ", withdrawType=" + withdrawType +
                ", account='" + account + '\'' +
                ", withdrawAmount=" + withdrawAmount +
                ", applyTime=" + applyTime +
                ", state=" + state +
                ", sign='" + sign + '\'' +
                '}';
    }
}
