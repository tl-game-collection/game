package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfDeskInfoByMCMJ extends PCLIMahjongNtfDeskInfo {
    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 癞子的牌
    public List<Byte> piList = new ArrayList<>(); // 皮的牌列表
    public List<Integer> cunList = new ArrayList<>(); // 存的列表，0x04风翻，0x10将翻，0x40连宝翻，0x80豹子翻

    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskInfoByMCMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                ", cunList=" + cunList +
                ", remainCard=" + remainCard +
                ", other=" + other +
                ", allOnlineState=" + allOnlineState +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", fumbleOnBarCnt=" + fumbleOnBarCnt +
                ", laiZi=" + laiZi +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
