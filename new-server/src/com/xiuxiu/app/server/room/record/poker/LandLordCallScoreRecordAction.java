package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class LandLordCallScoreRecordAction extends RecordAction {
    protected List<KeyValue<Long, Integer>> allCallScore = new ArrayList<>();
    protected List<KeyValue<Long, List<Byte>>> allHandCards = new ArrayList<>();

    public LandLordCallScoreRecordAction() {
        super(EActionOp.CALL_SCORE, -1);
    }

    public void addCallScore(long playerUid, int score) {
        this.allCallScore.add(new KeyValue<>(playerUid, score));
    }

    public void addHandCard(long playerUid, List<Byte> cards) {
        this.allHandCards.add(new KeyValue<>(playerUid, cards));
    }

    public void clear() {
        this.allCallScore.clear();
        this.allHandCards.clear();
    }

    public List<KeyValue<Long, Integer>> getAllCallScore() {
        return allCallScore;
    }

    public void setAllCallScore(List<KeyValue<Long, Integer>> allCallScore) {
        this.allCallScore = allCallScore;
    }
    
}
