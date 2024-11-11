package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfDeskInfoByKWX extends PCLIMahjongNtfDeskInfo {
    public static class DeskPlayerInfoByKWX extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public List<Byte> brightCard = new ArrayList<>();       // 亮牌
        public List<Byte> laiZiAndPiCard = new ArrayList<>();   // 打出癞子和痞子的牌
        public int remainCard;
        public int flutter;
        public List<Integer> shuKan= new ArrayList<>();                                      // 数坎, 高16位258将, 低16为1-9点数
        public String totalScore;                               // 总分
        public byte fumble = -1;                                // 摸到牌
        public HashMap<Byte, Integer> pao = new HashMap<>();    // 炮牌
        public ArrayList<Byte> kou = new ArrayList<>();         // 扣牌

        @Override
        public String toString() {
            return "DeskPlayerInfo{" +
                    "cpgCard=" + cpgCard +
                    ", deskCard=" + deskCard +
                    ", brightCard=" + brightCard +
                    ", card=" + card +
                    ", laiZiAndPiCard='" + laiZiAndPiCard + '\'' +
                    ", remainCard=" + remainCard +
                    ", flutter=" + flutter +
                    ", shuKan=" + shuKan +
                    ", totalScore='" + totalScore + '\'' +
                    ", fumble=" + fumble +
                    ", pao=" + pao +
                    ", kou=" + kou +
                    '}';
        }
    }

    public byte chaoPai;            // 朝牌
    public byte laiZi;              // 癞子
    public boolean flutterWait = false; // 是否选飘等待中
    public int fumbleOnBarCnt = 0;         // 杠后摸牌的次数

    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskInfo{" +
                "remainCard=" + remainCard +
                ", other=" + other +
                ", allOnlineState=" + allOnlineState +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", chaoPai=" + chaoPai +
                ", laiZi=" + laiZi +
                ", flutterWait=" + flutterWait +
                ", fumbleOnBarCnt=" + fumbleOnBarCnt +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
