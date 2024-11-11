package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfDeskInfoByYYMJ extends PCLIMahjongNtfDeskInfo {
    public byte fangPai = -1;       // 翻的牌

    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public boolean ting;

        @Override
        public String toString() {
            return "DeskPlayerInfo{" +
                    "ting=" + ting +
                    ", cpgCard=" + cpgCard +
                    ", deskCard=" + deskCard +
                    ", card=" + card +
                    ", remainCard=" + remainCard +
                    ", totalScore='" + totalScore + '\'' +
                    ", fumble=" + fumble +
                    ", over=" + over +
                    ", huCard=" + huCard +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskInfoByYYMJ{" +
                "fangPai=" + fangPai +
                ", remainCard=" + remainCard +
                ", other=" + other +
                ", allOnlineState=" + allOnlineState +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", fumbleOnBarCnt=" + fumbleOnBarCnt +
                ", laiZi=" + laiZi +
                ", timeout=" + timeout +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }


}
