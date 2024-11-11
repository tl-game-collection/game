package com.xiuxiu.app.server.room.record.mahjong2;

public class YYMJResultRecordAction extends ResultRecordAction {
    public YYMJResultRecordAction() {
        super();
    }

    public static class ScoreInfo extends ResultRecordAction.ScoreInfo {
        protected int niaoScore;            // 鸟分

        public int getNiaoScore() {
            return niaoScore;
        }

        public void setNiaoScore(int niaoScore) {
            this.niaoScore = niaoScore;
        }
    }
}
