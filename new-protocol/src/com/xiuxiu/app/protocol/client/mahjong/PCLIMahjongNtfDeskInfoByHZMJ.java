package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongNtfDeskInfoByHZMJ extends PCLIMahjongNtfDeskInfo {
    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public  byte lastFumbleCard=-1;//最后打出去的牌
        public HashMap<Byte/*takeCard*/, HashMap<Byte/*huCard*/, Integer>> tingInfo;//听信息
        @Override
        public String toString() {
            return "DeskPlayerInfo{" +
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
        return "PCLIMahjongNtfDeskInfoByHZMJ{" +
                "laiZi=" + laiZi +
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
