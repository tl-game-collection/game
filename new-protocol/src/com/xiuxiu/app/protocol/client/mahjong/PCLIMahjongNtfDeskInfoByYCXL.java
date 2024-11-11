package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfDeskInfoByYCXL extends PCLIMahjongNtfDeskInfo {
    public long curPlayerId;
    public short[] deskCard = new short[43];
    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public int queColor;        // 定缺颜色
        public boolean huanpai;
        public List<Long> huanPaiRecord = new ArrayList<>();
        public List<List> listHuInfo = new ArrayList<List>();
        public HashMap<Byte, HashMap<Byte, Integer>> tingInfo = new HashMap<>();
        public byte lastTakeCard;

        @Override
        public String toString() {
            return "DeskPlayerInfoByYCXL{" +
                    "queColor=" + queColor +
                    ",huanpai=" + huanpai +
                    ",huanPaiRecord=" + huanPaiRecord +
                    ",listHuInfo=" + listHuInfo +
                    ",tingInfo=" + tingInfo +
                    ",lastTakeCard=" + lastTakeCard +
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
        return "PCLIMahjongNtfDeskInfoByYCXL{" +
                "remainCard=" + remainCard +
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
                ", curPlayerId=" + curPlayerId +
                ", deskCard=" + deskCard +
                '}';
    }
}
