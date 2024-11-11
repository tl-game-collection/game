package com.xiuxiu.app.protocol.client.mahjong;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfDeskInfo extends PCLIRoomDeskInfo {
    public static class CardNode {
        public byte a = -1;
        public byte b = -1;
        public byte c = -1;
        public byte d = -1;
        public int type = -1; // 0: 碰, 1: 放杠, 2: 明杠 3: 暗杠, 4: 右(后)吃, 5: 左(前)吃, 6: 中吃, 7: 任意3张, 8: 癞子杠, 9: 皮子杠
        public long playerId;


        public void setBump(byte a) {
            this.type = 0;
            this.a = this.b = this.c = a;
        }

        public void setEat(byte a, byte b, byte c) {
            this.type = 1;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public void setBar(boolean dark, byte a) {
            this.type = dark ? 3 : 2;
            this.a = this.b = this.c = this.d = a;
        }

        @Override
        public String toString() {
            return "CardNode{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    ", d=" + d +
                    ", type=" + type +
                    ", playerId=" + playerId +
                    '}';
        }
    }

    public static class DeskPlayerInfo {
        public List<CardNode> cpgCard = new ArrayList<>();
        public List<Byte> deskCard = new ArrayList<>();         // 打出的牌
        public List<Byte> card = new ArrayList<>();             // 手牌
        public int remainCard;
        public String totalScore;                               // 总分
        public byte fumble = -1;                                // 摸到牌
        public boolean over = false;                            // 结束
        public List<Byte> huCard = new ArrayList<>();           // 胡列表
        /** 如果是当前玩家操作，断线后检查还能胡吗 如果不能胡了就不需要检查了false */
        public boolean needCheckHu = true;

        @Override
        public String toString() {
            return "DeskPlayerInfo{" +
                    "cpgCard=" + cpgCard +
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

    public int remainCard;
    public HashMap<Long, DeskPlayerInfo> other = new HashMap<>();
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态
    public int crap1;               // 色子一
    public int crap2;               // 色子二
    public int fumbleOnBarCnt = 0;  // 杠后摸牌的次数
    public byte laiZi = -1;         // 癞子的牌
    public long timeout;            // 当前操作超时剩余量

    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskInfo{" +
                "remainCard=" + remainCard +
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
