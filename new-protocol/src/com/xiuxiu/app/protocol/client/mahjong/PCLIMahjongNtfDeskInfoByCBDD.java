package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfDeskInfoByCBDD extends PCLIMahjongNtfDeskInfo {
    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public int zeng;        // 增值

        @Override
        public String toString() {
            return "DeskPlayerInfo{" +
                    "zeng=" + zeng +
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
                '}';
    }
}
