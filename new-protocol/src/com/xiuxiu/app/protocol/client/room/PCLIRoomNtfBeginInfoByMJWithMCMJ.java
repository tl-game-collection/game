package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomNtfBeginInfoByMJWithMCMJ extends PCLIRoomNtfBeginInfoByMJ {
    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 赖子牌
    public List<Byte> piList = new ArrayList<>(); // 皮的牌列表
    public List<Integer> cunList = new ArrayList<>(); // 存的列表，0x04风翻，0x10将翻，0x40连宝翻，0x80豹子翻

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByMJWithMCMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                ", cunList=" + cunList +
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
