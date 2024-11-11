package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfBlackJackBuyInsuranceResult {
    public long playerUid;                  // 玩家ID
    public boolean isBuyInsurance;          // 是否买保险

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackBuyInsuranceResult{" +
                "playerUid = " + playerUid +
                ",isBuyInsurance = " + isBuyInsurance +
                "}";
    }
}
