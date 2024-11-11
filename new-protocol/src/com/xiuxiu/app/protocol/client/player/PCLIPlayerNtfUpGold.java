package com.xiuxiu.app.protocol.client.player;

import java.util.List;

public class PCLIPlayerNtfUpGold {
    public static class TreasurerPlayer {
        public long playerUid;            //上分财务的id
        public String name;         //上分财务的name
        public String icon;         //上分财务的icon
        public long score;
        public String desc;         //上分财务描述

        public TreasurerPlayer() {

        }

        public TreasurerPlayer(long playerUid,String name,String icon,long score) {
            this.icon = icon;
            this.name = name;
            this.score = score;
            this.playerUid = playerUid;
        }

        @Override
        public String toString() {
            return "TreasurerPlayer{" +
                    "icon='" + icon + '\'' +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ",score=" + score +
                    ",playerUid" + playerUid +
                    '}';
        }
    }
    public long clubUid;                //申请上分的人亲友圈
    public long fromClubUid;    //上分财务所在亲友圈(合圈后只可能在主圈)
    public List<TreasurerPlayer> players;

    @Override
    public String toString() {
        return "PCLIPlayerNtfUpGold{" +
                "clubUid=" + clubUid +
                ", players='" + players + '\'' +
                ", fromClubUid=" + fromClubUid +
                '}';
    }
}
