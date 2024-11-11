package com.xiuxiu.app.protocol.client.poker;
public class PCLIPokerNtfTexasInsuraceBuy {
	public long playerUid;//玩家uid
	public int payValue;//赔付值
	public int buyValue;//购买值
	public boolean isbreakeven;
	public int surpassCardSize;//反超牌数量
	
    @Override
    public String toString() {
        return "PCLIPokerNtfTexasInsuraceBuy{" +
        		"playerUid=" + playerUid +
                "payValue=" + payValue +
                "buyValue=" + buyValue +
                "isbreakeven=" + isbreakeven +
                "surpassCardSize=" + surpassCardSize +
                '}';
    }
}
