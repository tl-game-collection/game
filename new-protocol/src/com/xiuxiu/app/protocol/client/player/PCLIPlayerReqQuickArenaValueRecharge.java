package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqQuickArenaValueRecharge {
    public int payType;             // 支付方式 1: 钱包 2:支付宝 3:微信
    public int rechargeMoney;       // 充值金额 * 100
    public long groupUid;           // 群ID

    @Override
    public String toString() {
        return "PCLIPlayerReqQuickArenaValueRecharge{" +
                "payType=" + payType +
                ", rechargeMoney=" + rechargeMoney +
                ", groupUid=" + groupUid +
                '}';
    }
}
