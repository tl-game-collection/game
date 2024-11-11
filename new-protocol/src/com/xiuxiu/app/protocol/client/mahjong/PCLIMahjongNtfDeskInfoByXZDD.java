package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfDeskInfoByXZDD extends PCLIMahjongNtfDeskInfo {
    public static class DeskPlayerInfo extends PCLIMahjongNtfDeskInfo.DeskPlayerInfo {
        public int queColor;        // 定缺颜色

        @Override
        public String toString() {
            return "DeskPlayerInfo{" +
                    "queColor=" + queColor +
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
        return "PCLIMahjongNtfDeskInfoByXZDD{" +
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
