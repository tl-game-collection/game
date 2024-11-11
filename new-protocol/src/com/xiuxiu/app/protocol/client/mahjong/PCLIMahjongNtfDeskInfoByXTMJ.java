package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfDeskInfoByXTMJ extends PCLIMahjongNtfDeskInfo {
    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 癞子的牌
    public List<Byte> piList = new ArrayList<>(); // 皮的牌列表
    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskInfoByXTMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                ", remainCard=" + remainCard +
                ", other=" + other +
                ", allOnlineState=" + allOnlineState +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", fumbleOnBarCnt=" + fumbleOnBarCnt +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
