package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfDeskInfoByHSMJ extends PCLIMahjongNtfDeskInfo {
    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 癞子的牌
    public List<Byte> piList = new ArrayList<>(); // 皮的牌列表
    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public boolean yangPai; // 是否操作过(打牌、吃、碰、杠、仰)    改为(打完或者仰过牌之后不能仰)
        public int fumbleCnt;   // 摸牌次数
        public HashMap<Byte/*takeCard*/, HashMap<Byte/*huCard*/, Integer>> tingInfo;//听信息
        public int yangPaiCnt;  //仰次数


        @Override
        public String toString() {
            return "DeskPlayerInfoByHSMJ{" +
                    "yangPai=" + yangPai +
                    ", fumbleCnt=" + fumbleCnt +
                    ", cpgCard=" + cpgCard +
                    ", deskCard=" + deskCard +
                    ", card=" + card +
                    ", remainCard=" + remainCard +
                    ", totalScore='" + totalScore + '\'' +
                    ", fumble=" + fumble +
                    ", huCard=" + huCard +
                    '}';
        }
    }
    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskInfoByHSMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
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
