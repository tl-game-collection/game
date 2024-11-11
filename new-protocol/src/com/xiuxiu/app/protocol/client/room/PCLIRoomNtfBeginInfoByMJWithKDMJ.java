package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomNtfBeginInfoByMJWithKDMJ extends PCLIRoomNtfBeginInfoByMJ {
    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 赖子牌
    public List<Byte> piList = new ArrayList<>(); // 皮的牌列表

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByMJWithKDMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", myIndex=" + myIndex +
                ", myCards=" + myCards +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
