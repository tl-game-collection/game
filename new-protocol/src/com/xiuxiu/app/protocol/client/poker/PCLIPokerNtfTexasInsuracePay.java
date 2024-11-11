package com.xiuxiu.app.protocol.client.poker;
public class PCLIPokerNtfTexasInsuracePay {
	public boolean isSurpass;//是否被反超
	public long playerUid;//玩家uid
	public int value;//值
    @Override
    public String toString() {
        return "PCLIPokerNtfTexasInsuracePay{" +
        		"isSurpass=" + isSurpass +
                "playerUid=" + playerUid +
                "value=" + value +
                '}';
    }
}
