package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfWalletRechargeInfo {
    public String orderString;   //支付宝或微信需要的支付串
    public int payType;       //支付类型

    public PCLIPlayerNtfWalletRechargeInfo(){}

    public PCLIPlayerNtfWalletRechargeInfo(String orderString, int payType){
        this.orderString = orderString;
        this.payType = payType;
    }


    @Override
    public String toString() {
        return "PCLIPlayerNtfWalletRechargeInfo{" +
                "orderString='" + orderString + '\'' +
                ", payType=" + payType +
                '}';
    }
}
