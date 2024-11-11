package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomNtfBeginInfoByMJ extends PCLIRoomNtfBeginInfo {
    public int crap1;
    public int crap2;
    public int myIndex;
    public List<Byte> myCards = new ArrayList<>();
    public byte laiZi = -1;         // 癞子的牌

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByMJ{" +
                "crap1=" + crap1 +
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
