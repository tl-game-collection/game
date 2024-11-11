package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfDeskInfoByCSMJ extends PCLIMahjongNtfDeskInfo {

    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public int zeng;
        public boolean isOutTake=false;

        @Override
        public String toString() {
            return "DeskPlayerInfo{" +
                    "zeng=" + zeng +
                    "isOutTake=" + isOutTake +
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
        return "PCLIMahjongNtfDeskInfoByCSMJ{" +
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
