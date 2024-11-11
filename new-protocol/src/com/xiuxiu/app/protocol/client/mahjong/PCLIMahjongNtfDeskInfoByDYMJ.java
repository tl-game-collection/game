package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;

public class PCLIMahjongNtfDeskInfoByDYMJ extends PCLIMahjongNtfDeskInfo {
    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        @Override
        public String toString() {
            return "DeskPlayerInfoByWHMJ{" +
                    "cpgCard=" + cpgCard +
                    ", deskCard=" + deskCard +
                    ", card=" + card +
                    ", remainCard=" + remainCard +
                    ", totalScore='" + totalScore + '\'' +
                    ", fumble=" + fumble +
                    ", huCard=" + huCard +
                    '}';
        }
    }

    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 癞子的牌
    public ArrayList<Byte> piList = new ArrayList<>();      // 皮的牌列表
    public boolean isBaoZiF = false;// 豹子翻倍
    public boolean isJFYLF = false; // 见风原癞翻倍
    public boolean isJ258F = false; // 见258将翻倍
    public HashMap<Long, Long> allShow = new HashMap<>();   // 所有牌桌显示

    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskInfoByWHMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                ", isBaoZiF=" + isBaoZiF +
                ", isJFYLF=" + isJFYLF +
                ", isJ258F=" + isJ258F +
                ", allShow=" + allShow +
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
