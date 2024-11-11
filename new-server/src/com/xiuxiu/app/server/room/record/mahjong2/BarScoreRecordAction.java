package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class BarScoreRecordAction extends RecordAction {
    protected HashMap<Long, Integer> barScore = new HashMap<>();

    public BarScoreRecordAction() {
        super(EActionOp.BAR_SCORE, -1);
    }

    public void addBarScore(long playerUid, int score) {
        int old = this.barScore.getOrDefault(playerUid, 0);
        this.barScore.put(playerUid, old + score);
    }

    public HashMap<Long, Integer> getBarScore() {
        return barScore;
    }

    public void setBarScore(HashMap<Long, Integer> barScore) {
        this.barScore = barScore;
    }
}
