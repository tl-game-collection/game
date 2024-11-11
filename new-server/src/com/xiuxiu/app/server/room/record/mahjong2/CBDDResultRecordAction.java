package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.ArrayList;
import java.util.List;

public class CBDDResultRecordAction extends ResultRecordAction {
    public static class ScoreInfo extends ResultRecordAction.ScoreInfo {
        protected int niaoScore;            // 鸟分
        protected int zengScore;            // 增分

        public int getNiaoScore() {
            return niaoScore;
        }

        public void setNiaoScore(int niaoScore) {
            this.niaoScore = niaoScore;
        }

        public int getZengScore() {
            return zengScore;
        }

        public void setZengScore(int zengScore) {
            this.zengScore = zengScore;
        }
    }

    public static class PlayerInfo extends ResultRecordAction.PlayerInfo {
    }

    private List<Byte> niaoList = new ArrayList<>();

    public CBDDResultRecordAction() {
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
