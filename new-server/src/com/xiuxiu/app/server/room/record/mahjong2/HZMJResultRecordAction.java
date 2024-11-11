package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.ArrayList;
import java.util.List;

public class HZMJResultRecordAction extends ResultRecordAction {
    public static class ScoreInfo extends ResultRecordAction.ScoreInfo {
        protected int niaoScore;            // 鸟分
        protected int wypn;                 // 围一票鸟

        public int getNiaoScore() {
            return niaoScore;
        }

        public void setNiaoScore(int niaoScore) {
            this.niaoScore = niaoScore;
        }

        public int getWypn() {
            return wypn;
        }

        public void setWypn(int wypn) {
            this.wypn = wypn;
        }
    }

    public static class PlayerInfo extends ResultRecordAction.PlayerInfo {
    }

    private List<Byte> niaoList = new ArrayList<>();

    public HZMJResultRecordAction() {
        super();
    }

    public void addNiaoList(List<Byte> list) {
        this.niaoList.addAll(list);
    }

    public List<Byte> getNiaoList() {
        return niaoList;
    }

    public void setNiaoList(List<Byte> niaoList) {
        this.niaoList = niaoList;
    }
}
