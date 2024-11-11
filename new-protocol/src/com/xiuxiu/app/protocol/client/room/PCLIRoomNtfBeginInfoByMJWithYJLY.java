package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginInfoByMJWithYJLY extends PCLIRoomNtfBeginInfoByMJ {
    public byte chaoCard;
    public byte laiZi;


    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByMJWithYJLY{" +
                "chaoCard=" + chaoCard +
                ", laiZi=" + laiZi +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", myIndex=" + myIndex +
                ", myCards='" + myCards + '\'' +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
