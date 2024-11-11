package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfQuickArenaValueRecharge {
    public String orderString;   //支付宝或微信需要的支付串
    public int payType;       //支付类型

    @Override
    public String toString() {
        return "PCLIPlayerNtfQuickArenaValueRecharge{" +
                "orderString='" + orderString + '\'' +
                ", payType=" + payType +
                '}';
    }
}
