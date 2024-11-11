package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfo;

import java.util.List;

public class PCLIRoomNtfBeginInfoByTDK extends PCLIRoomNtfBeginInfo {
    public boolean lanGuo;          // 上把烂锅与否
    public int myIndex;             // 我的索引
    public List<Byte> myCards;      // 我的手牌
    public int potPond;             // 底池

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByTDK{" +
                "lanGuo=" + lanGuo +
                ", myIndex=" + myIndex +
                ", myCards=" + myCards +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", potPond=" + potPond +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
