package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;

public class PCLIRoomNtfBeginInfoByMJWithWHMJ extends PCLIRoomNtfBeginInfoByMJ  {
    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 癞子的牌
    public ArrayList<Byte> piList = new ArrayList<>();      // 皮的牌列表
    public boolean isBaoZiF = false;// 豹子翻倍
    public boolean isJFYLF = false; // 见风原癞翻倍
    public boolean isJ258F = false; // 见258将翻倍

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByMJWithWHMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                ", isBaoZiF=" + isBaoZiF +
                ", isJFYLF=" + isJFYLF +
                ", isJ258F=" + isJ258F +
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
