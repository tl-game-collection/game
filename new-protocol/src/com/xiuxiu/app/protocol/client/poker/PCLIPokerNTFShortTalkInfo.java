package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNTFShortTalkInfo {
    public long playerUid;                // 快捷语玩家uid
    public String talk;                  // 快捷语内容；

    @Override
    public String toString() {
        return "PCLIPokerNTFShortTalkInfo{" +
                "playerUid=" + playerUid +
                ", talk='" + talk + '\'' +
                '}';
    }
}
