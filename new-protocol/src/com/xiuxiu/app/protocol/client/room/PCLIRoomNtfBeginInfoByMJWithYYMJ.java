package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginInfoByMJWithYYMJ extends PCLIRoomNtfBeginInfoByMJ {
    public byte fangPai = -1;       // 翻的牌

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByMJWithYYMJ{" +
                "fangPai=" + fangPai +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", myIndex=" + myIndex +
                ", myCards=" + myCards +
                ", laiZi=" + laiZi +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
