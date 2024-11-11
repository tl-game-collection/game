package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class BombScoreRecordAction extends RecordAction {
    protected HashMap<Long, Integer> bombScore = new HashMap<>();

    public BombScoreRecordAction() {
        super(EActionOp.BOMB_SCORE, -1);
    }

    public void addScore(long playerUid, int score) {
        int old = this.bombScore.getOrDefault(playerUid, 0);
        this.bombScore.put(playerUid, old + score);
    }

    public HashMap<Long, Integer> getBombScore() {
        return bombScore;
    }

    public void setBombScore(HashMap<Long, Integer> bombScore) {
        this.bombScore = bombScore;
    }
}
