package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfDeskInfoByKDMJ extends PCLIMahjongNtfDeskInfo{
    public byte fangPai = -1;       // 翻的牌
    public byte laiZi = -1;         // 癞子的牌
    public boolean canOperate = false;  // 是否操作过(打牌、吃、碰、杠、仰)
    public List<Byte> piList = new ArrayList<>(); // 皮的牌列表
    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public int fumbleCnt;   // 摸牌次数
        public boolean ting;
        public  byte tingCardValue;//听牌值
        public  byte tingCardIndex;//听牌手牌上index
        public  byte desktingIndex;//听牌牌桌位置
        public  byte lastFumbleCard=-1;//最后打出去的牌
        public HashMap<Byte/*takeCard*/, HashMap<Byte/*huCard*/, Integer>> tingInfo;//听信息
        @Override
        public String toString() {
            return "DeskPlayerInfoByYXMJ{" +
                    "fumbleCnt=" + fumbleCnt +
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
        return "PCLIMahjongNtfDeskInfoByKDMJ{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                ", remainCard=" + remainCard +
                ", other=" + other +
                ", allOnlineState=" + allOnlineState +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", fumbleOnBarCnt=" + fumbleOnBarCnt +
                ", canOperate=" + canOperate +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
